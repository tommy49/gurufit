package com.gurutel.gurufit;

/**
 * Created by udnet_pc1 on 2015-08-07.
 */


import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;

public class Client {
    public static final String TAG = "GuruFit";

    private static final int REQUEST_OAUTH = 1;

    private GoogleApiClient client;
    private boolean authInProgress = false;

    public interface Connection {
        public void onConnected();
    }
    private Connection connection;

//    public Client(final Activity activity, final Connection connection) {
    public Client(final Activity activity, final Connection connection) {

            this.connection = connection;
        client = new GoogleApiClient.Builder(activity)
                .addApi(Fitness.CONFIG_API)
                .addApi(Fitness.SESSIONS_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected");
                                connection.onConnected();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                Log.i(TAG, "Connection suspended");
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost. Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost. Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), activity, 0).show();
                                    return;
                                }

                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(activity, REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.i(TAG, "Exception while starting resolution activity: " + e.getMessage());
                                    }
                                }
                            }
                        }
                )
                .build();
    }

    public void connect() {
        client.connect();
    }

    public void disconnect() {
        if (client.isConnected()) {
            client.disconnect();
        }
    }

    //        disable should be called only for revoking authorization in GoogleFit
    public void revokeAuth() {
        PendingResult<Status> pendingResult = Fitness.ConfigApi.disableFit(client);
    }

    public GoogleApiClient getClient() {
        return client;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            Log.i(TAG, "onActivityResult: REQUEST_OAUTH");
            authInProgress = false;
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!client.isConnecting() && !client.isConnected()) {
                    Log.i(TAG, "onActivityResult: client.connect()");
                    client.connect();
                }
            }
        }
    }

}
