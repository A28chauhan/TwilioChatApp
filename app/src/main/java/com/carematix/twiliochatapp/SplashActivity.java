package com.carematix.twiliochatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.SessionManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.databinding.ActivitySplashBinding;
import com.carematix.twiliochatapp.listener.LoginListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.service.RegistrationIntentService;
import com.carematix.twiliochatapp.ui.login.LoginActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.twilio.messaging.internal.Logger;

public
class SplashActivity extends AppCompatActivity{

    PrefManager prefManager;
    ActivitySplashBinding activitySplashBinding;
    private static final Logger logger = Logger.getLogger(SplashActivity.class);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());

        prefManager =new PrefManager(this);
        prefManager.setBooleanValue(PrefConstants.SPLASH_ACTIVE_SERVICE,true);

        try {
            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }

            // sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
            //  String token = sharedPreferences.getString(FCMPreferences.TOKEN_NAME,null);
//            if(token != null && !token.equals("")){
//                TwilioApplication.get().getChatClientManager().unRegisterFCMToken(token);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    startActivity(new Intent(SplashActivity.this ,
                            ((SessionManager.getInstance().isLoggedIn()) ? MainActivity.class :LoginActivity.class)));
                    SplashActivity.this.finish();
//       1-Sep             if(SessionManager.getInstance().isLoggedIn()){
//                        callMain();
//                    }else {
//                        startActivity(new Intent(c.this, LoginActivity.class));
//                        SplashActivity.this.finish();
//                    }
                }

            }
        });
        t.start();
    }
//   1-Sep    public void callMain(){
//        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//        SplashActivity.this.finish();
//    }

    private static final int  PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean checkPlayServices()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int                   resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                logger.i("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
