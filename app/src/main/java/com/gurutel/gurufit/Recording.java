package com.gurutel.gurufit;

/**
 * Created by udnet_pc1 on 2015-08-07.
 */

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;


public class Recording {
    public static final String TAG = "GuruFit";
    private GoogleApiClient client;

    public Recording(GoogleApiClient googleApiClient) {
        this.client = googleApiClient;
    }

    public void subscribe() {
        subscribe(DataType.TYPE_ACTIVITY_SAMPLE);
        subscribe(DataType.TYPE_STEP_COUNT_DELTA);
        subscribe(DataType.TYPE_LOCATION_SAMPLE);
        subscribe(DataType.TYPE_DISTANCE_DELTA);
    }

    public void subscribe(final DataType dataType) {
        Fitness.RecordingApi.subscribe(client, dataType)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected ["+dataType.getName()+"]");
                            } else {
                                Log.i(TAG, "Successfully subscribed! ["+dataType.getName()+"]");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing. ["+dataType.getName()+"]");
                        }
                    }
                });
    }



    public void listSubscriptions() {
//        Fitness.RecordingApi.listSubscriptions(client, DataType.TYPE_STEP_COUNT_DELTA).setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
            Fitness.RecordingApi.listSubscriptions(client).setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
              @Override
            public void onResult(ListSubscriptionsResult listSubscriptionsResult) {
                for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                    DataType dt = sc.getDataType();
                    Log.i(TAG,"found subscription for data type: " + dt.getName());
                }
            }
        });
    }

    public void unsubscribe() {

        Fitness.RecordingApi.unsubscribe(client, DataType.TYPE_ACTIVITY_SAMPLE).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Successfully unsubscribed for data type: " +  DataType.TYPE_ACTIVITY_SAMPLE.toString());
                } else {
                    Log.i(TAG, "Failed to unsubscribe for data type: " +  DataType.TYPE_ACTIVITY_SAMPLE.toString());
                }
            }
        });

        Fitness.RecordingApi.unsubscribe(client, DataType.TYPE_STEP_COUNT_DELTA).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Successfully unsubscribed for data type: " + DataType.TYPE_STEP_COUNT_DELTA.toString());
                } else {
                    Log.i(TAG, "Failed to unsubscribe for data type: " + DataType.TYPE_STEP_COUNT_DELTA.toString());
                }
            }
        });

        Fitness.RecordingApi.unsubscribe(client, DataType.TYPE_LOCATION_SAMPLE).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Successfully unsubscribed for data type: " + DataType.TYPE_LOCATION_SAMPLE.toString());
                } else {
                    Log.i(TAG, "Failed to unsubscribe for data type: " + DataType.TYPE_LOCATION_SAMPLE.toString());
                }
            }
        });

        Fitness.RecordingApi.unsubscribe(client, DataType.TYPE_DISTANCE_DELTA).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Successfully unsubscribed for data type: " + DataType.TYPE_DISTANCE_DELTA.toString());
                } else {
                    Log.i(TAG, "Failed to unsubscribe for data type: " + DataType.TYPE_DISTANCE_DELTA.toString());
                }
            }
        });

    }
}
