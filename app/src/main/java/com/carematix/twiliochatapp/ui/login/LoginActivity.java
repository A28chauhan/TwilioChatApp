package com.carematix.twiliochatapp.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.SessionManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelListViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChatViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserListViewModel;
import com.carematix.twiliochatapp.bean.FetchUser;
import com.carematix.twiliochatapp.bean.User;
import com.carematix.twiliochatapp.bean.accesstoken.TokenResponse;
import com.carematix.twiliochatapp.bean.login.DroLanguage;
import com.carematix.twiliochatapp.bean.login.UserResult;
import com.carematix.twiliochatapp.data.model.LoggedInUser;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.FCMPreferences;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.listener.LoginListener;
import com.carematix.twiliochatapp.listener.TaskCompletionListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;
import com.carematix.twiliochatapp.service.RegistrationIntentService;
import com.carematix.twiliochatapp.ui.login.LoginViewModel;
import com.carematix.twiliochatapp.ui.login.LoginViewModelFactory;
import com.carematix.twiliochatapp.databinding.ActivityLoginBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;
import com.twilio.messaging.internal.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements LoginListener {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private ChatClientManager clientManager;
    SharedPreferences sharedPreferences;
    private static final Logger logger = Logger.getLogger(LoginActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        prefManager =new PrefManager(this);
        prefManager.clearPref();
        prefManager.setBooleanValue(PrefConstants.SPLASH_ACTIVE_SERVICE,true);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        deleteDb();

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final TextInputLayout textInputEmail=binding.textEmail;
        final TextInputLayout textInputPassword=binding.textPassword;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    textInputEmail.setError(getString(loginFormState.getUsernameError()));
                }else{
                    textInputEmail.setError(null);
                }
                if (loginFormState.getPasswordError() != null) {
                    textInputPassword.setError(getString(loginFormState.getPasswordError()));
                }else{
                    textInputPassword.setError(null);
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
                enableSignButton();


            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        TextWatcher afterTextChangedListenerPassword = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                enableSignButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListenerPassword);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if( enableSignButton() ){
                        loginWithNurse();
                    }

                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( enableSignButton() ) {
                    loginWithNurse();
                }
            }
        });
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);

    }

    public boolean enableSignButton(){

        String email =binding.username.getText().toString();
        String password = binding.password.getText().toString();

        loginViewModel.loginDataChanged(email,
                password);
        if ( !loginViewModel.loginDataChanged(email) ) {
            //  binding.textEmail.setError(getString(loginViewModel.getLoginFormState().getValue().getUsernameError()));
            binding.login.setBackground(getResources().getDrawable(R.drawable.btn_shape_fade));
            binding.login.setEnabled(false);
            return false;
        }else{
            binding.textEmail.setError(null);

        }
        if(!( password.length() >= 8)){
            binding.password.setError(getString(loginViewModel.getLoginFormState().getValue().getPasswordError()));
            binding.login.setBackground(getResources().getDrawable(R.drawable.btn_shape_fade));
            binding.login.setEnabled(false);
            return false;
        }else{
            binding.password.setError(null);
        }
        if (loginViewModel.loginDataChanged(email) && ( password.length() >= 8)){
            binding.login.setBackground(getResources().getDrawable(R.drawable.btn_shape));
            binding.login.setEnabled(true);
            return true;
        }
        binding.login.setBackground(getResources().getDrawable(R.drawable.btn_shape_fade));
        binding.login.setEnabled(false);
        return false;
    }

    public void deleteDb(){

        try {
            allViewModel.delete();
            channelViewModel.delete();
            userChannelViewModel.delete();
            userChatViewModel.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loginWithNurse(){
        binding.loading.setVisibility(View.VISIBLE);

        if(Utils.onNetworkChange(this)){
            apiService =null;
            String email =binding.username.getText().toString();
            String password = binding.password.getText().toString();
            apiService = ApiClient.getClient1().create(ApiInterface.class);
            Call<UserResult> call = apiService.loginHCM(email,password,Constants.X_DRO_SOURCE);
            call.enqueue(new Callback<UserResult>() {
                @Override
                public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                    try {
                        int code = response.raw().code();
                        if (code <= Constants.BAD_REQUEST) {

                            UserResult userResult = response.body();

                            if(userResult.getProgramUserId() != 0){
                                // UserResult userResult = response.body();
                                // if (userResult != null) {
                                userResultData(userResult);
                                // }
                            }else{
                                try {
                                    unloadProgress();

                                    binding.textPassword.setError("message");
                                    focusView = binding.textPassword;
                                    cancel = true;
                                    focusView.requestFocus();
                                } catch (Exception e) {
                                    unloadProgress();

                                    e.printStackTrace();
                                }
                            }
                        }
                        else{
                            unloadProgress();
                            Utils.showToast("Error : "+code,LoginActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        unloadProgress();
                    }
                }

                @Override
                public void onFailure(Call<UserResult> call, Throwable t) {
                    Utils.showToast(t.getMessage(),LoginActivity.this);
                    unloadProgress();
                }
            });
        }else{
            Utils.showToast(Utils.getStringResource(R.string.internet,LoginActivity.this),LoginActivity.this);
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    public UserListViewModel allViewModel;
    public UserChannelViewModel channelViewModel;
    public UserChannelListViewModel userChannelViewModel;
    public UserChatViewModel userChatViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        allViewModel = new ViewModelProvider(LoginActivity.this).get(UserListViewModel.class);
        channelViewModel = new ViewModelProvider(LoginActivity.this).get(UserChannelViewModel.class);
        userChannelViewModel = new ViewModelProvider(LoginActivity.this).get(UserChannelListViewModel.class);
        userChatViewModel = new ViewModelProvider(LoginActivity.this).get(UserChatViewModel.class);
        return super.onCreateView(name, context, attrs);
    }

    public void unloadProgress(){
        binding.loading.setVisibility(View.GONE);
    }
    private final String USERNAME_FORM_FIELD = "username";
    public void callMain(){
        unloadProgress();
        SessionManager.getInstance().createLoginSession(prefManager.getStringValue(PrefConstants.USER_NAME));
        prefManager.setBooleanValue(PrefConstants.IS_FIRST_TIME_LOGIN,true);
        prefManager.setBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK,true);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        LoginActivity.this.finish();
    }

    PrefManager prefManager;

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    String passwordEncrypt=null;
    User user;
    View focusView = null;
    ApiInterface apiService= null,apiService1=null;
    boolean cancel=false;

    private void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public void userResultData(UserResult userResult){
        try {
            prefManager = new PrefManager(LoginActivity.this);
            prefManager.setStringValue(PrefConstants.USER_ID,String.valueOf(userResult.getUserId()));
            prefManager.setStringValue(PrefConstants.PROGRAM_USER_ID,String.valueOf(userResult.getProgramUserId()));

            sharedPreferences.edit().putString("userName", userResult.getUserName()).commit();
            prefManager.setStringValue(PrefConstants.USER_NAME,userResult.getUserName());
            prefManager.setStringValue(PrefConstants.USER_FIRST_NAME,userResult.getFirstName());
            prefManager.setStringValue(PrefConstants.USER_LAST_NAME,userResult.getLastName());
            prefManager.setStringValue(PrefConstants.USER_IMAGE,userResult.getUserImage());

            prefManager.setStringValue(PrefConstants.PROGRAM_ID,String.valueOf(userResult.getProgramInfo().getProgramId()));
            prefManager.setStringValue(PrefConstants.ORGANIZATION_NAME,userResult.getProgramInfo().getOrganizationName());
            prefManager.setStringValue(PrefConstants.PROGRAM_NAME,userResult.getProgramInfo().getProgramName());
            prefManager.setStringValue(PrefConstants.LOGO_URL,userResult.getProgramInfo().getLogoUrl());

            prefManager.setBooleanValue(PrefConstants.IS_FIRST_TIME_LOGIN,userResult.isFirstLogin());

            fetchUser();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast("Please try again. ",LoginActivity.this);
        }

    }



    public void fetchUser(){

        if(Utils.onNetworkChange(this)){
            //apiService1=null;
            apiService1 = ApiClient.getClient1().create(ApiInterface.class);
            String programUserId =prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
            com.carematix.twiliochatapp.bean.fetchUser.FetchUser fetchUser=new com.carematix.twiliochatapp.bean.fetchUser.FetchUser(Integer.parseInt(programUserId),Constants.X_DRO_SOURCE);
            Call<FetchUser> call= apiService1.fetchUser(programUserId,Constants.X_DRO_SOURCE);
            call.enqueue(new Callback<FetchUser>() {
                @Override
                public void onResponse(Call<FetchUser> call, Response<FetchUser> response) {
                    int code = response.raw().code();
                    if(code == Constants.OK){
                        prefManager.setStringValue(PrefConstants.TWILIO_USER_SID,response.body().getData().getUserSid());
                        prefManager.setStringValue(PrefConstants.TWILIO_ROLE_ID,String.valueOf(response.body().getData().getRoleId()));
                        getToken();
                    }else{
                        unloadProgress();
                    }
                }

                @Override
                public void onFailure(Call<FetchUser> call, Throwable t) {
                    unloadProgress();
                }
            });
        }else{
            Utils.showToast(Utils.getStringResource(R.string.internet,LoginActivity.this),LoginActivity.this);
        }

    }

    public void getToken(){
        try {
            clientManager = TwilioApplication.get().getChatClientManager();

            clientManager.connectClient(new TaskCompletionListener<Void, String>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String fcmToken = sharedPreferences.getString(FCMPreferences.TOKEN_NAME,null);
                    Logs.d("fcmToken ","token :"+fcmToken);
                    SessionManager.getInstance().createLoginSession(USERNAME_FORM_FIELD);
                    TwilioApplication.get().getChatClientManager().setFCMToken(fcmToken);
                    callMain();
                }

                @Override
                public void onError(String errorMessage) {
                    unloadProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoginStarted()
    {
        try {
            logger.d("Log in started");
            binding.loading.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onLogoutFinished(){
        finishDialog();
        TwilioApplication.get().showToast("Log out finished");
    }

    public void finishDialog(){
        try {
            binding.loading.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}