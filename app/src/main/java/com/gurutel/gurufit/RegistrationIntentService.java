package com.gurutel.gurufit;

/**
 * Created by udnet_pc1 on 2015-08-11.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.os.Build.SERIAL;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "GuruFit";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, " [RegIntentService] GCM Registration Token: " + token);

                // TODO: Implement this method to send any registration to your app's servers.
                sendRegistrationToServer(token);

                // Subscribe to topic channels
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
            }
        } catch (Exception e) {
            Log.d(TAG, "[RegIntentService] Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        Log.i(TAG,"GCM_User sendRegistrationToServer");

        HttpURLConnection urlConnection = null;
        URL mURL = null;
        String email=null;

        String sUrl="http://211.115.217.70/GuruFit/GCM_User.php";

        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            //Log.d(TAG, "GCM_User ACCOUNT Name : " + account.name);
            if (emailPattern.matcher(account.name).matches()) {
                email = account.name;
             //   Log.d(TAG, "GCM_User email : " + email);
            }
        }

        try{
            mURL = new URL(sUrl);
            urlConnection = (HttpURLConnection) mURL.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(5000);  //connection timeout 5sec....

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("PhoneNumber", MyGlobals.getInstance().getmMyPhoneNumber()));
            params.add(new BasicNameValuePair("Serial", SERIAL));
            params.add(new BasicNameValuePair("Email", email));
            params.add(new BasicNameValuePair("Token", token));

            Log.i(TAG,"GCM_User params:"+params);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));

            writer.flush();
            writer.close();
            os.close();


            urlConnection.connect();
            if( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK ){
                Log.i(TAG," GCM_USER SUCCESS :"+urlConnection.getResponseCode());
            }else{
                Log.i(TAG," GCM_USER ERROR"+urlConnection.getResponseCode());
            }

            urlConnection.disconnect();
            Log.i(TAG," GCM_USER HttpURL Disconnect()");

        }catch(Exception e){
            Log.d( TAG, e.toString());
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

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

}
