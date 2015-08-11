package com.gurutel.gurufit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by udnet_pc1 on 2015-08-11.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService{
    private static final String TAG = "GuruFit";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        Log.i(TAG,"[MyInstanceIDLS] onTokenRefresh().........");
    }
    // [END refresh_token]
}
