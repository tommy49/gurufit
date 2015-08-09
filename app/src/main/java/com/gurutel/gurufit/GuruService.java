package com.gurutel.gurufit;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by udnet_pc1 on 2015-08-07.
 */
public class GuruService extends Service {
    public static final String TAG = "GuruFit";
    private ClientService mClient;
    private Recording recording;
    private History history;
    private Sensors sensors;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "GuruService onCreate");
        // Create the Google API Client

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        Log.i(TAG, "GuruService onStart");
        mClient = new ClientService(this,
                new ClientService.Connection(){
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "API Connected..... ["+mClient+"]");

                        sensors = new Sensors(mClient.getClient(),
                                new Sensors.DatasourcesListener() {
                                    @Override
                                    public void onDatasourcesListed() {
                                        Log.i(TAG,"datasources listed");
                                        ArrayList<String> datasources = sensors.getDatasources();
                                        for (String d:datasources) {
                                            Log.i(TAG, d);
                                        }
                                    }
                                }
                        );

                        sensors.listDatasourcesAndSubscribe();

                        recording = new Recording(mClient.getClient());
                        recording.subscribe();

                        history = new History(mClient.getClient());
                        history.readBefore(new Date());
                    }
                });
        mClient.connect();
        mClient.disconnect();
        this.stopSelf();

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG,"GuruService is destroy");

    }




}
