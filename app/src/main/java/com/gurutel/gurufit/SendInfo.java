package com.gurutel.gurufit;

import android.os.AsyncTask;
import android.util.Log;

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


/**
 * Created by tey3 on 15. 8. 9.
 */
public class SendInfo extends AsyncTask<String,Void,Boolean> {
        public static final String TAG = "GuruFit";

    protected void onPreExecute() {
        Log.i(TAG,"SendInfo AsyncTask onPreExecute");
        // somethings

    }

    protected Boolean doInBackground(String... url) {
        // URL Connection
        Log.i(TAG,"SendInfo AsyncTask doInBackground");

        HttpURLConnection urlConnection = null;
        URL mURL = null;

        String sUrl="http://211.115.217.106/test/test.php";

        try{
            Log.i(TAG,"SendInfo AsyncTask sUrl:"+sUrl);
            mURL = new URL(sUrl);
            urlConnection = (HttpURLConnection) mURL.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);  //connection timeout 5sec....

            Log.i(TAG,"SendInfo AsyncTask SET POST");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("PhoneNumber", "01098955259"));
            params.add(new BasicNameValuePair("StepCount", "999"));
            params.add(new BasicNameValuePair("Lon", "128.1234"));

            Log.i(TAG,"SendInfo AsyncTask OutputStream");

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
            Log.i(TAG,"SendInfo AsyncTask HttpURL Disconnect()");

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
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}

