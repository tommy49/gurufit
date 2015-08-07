package com.gurutel.gurufit;

import android.app.Service;
import android.os.IBinder;
import android.util.Log;
import android.content.Intent;

import java.util.Date;

/**
 * Created by udnet_pc1 on 2015-08-07.
 */
public class GuruService extends Service {
    public static final String TAG = "GuruFit";
    private Client mClient;
    private Recording recording;
    private History history;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "GuruService is Create");
        Log.i(TAG, "Connecting...");
        // Create the Google API Client

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.i(TAG,"GuruService is started");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG,"GuruService is destroy");

    }
}
