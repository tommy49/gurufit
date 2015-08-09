package com.gurutel.gurufit;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by tey3 on 15. 8. 6.
 */
public class AlarmRecever extends BroadcastReceiver {
    public static final String TAG = "GuruFit";

    private Client mClient;
    private Recording recording;
    private History history;
    private Sensors sensors;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        Log.e(TAG, "AlarmReceiver 111 Started.");

//        Intent i = new Intent(context,GuruActivity.class);
        Intent i = new Intent(context,GuruService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
        context.startService(i);
        Toast.makeText(context, "hi2", Toast.LENGTH_LONG).show();

    }
}