package com.carematix.twiliochatapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.carematix.twiliochatapp.application.SessionManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.helper.FCMPreferences;
import com.carematix.twiliochatapp.helper.Logs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegistrationIntentService extends IntentService {


    public RegistrationIntentService(){
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Logs.d("onHandleIntent","intent"+intent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
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
                    if(SessionManager.getInstance().isLoggedIn()){
                        TwilioApplication.get().getChatClientManager().setFCMToken(token);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(FCMPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


}
