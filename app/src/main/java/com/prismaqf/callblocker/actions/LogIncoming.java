package com.prismaqf.callblocker.actions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.prismaqf.callblocker.CallHelper;
import com.prismaqf.callblocker.sql.DbHelper;
import com.prismaqf.callblocker.sql.LoggedCallProvider;
import com.prismaqf.callblocker.sql.ServiceRunProvider;

/**
 * Class to log an incoming call
 * @author ConteDiMonteCristo
 */
@AvailableAction(description = "Logs events to DB")
public class LogIncoming implements IAction{

    private final static String TAG = LogIncoming.class.getCanonicalName();
    private final static String DESCRIPTION = "Logs events to DB";


    @Override
    public void act(final Context ctx, final String number, final LogInfo info) {
        //start a thread to write to DC
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Recording a call received in DB");
                SQLiteDatabase db = new DbHelper(ctx).getWritableDatabase();
                try {
                    String contactDescription = CallHelper.resolveContactDescription(ctx,number);
                    LoggedCallProvider.LoggedCall lc = new LoggedCallProvider.LoggedCall(info.getRunId(),info.getAction(),number,contactDescription);
                    LoggedCallProvider.InsertRow(db, lc);
                    ServiceRunProvider.UpdateWhileRunning(db, info.getRunId(), info.getNumReceived(), info.getNumTriggered());
                }
                finally {
                    db.close();
                }
            }
        }).start();
    }

    @Override
    public String toString() {
        return DESCRIPTION;
    }

}
