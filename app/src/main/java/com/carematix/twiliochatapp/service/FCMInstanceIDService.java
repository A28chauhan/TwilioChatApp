package com.carematix.twiliochatapp.service;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.carematix.twiliochatapp.helper.FCMPreferences;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.twilio.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;

public
class FCMInstanceIDService extends FirebaseMessagingService {

    private static final Logger logger = Logger.getLogger(FCMInstanceIDService.class);
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onNewToken(@NonNull String s) {
       // super.onNewToken(s);

        Logs.d("onTokenRefresh","new Token :"+s);

        // Fetch updated Instance ID token and notify our app's server of any changes.
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(FCMPreferences.SENT_TOKEN_TO_SERVER,s.toString());
        startService(intent);
    }
}
