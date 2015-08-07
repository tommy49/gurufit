package com.gurutel.gurufit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;


/**
 * Created by tey3 on 15. 8. 6.
 */
public class AutoStartUp extends BroadcastReceiver {
    public static final String TAG = "BasicSensorsApi";
    private AlarmManager mManager;

    @Override
    public void onReceive(Context ctxt, Intent i) {

        Log.e(TAG, "REBoot Completed onReceive.");

        mManager = (AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctxt, AlarmRecever.class);
        PendingIntent pender = PendingIntent.getBroadcast(ctxt, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 1*60000, pender);

        Log.e(TAG, "REBoot Completed.  Alarm Manager Restart.");

    }



}
