package com.gurutel.gurufit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

/**
 * Created by udnet_pc1 on 2015-08-07.
 */
public class GuruActivity extends Activity {
    public static final String TAG = "GuruFit";
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private Client mClient;
    private Recording recording;
    private History history;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moveTaskToBack(true);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        Log.i(TAG, "Connecting...");
        // Create the Google API Client
        mClient = new Client(this,
                new Client.Connection(){
                    @Override
                    public void onConnected() {
                        recording = new Recording(mClient.getClient());
                        recording.subscribe();

                        history = new History(mClient.getClient());
                        history.readBefore(new Date());

                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        moveTaskToBack(true);
        Log.i(TAG, "onStart Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // if (sensors != null)
        //    sensors.unsubscribe();
        Log.i(TAG, "onStop...............");
        if (recording != null)
            recording.unsubscribe();

        if (mClient != null)
            mClient.disconnect();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        mClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }
}
