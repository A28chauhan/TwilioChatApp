package com.carematix.twiliochatapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.helper.FCMPreferences;
import com.carematix.twiliochatapp.helper.Logs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twilio.messaging.internal.Logger;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final Logger logger = Logger.getLogger(RegistrationIntentService.class);

    private static final String[] TOPICS = { "global" };

    public RegistrationIntentService()
    {
        super("RegistrationIntentService");
        logger.i("Stared");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Logs.d("onHandleIntent","intent"+intent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            String token = intent.getStringExtra(FCMPreferences.SENT_TOKEN_TO_SERVER);
            if(token == null || token.equals("")){
               // token = FirebaseInstanceId.getInstance().getToken();
            }

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Logs.d("RegistrationIntentService", "Fetching FCM registration token failed"+ task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    sharedPreferences.edit().putString(FCMPreferences.TOKEN_NAME, token).commit();
                    TwilioApplication.get().getChatClientManager().setFCMToken(token);
                }
            });
            Logs.d("FCM Register : ","FCM Registration Token: " + token);

            /**
             * Persist registration to Twilio servers.
             */
            TwilioApplication.get().getChatClientManager().setFCMToken(token);

            sharedPreferences.edit().putString(FCMPreferences.TOKEN_NAME, token).commit();

            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(FCMPreferences.SENT_TOKEN_TO_SERVER, true).commit();
        } catch (Exception e) {
            logger.e("Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration
            // data, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(FCMPreferences.SENT_TOKEN_TO_SERVER, false).commit();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(FCMPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    private void subscribeTopics(String token) throws IOException
    {
        // for (String topic : TOPICS) {
        //     FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+topic);
        // }
    }
}
