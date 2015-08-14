package com.gurutel.gurufit;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.content.Intent;

import com.google.android.gms.location.LocationServices;

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

        MyGlobals.getInstance().setmMyPhoneNumber(intent.getStringExtra("phoneNum"));
        Log.i(TAG, "GuruService Intent Phone Number : " + intent.getStringExtra("phoneNum"));
        mClient = new ClientService(this,
                new ClientService.Connection(){
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "API Connected..... ["+mClient+"]");

                        try {
                            Log.i(TAG,"FusedLocationApi");
                            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient.getClient());
                                Log.i(TAG, "Location Latitude : " + mLastLocation.getLatitude());
                                MyGlobals.getInstance().setmLat(String.valueOf(mLastLocation.getLatitude()));
                                Log.i(TAG, "Location Longtitude : " + mLastLocation.getLongitude());
                                MyGlobals.getInstance().setmLon(String.valueOf(mLastLocation.getLongitude()));
                                Log.i(TAG, "Location Accuracy : " + mLastLocation.getAccuracy());
                                MyGlobals.getInstance().setmAccuracy(String.valueOf(mLastLocation.getAccuracy()));
                        }catch(Exception e){
                            MyGlobals.getInstance().setmLat("0");
                            MyGlobals.getInstance().setmLon("0");
                            MyGlobals.getInstance().setmAccuracy("0");
                            e.printStackTrace();
                        }

                        recording = new Recording(mClient.getClient());
                        recording.subscribe();
                        history = new History(mClient.getClient());
                        history.readBefore(new Date());




                        /*
                        sensors = new Sensors(mClient.getClient(),
                                new Sensors.DatasourcesListener() {
                                    @Override
                                    public void onDatasourcesListed() {
                                        Log.i(TAG,"Sensor datasources listed");
                                        ArrayList<String> datasources = sensors.getDatasources();
                                        for (String d:datasources) {
                                            Log.i(TAG, d);
                                        }
                                    }
                                }
                        );



                        sensors.listDatasourcesAndSubscribe();
                        */
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
