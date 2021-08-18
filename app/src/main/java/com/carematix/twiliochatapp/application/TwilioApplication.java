package com.carematix.twiliochatapp.application;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.carematix.twiliochatapp.helper.Logs;
import com.google.firebase.FirebaseApp;
import com.twilio.chat.ErrorInfo;

public
class TwilioApplication extends Application {

    private static TwilioApplication instance;
    private ChatClientManager basicClient;
    boolean isVisible = false;

    public static TwilioApplication get() {
        return instance;
    }

    public ChatClientManager getChatClientManager() {
        return this.basicClient;
    }

    public static final String TAG = "TwilioChat";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            //Crashlytics.someAction();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TwilioApplication.instance = TwilioApplication.this;
            basicClient = new ChatClientManager(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(final String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public void showToast(final String text, final int duration){
        Log.d("TwilioApplication", text);
        if(isVisible){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            });
        }
    }

    public void showError(final ErrorInfo error)
    {
        showError("Something went wrong", error);
    }

    public void showError(final String message, final ErrorInfo error){
        showToast(formatted(message, error), Toast.LENGTH_LONG);
        logErrorInfo(message, error);
    }

    public void logErrorInfo(final String message, final ErrorInfo error){
        Logs.e("TwilioApplication", formatted(message, error));
    }

    private String formatted(String message, ErrorInfo error){
        return String.format("%s. %s", message, error.toString());
    }
}
