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
import com.carematix.twiliochatapp.ui.login.LoginActivity;

public
class SplashActivity extends AppCompatActivity implements LoginListener {

    PrefManager prefManager;
    private ChatClientManager chatClientManager;

    ActivitySplashBinding activitySplashBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_splash);
        activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());

        try {
            prefManager =new PrefManager(this);
            chatClientManager = TwilioApplication.get().getChatClientManager();

        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager.setBooleanValue(PrefConstants.SPLASH_ACTIVE_SERVICE,true);
        Thread t =new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    //wait(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //prefManager.setBooleanValue(PrefConstants.LOGIN_ACTIVE,false);
                    if(SessionManager.getInstance().isLoggedIn()){
                        callMain();
                    }else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        SplashActivity.this.finish();
                    }


                }

            }
        });
        t.start();
    }


    @Override
    public void onLoginStarted()
    {

    }

    public void callMain(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    public void onLoginFinished()
    {
        try {
            finishDialog();
            callMain();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoginError(String errorMessage)
    {
        try {
            finishDialog();
            TwilioApplication.get().showToast("Error logging in : " + errorMessage, Toast.LENGTH_LONG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLogoutFinished()
    {
        finishDialog();
        TwilioApplication.get().showToast("Log out finished");
    }

    public void finishDialog(){
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
