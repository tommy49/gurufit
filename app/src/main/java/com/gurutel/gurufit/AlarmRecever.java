package com.gurutel.gurufit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by tey3 on 15. 8. 6.
 */
public class AlarmRecever extends BroadcastReceiver {
    public static final String TAG = "BasicSensorsApi";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        Log.e(TAG, "AlarmReceiver Started.");

        Toast.makeText(context, "hi", Toast.LENGTH_LONG).show();

    }
}