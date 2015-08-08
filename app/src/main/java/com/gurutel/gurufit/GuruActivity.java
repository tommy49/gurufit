package com.gurutel.gurufit;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by udnet_pc1 on 2015-08-07.
 */
public class GuruActivity extends Activity {
    public static final String TAG = "GuruFit";
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private boolean backGround = false;

    private Client mClient;
    private Recording recording;
    private History history;
    private Sensors sensors;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // moveTaskToBack(true);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        Log.i(TAG, "GuruActivity onCreate ");
        // Create the Google API Client
       getClient();
    }

    protected  void getClient(){
        mClient = new Client(this,
                new Client.Connection(){
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "GuruActivity onRestart .........["+mClient+"]");
        //getClient();
    }





    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API

        //if(authInProgress)
            backGround = moveTaskToBack(true);
        /*
        if(mClient.progressAuth){
            backGround = moveTaskToBack(false);
        }else {
            backGround = moveTaskToBack(true);
        }
       */

        Log.i(TAG, "GuruActivity  onStart Connecting... backGround :" + backGround);


      //  if (mClient == null) {
      //      Log.i(TAG, "onStart mClient connect");
      //  Log.i(TAG,"API Connect : ");
                mClient.connect();
      //  }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "GuruActivity onResume .........");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i(TAG, "GuruActivity onPause .........mClient Auth : "+mClient.progressAuth);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // if (sensors != null)
        //    sensors.unsubscribe();
        Log.i(TAG, "GuruActivity onStop...............");
        //if (recording != null) {
          //  Log.i(TAG, "onStop recording unsubscribe");
          //  recording.unsubscribe();
        //}

        if (mClient != null) {
           Log.i(TAG, "onStop mClient disconnect");
           mClient.disconnect();
        }
        finish();
        //this.finish();
    }

    protected void onDestory(){
        super.onDestroy();
        Log.i(TAG, "GuruActivity onDestory .........");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "GuruActivity onActivityResult");
        mClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }



}
