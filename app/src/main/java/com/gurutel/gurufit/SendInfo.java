package com.gurutel.gurufit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.content.Context;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.SERIAL;


/**
 * Created by tey3 on 15. 8. 9.
 */
public class SendInfo extends AsyncTask<String,Void,Boolean> {
        public static final String TAG = "GuruFit";
        String smsText = "";
    protected void onPreExecute() {
        Log.i(TAG,"SendInfo AsyncTask onPreExecute");
        // somethings

    }

    protected Boolean doInBackground(String... url) {
        // URL Connection
        Log.i(TAG,"SendInfo AsyncTask doInBackground");

        HttpURLConnection urlConnection = null;
        URL mURL = null;

//        String sUrl="http://211.115.217.70/GuruFit/SetInfo.php";
        String sUrl="http://data.udnet.co.kr/SetInfo.php";

        try{
          //Log.i(TAG,"SendInfo AsyncTask sUrl:"+sUrl);
            mURL = new URL(sUrl);
            urlConnection = (HttpURLConnection) mURL.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
//            urlConnection.setConnectTimeout(5000);  //connection timeout 5sec....
            urlConnection.setConnectTimeout(15000);  //connection timeout 15sec....

            Log.i(TAG, "SendInfo AsyncTask SET POST");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("PhoneNumber", MyGlobals.getInstance().getmMyPhoneNumber()));
            params.add(new BasicNameValuePair("StepCount", MyGlobals.getInstance().getmStepCount()));
            params.add(new BasicNameValuePair("StepStartDate", MyGlobals.getInstance().getmStepStartDate()));
            params.add(new BasicNameValuePair("StepEndDate", MyGlobals.getInstance().getmStepEndDate()));
            params.add(new BasicNameValuePair("Lon", MyGlobals.getInstance().getmLon()));
            params.add(new BasicNameValuePair("Lat", MyGlobals.getInstance().getmLat()));
            params.add(new BasicNameValuePair("Accuracy", MyGlobals.getInstance().getmAccuracy()));
            params.add(new BasicNameValuePair("Serial", SERIAL));

            smsText = MyGlobals.getInstance().getmStepCount()+","+MyGlobals.getInstance().getmLon()+","+MyGlobals.getInstance().getmLat()+","+MyGlobals.getInstance().getmAccuracy();

            Log.i(TAG,"SendInfo AsyncTask params:"+params);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));

            Log.i(TAG,"SendInfo AsyncTask write params:"+params);

            writer.flush();
            writer.close();
            os.close();


            urlConnection.connect();
            if( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                Log.i(TAG,"SendInfo AsyncTask HTTP_OK :"+urlConnection.getResponseCode());
            }else{
                Log.i(TAG,"SendInfo AsyncTask ResponseCode"+urlConnection.getResponseCode());
            }

            urlConnection.disconnect();
            Log.i(TAG, "SendInfo AsyncTask HttpURL Disconnect()");

           // sendSMS("01098955259",smsText);

            return true;

        }catch(Exception e){
            Log.d( TAG, e.toString());
            return false;
        }

    }

    protected void onPostExecute (Boolean result) {
        Log.i(TAG,"SendInfo AsyncTask onPostExeute");
        // UI Update
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            if(pair.getValue()==null){
                result.append("");
            }else {
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }
           // Log.i(TAG,"SendInfo getQuery result : "+result.toString());
        }

        return result.toString();
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }


}

