package com.gurutel.gurufit;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

//import com.gurutel.gurufit.common.logger.Log;
//import com.gurutel.gurufit.common.logger.LogView;
//import com.gurutel.gurufit.common.logger.LogWrapper;
//import com.gurutel.gurufit.common.logger.MessageOnlyLogFilter;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
//import java.util.List;
//import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity  {

    public static final String TAG = "GuruFit";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;






    private AlarmManager mManager;
    private GregorianCalendar mCalendar;

    private Client mClient;
    private Recording recording;
    private History history;
    private Sensors sensors;

    private TimerTask mTask;
    private Timer mTimer;
    private TextView stepText;
    private String stepCount;
    private long sTime1;
    private long sTime2;
    GuruSQLDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //GPS 가 꺼져있을때..
            alertCheckGPS();
        }



        TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String phoneNum = telManager.getLine1Number();

        //GlobalApp globalApp = (GlobalApp) getApplication();
        //globalApp.setmMyPhoneNumber(phoneNum);
        MyGlobals.getInstance().setmMyPhoneNumber(phoneNum);

        db = new GuruSQLDB(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        stepText = (TextView)findViewById(R.id.stepView);
        stepText.setText(db.getStep());
  //      initializeLogging();

        Log.i(TAG, "MainActivity  Phone Number : "+phoneNum);

        //2015-08-06 added start
     //   mNotification = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);


        mCalendar = new GregorianCalendar();
        Log.i(TAG, " ToDate : " + mCalendar.getTime().toString());


        resetAlarm(0,0);

//        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pender);
        //2015-08-06 added  end

       //2015-08-11 added start
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                   // mInformationTextView.setText(getString(R.string.gcm_send_message));
                    Log.i(TAG, "[GCM] Token retrieved and sent to server! You can now use gcmsender to send downstream messages to this app.");
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                    Log.i(TAG, "[GCM] An error occurred while either fetching the InstanceID token sending the fetched token to the server or subscribing to the PubSub topic. Please try running the sample again.");
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.i(TAG,"checkPlayServices()  OK......");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.i(TAG, "GCM RegisterationIntentService start.......");

        }

        //2015-08-11 added end

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

     //   Log.i(TAG, "MainActivity onCreate  STEP : " + MyGlobals.getInstance().getmStepCount());

      //
        mTask = new TimerTask() {
            @Override
            public void run() {
                final Runnable timerAction = new Runnable() {
                    @Override
                    public void run() { //textview post를 처리 하기 위한 Action.
                        Log.i(TAG, "TimerTask run executed............................");
                       // Log.i(TAG, "MainActivity TimerTask run : " + MyGlobals.getInstance().getmStepCount());
                        stepText.setText(MyGlobals.getInstance().getmStepCount());
                        sTime2 = System.currentTimeMillis();
                        Log.i(TAG, "Now Time sTime2 : " + sTime2 +"   TimeDiff : " + (sTime2 - sTime1));

                        if((sTime2 - sTime1) > ( 3 * 60000)) {
                            buildFitnessClient();
                        }

                    }
                };
                //Log.i(TAG, "MainActivity stepText.post................... ");
                stepText.post(timerAction);

            }
        };

        //Log.i(TAG, "MainActivity onCreate  STEP : " + MyGlobals.getInstance().getmStepCount());
        mTimer = new Timer();
        //mTimer.schedule(mTask, 1000);
        mTimer.schedule(mTask, 5000, 10000);

    }

    private void alertCheckGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("위치 제공 서비스 기능이 꺼져 있습니다. 위치제공 서비스 이용을 사용하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("위치제공 서비스 사용",
                     new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            moveConfigGPS();
                        }
                     })
                .setNegativeButton("위치제공 서비스 사용안함",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void moveConfigGPS() {
        Intent gpsOptionIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionIntent);
    }


    private void buildFitnessClient() {

        Log.i(TAG, "Connecting...");

        sTime1 = System.currentTimeMillis();
        Log.i(TAG, "Now Time sTime1 : " + sTime1 );

        // Create the Google API Client
        mClient = new Client(this,
                    new Client.Connection(){
                        @Override
                        public void onConnected() {

                         try {
                            Log.i(TAG,"FusedLocationApi");
                            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient.getClient());
                                 Log.i(TAG, "Location Latitude : " + mLastLocation.getLatitude());
                                 MyGlobals.getInstance().setmLat(String.valueOf(mLastLocation.getLatitude()));
                                 Log.i(TAG, "Location Longtitude : " + mLastLocation.getLongitude());
                                 MyGlobals.getInstance().setmLon(String.valueOf(mLastLocation.getLongitude()));
                                 Log.i(TAG, "Location Accuracy : " + mLastLocation.getAccuracy());
                                 MyGlobals.getInstance().setmAccuracy(String.valueOf(mLastLocation.getAccuracy()));

                                 if(mLastLocation.getAccuracy() > 200){
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


                                     Log.i(TAG, "Sensors Subscribe start.....");
                                     sensors.listDatasourcesAndSubscribe();
                                     Log.i(TAG, "Sensors Subscribe End.....");

                                 }

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
                        }
                    });
        mClient.connect();
        mClient.disconnect();



    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "[checkPlayServices]This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        // Connect to the Fitness API
        Log.i(TAG, "MainActiviey   onRestart .....................");
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(TAG, "MainActiviey onStart Connecting...");
        buildFitnessClient();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Connect to the Fitness API
        Log.i(TAG, "MainActiviey   onResume .....................");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "MainActiviey   onPause .....................");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        // Connect to the Fitness API


   //     moveTaskToBack(true);
    }

    private void setAlarm(int curAlarm, int typeAlarm){
        if(typeAlarm==0) {
            mManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000 * 5, 10 * 60000, pendingIntent(curAlarm, typeAlarm)); //6sec
           // mManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 10000 ,  30000, pendingIntent(curAlarm, typeAlarm)); //6sec

        }else{
            mManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000 , 60000, pendingIntent(curAlarm, typeAlarm)); //6sec
        }
        Log.i(TAG, "AlarmManger Register Date : " + mCalendar.getTime().toString());
    }


    private PendingIntent pendingIntent(int curAlarm,int typeAlarm) {
        Intent intent;
        PendingIntent pender;
        if(typeAlarm==0) {
            intent = new Intent(MainActivity.this, AlarmRecever.class);
            pender = PendingIntent.getBroadcast(MainActivity.this, curAlarm, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
            intent = new Intent(getApplicationContext(), MainActivity.class);
            pender = PendingIntent.getActivity(MainActivity.this, curAlarm, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pender;
    }

    private void resetAlarm(int curAlarm,int typeAlarm) {
        mManager.cancel(pendingIntent(curAlarm, typeAlarm));
    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "MainActiviey   onStop .....................");

        //if (mClient != null)
        //    mClient.disconnect();

        // resetAlarm(1,1);
        if(mTimer != null)
           mTimer.cancel();

         setAlarm(0,0);
         finish();

    }

    protected void onDestory() {
        Log.i(TAG, "MainActiviey   onDestory .....................");
        super.onDestroy();
        // Connect to the Fitness API
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "MainActiviey onActivityResult");
        mClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //if(id == R.id.map_view){
        //    startActivity(new Intent(this, MapViewActivity.class));
        //}

        if(id == R.id.list_view){
            startActivity(new Intent(this, CustomerListActivity.class));
        }


        return super.onOptionsItemSelected(item);


    }
/*
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

*/

}

