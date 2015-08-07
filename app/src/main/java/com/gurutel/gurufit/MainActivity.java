package com.gurutel.gurufit;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
import android.content.Intent;
//import android.content.IntentSender;
import android.graphics.Color;
//import android.os.AsyncTask;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Scope;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.fitness.FitnessStatusCodes;
//import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.OnDataPointListener;
//import com.google.android.gms.fitness.result.ListSubscriptionsResult;

import com.gurutel.gurufit.common.logger.Log;
import com.gurutel.gurufit.common.logger.LogView;
import com.gurutel.gurufit.common.logger.LogWrapper;
import com.gurutel.gurufit.common.logger.MessageOnlyLogFilter;
/*
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
*/

//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
import java.util.Date;
import java.util.GregorianCalendar;
//import java.util.List;
//import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity  {
    public static final String TAG = "GuruFit";
   // private static final int REQUEST_OAUTH = 1;
   // private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    //private GoogleApiClient mClient = null;

   // private OnDataPointListener mListener;
  //  private OnDataPointListener mListener2;
  //  private OnDataPointListener mListener3;


    private AlarmManager mManager;
    private GregorianCalendar mCalendar;
    private NotificationManager mNotification;

    private Client mClient;
    private Recording recording;
    private History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.

        initializeLogging();

        //2015-08-06 added start
        mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mCalendar = new GregorianCalendar();
        Log.i(TAG, " ToDate : "+mCalendar.getTime().toString());

        Intent intent = new Intent(MainActivity.this, AlarmRecever.class);
        PendingIntent pender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pender);
        mManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 1 * 60000, pender); //1 minute
//        mManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 1 * 6000, pender); //6sec

        Log.i(TAG, "AlarmManger Register Date : "+mCalendar.getTime().toString());

       //2015-08-06 added  end
        /*
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
*/

      //  buildFitnessClient();


    }


   /*
    private void buildFitnessClient() {
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

*/

/*
                                new InsertAndVerifyDataTask().execute();

                                findFitnessDataLocation();

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
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
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }
    */

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(TAG, "onStart Connecting...");
      //  mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // if (sensors != null)
        //    sensors.unsubscribe();
/*
        if (recording != null)
            recording.unsubscribe();

        if (mClient != null)
            mClient.disconnect();
*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
      //  mClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
      //  outState.putBoolean(AUTH_PENDING, authInProgress);
    }

/*
    public void subscribeACTIVITYSAMPLE(){
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for TYPE_ACTIVITY_SAMPLE detected.");
                            } else {
                                Log.i(TAG, "Successfully TYPE_ACTIVITY_SAMPLE subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem TYPE_ACTIVITY_SAMPLE subscribing.");
                        }
                    }
                });

    }

    public void subscribeSTEPCOUNT(){
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for TYPE_STEP_COUNT_DELTA detected.");
                            } else {
                                Log.i(TAG, "Successfully TYPE_STEP_COUNT_DELTA subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem TYPE_STEP_COUNT_DELTA subscribing.");
                        }
                    }
                });

    }

    public void subscribeLOCATION(){
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_LOCATION_SAMPLE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for TYPE_LOCATION_SAMPLE detected.");
                            } else {
                                Log.i(TAG, "Successfully TYPE_LOCATION_SAMPLEsubscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem TYPE_LOCATION_SAMPLE subscribing.");
                        }
                    }
                });

    }

    public void subscribeDISTANCE(){
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_DISTANCE_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for TYPE_DISTANCE_DELTA detected.");
                            } else {
                                Log.i(TAG, "Successfully TYPE_DISTANCE_DELTA subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem TYPE_DISTANCE_DELTA subscribing.");
                        }
                    }
                });

    }

    private void dumpSubscriptionsList() {
        // [START list_current_subscriptions]
//        Fitness.RecordingApi.listSubscriptions(mClient, DataType.TYPE_ACTIVITY_SAMPLE)
        Fitness.RecordingApi.listSubscriptions(mClient)
                // Create the callback to retrieve the list of subscriptions asynchronously.
                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                    @Override
                    public void onResult(ListSubscriptionsResult listSubscriptionsResult) {
                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                            DataType dt = sc.getDataType();
                            Log.i(TAG, "Active subscription for data type: " + dt.getName());
                        }
                    }
                });
        // [END list_current_subscriptions]
    }

    private void cancelSubscription() {
        final String dataTypeStr = DataType.TYPE_ACTIVITY_SAMPLE.toString();
        Log.i(TAG, "Unsubscribing from data type: " + dataTypeStr);

        // Invoke the Recording API to unsubscribe from the data type and specify a callback that
        // will check the result.
        // [START unsubscribe_from_datatype]
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully unsubscribed for data type: " + dataTypeStr);
                        } else {
                            // Subscription not removed
                            Log.i(TAG, "Failed to unsubscribe for data type: " + dataTypeStr);
                        }
                    }
                });
        // [END unsubscribe_from_datatype]
    }


    private void findFitnessDataSources() {
        // [START find_data_sources]
        Log.i(TAG, "[START find_data_sources]");
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
               // .setDataTypes(DataType.TYPE_STEP_COUNT_CADENCE)
            //     .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
             //   .setDataTypes(DataType.TYPE_ACTIVITY_SAMPLE)
                        // Can specify whether data type is raw or derived.
             //   .setDataSourceTypes(DataSource.TYPE_RAW)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)

                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                                       @Override
                                       public void onResult(DataSourcesResult dataSourcesResult) {
                                           Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                                           Log.i(TAG, "getDataSources(): " + dataSourcesResult.getDataSources());
                                           for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                                               Log.i(TAG, "Data source found: " + dataSource.toString());
                                               Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
                                               Log.i(TAG, "Data Source getDevice: " + dataSource.getDevice());
                                               Log.i(TAG, "Data Source getDataType: " + dataSource.getDataType());
                                               Log.i(TAG, "DataType.TYPE_STEP_COUNT_DELTA:" + DataType.TYPE_STEP_COUNT_DELTA);
                                               //Let's register a listener to receive Activity data!

                                               if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)
                                                       && mListener == null) {
                                                   Log.i(TAG, "Data source for TYPE_STEP_COUNT_DELTA found!  Registering.");
                                                   registerFitnessDataListener(dataSource,
                                                           DataType.TYPE_STEP_COUNT_DELTA);
                                               }

                                           }
                                       }
                                   }
                );
        Log.i(TAG, "[END find_data_sources]");
    }

    private void findFitnessDataLocation() {
        // [START find_data_sources]
        Log.i(TAG, "[START find_data_Location]");
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                   .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                   .setDataSourceTypes(DataSource.TYPE_RAW)
 //               .setDataSourceTypes(DataSource.TYPE_DERIVED)

                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                                       @Override
                                       public void onResult(DataSourcesResult dataSourcesResult) {
                                           Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                                           //   Log.i(TAG, "getDataSources(): " + dataSourcesResult.getDataSources());
                                           for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                                               Log.i(TAG, "Data source found: " + dataSource.toString());
                                               Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
                                               //     Log.i(TAG, "Data Source getDevice: " + dataSource.getDevice());
                                               //    Log.i(TAG, "Data Source getDataType: " + dataSource.getDataType());
                                               //    Log.i(TAG,"DataType.TYPE_STEP_COUNT_DELTA:"+DataType.TYPE_LOCATION_SAMPLE);
                                               //Let's register a listener to receive Activity data!
                                               if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)
                                                       && mListener2 == null) {
                                                   Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                                   registerFitnessDataListener2(dataSource,
                                                           DataType.TYPE_LOCATION_SAMPLE);
                                               }

                                           }
                                       }
                                   }
                );
        Log.i(TAG, "[END find_data_sources]");
    }

    private void findFitnessDataPower() {
        // [START find_data_sources]
        Log.i(TAG, "[START find_data_Power]");
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_POWER_SAMPLE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                        //  .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                                       @Override
                                       public void onResult(DataSourcesResult dataSourcesResult) {
                                           Log.i(TAG, "Result 3: " + dataSourcesResult.getStatus().toString());
                                           Log.i(TAG, "Result 3 getDataSources(): " + dataSourcesResult.getDataSources());
                                           for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                                               Log.i(TAG, "Data source found: " + dataSource.toString());
                                               Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
                                               if (dataSource.getDataType().equals(DataType.TYPE_POWER_SAMPLE)
                                                       && mListener3 == null) {
                                                   Log.i(TAG, "Data source for TYPE_POWER_SAMPLE found!  Registering.");
                                                   registerFitnessDataListener3(dataSource,
                                                           DataType.TYPE_POWER_SAMPLE);
                                               }

                                           }
                                       }
                                   }
                );
        Log.i(TAG, "[END find_data_power]");
    }



    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {


        Log.i(TAG,"[START register_data_listener]");
        // [START register_data_listener]
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long endTime = cal.getTimeInMillis();
                Log.i(TAG,"[onDataPoint]"+endTime);
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
//                    Log.i(TAG, "Detected DataPoint value: " + val);
                    Log.i(TAG, "Detected DataPoint value: " + val.asInt());

                }
            }
        };

        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered!");
                        } else {
                            Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
        Log.i(TAG,"[END register_data_listener]");
        // [END register_data_listener]
    }

    private void registerFitnessDataListener2(DataSource dataSource, DataType dataType) {
        Log.i(TAG,"[START register_data_listener2]");
        // [START register_data_listener]
        mListener2 = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long endTime = cal.getTimeInMillis();
                Log.i(TAG,"[onDataPoint2]"+endTime);
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
//                    Log.i(TAG, "Detected DataPoint value: " + val);
                    Log.i(TAG, "Detected DataPoint value: " + val);

                }
            }
        };

        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(10, TimeUnit.SECONDS)
                        .build(),
                mListener2)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener2 registered!");
                        } else {
                            Log.i(TAG, "Listener2 not registered.");
                        }
                    }
                });
        Log.i(TAG,"[END register_data_listener2]");
        // [END register_data_listener]

    }


    private void registerFitnessDataListener3(DataSource dataSource, DataType dataType) {
        Log.i(TAG,"[START register_data_listener3]");
        // [START register_data_listener]
        mListener3 = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long endTime = cal.getTimeInMillis();
                Log.i(TAG,"[onDataPoint3]"+endTime);
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
//                    Log.i(TAG, "Detected DataPoint value: " + val);
                    Log.i(TAG, "Detected DataPoint value: " + val);

                }
            }
        };

        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(10, TimeUnit.SECONDS)
                        .build(),
                mListener3)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener3 registered!");
                        } else {
                            Log.i(TAG, "Listener3 not registered.");
                        }
                    }
                });
        Log.i(TAG, "[END register_data_listener3]");
        // [END register_data_listener]
    }



    private void unregisterFitnessDataListener() {
        if (mListener == null) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

        // [START unregister_data_listener]
        // Waiting isn't actually necessary as the unregister call will complete regardless,
        // even if called from within onStop, but a callback can still be added in order to
        // inspect the results.
        Fitness.SensorsApi.remove(
                mClient,
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener was removed!");
                        } else {
                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }

     */
  /*
    private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
           /*
            //First, create a new dataset and insertion request.
            DataSet dataSet = insertFitnessData();

            // [START insert_dataset]
            // Then, invoke the History API to insert the data and await the result, which is
            // possible here because of the {@link AsyncTask}. Always include a timeout when calling
            // await() to prevent hanging that can occur from the service being shutdown because
            // of low memory or other conditions.
            Log.i(TAG, "Inserting the dataset in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.HistoryApi.insertData(mClient, dataSet)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the dataset.");
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.i(TAG, "Data insert was successful!");
            // [END insert_dataset]
            */

  /*
            // Begin by creating the query.
            DataReadRequest readRequest = queryFitnessData();
            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            // [END read_dataset]
            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.
            printData(dataReadResult);

            /*
            DataReadRequest readRequest2 = queryLocationData();
            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResult2 =
                    Fitness.HistoryApi.readData(mClient, readRequest2).await(1, TimeUnit.MINUTES);
            // [END read_dataset]
            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.
            printData(dataReadResult2);
            */
/*
            return null;
        }
    }
*/
/*
    private DataSet insertFitnessData() {
        Log.i(TAG, "Creating a new data insert request");

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName(TAG + " - step count")
                .setType(DataSource.TYPE_RAW)
           //     .setType(DataSource.TYPE_DERIVED)
                .build();

        // Create a data set
        int stepCountDelta = 1000;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]

        return dataSet;
    }


    private DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

//        Log.i(TAG, " Time : " + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) +  "-" +   cal.get(Calendar.SECOND));
//        cal.add(Calendar.WEEK_OF_YEAR, -1);
     //   cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

      //  cal.add(Calendar.DAY_OF_YEAR, -1);
     //   long startTime = cal.getTimeInMillis();

//        long startTime = cal.getTimeInMillis() - ( cal.get(Calendar.HOUR_OF_DAY)  *  cal.get(Calendar.MINUTE) *  cal.get(Calendar.SECOND) * 1000);
        long startTime = cal.getTimeInMillis() - ( 10 * 60 * 1000);


        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);


        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));
        Log.i(TAG, "Range Start: " + startTime);
        Log.i(TAG, "Range End: " + endTime);



        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
             //   .aggregate(DataType.TYPE_STEP_COUNT_DELTA , DataType.TYPE_STEP_COUNT_DELTA)

                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
//                .bucketByTime(1, TimeUnit.DAYS)
                .bucketByTime(1, TimeUnit.MINUTES)
//                .bucketByTime(1, TimeUnit.HOURS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                .setTimeRange(startTime, endTime, TimeUnit.MINUTES)

                .build();
        // [END build_read_data_request]

        return readRequest;
    }

    private DataReadRequest queryLocationData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.add(Calendar.DAY_OF_YEAR, -1);

        long startTime = cal.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
//                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)

                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // [END build_read_data_request]

        return readRequest;
    }

    private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        Log.i(TAG, "dataSet.getDataPoints(): " + dataSet.getDataPoints());

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }
    // [END parse_dataset]

  /*
    private void deleteData() {
        Log.i(TAG, "Deleting today's step count data");

        // [START delete_dataset]
        // Set a start and end time for our data, using a start time of 1 day before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        //  Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        // Invoke the History API with the Google API client object and delete request, and then
        // specify a callback that will check the result.
        Fitness.HistoryApi.deleteData(mClient, request)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully deleted today's step count data");
                        } else {
                            // The deletion will fail if the requesting app tries to delete data
                            // that it did not insert.
                            Log.i(TAG, "Failed to delete today's step count data");
                        }
                    }
                });
        // [END delete_dataset]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_data) {
            deleteData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Initialize a custom log class that outputs both to in-app targets and logcat.
     */

    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = (LogView) findViewById(R.id.sample_logview);
        logView.setTextAppearance(this, R.style.Log);
        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready");
    }



}

