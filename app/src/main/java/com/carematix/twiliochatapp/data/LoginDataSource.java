package com.carematix.twiliochatapp.data;

import com.carematix.twiliochatapp.bean.User;
import com.carematix.twiliochatapp.bean.login.UserResult;
import com.carematix.twiliochatapp.data.model.LoggedInUser;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    ApiInterface apiService= null;
    public Result<LoggedInUser> login(User user){
        // handle login
        try {
        apiService = ApiClient.getClient().create(ApiInterface.class);
        String timeZone =user.getTimeZone();
        String language = user.getLanguage();
        Call<UserResult> call = apiService.postUserDetails(Constants.CONTENT_TYPE, Constants.X_DRO_SOURCE,timeZone,language, user);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
               // return new Result.Success<>(fakeUser);
                try {
                    int code = response.raw().code();
                    if (code<= Constants.BAD_REQUEST) {
                        if(response.body() != null){
                            LoggedInUser loggedInUser =new LoggedInUser(response);
                            //return new Result.Success<>(loggedInUser);
                            loginInSuccessDetails(loggedInUser);
                        }else{
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                String message = jObjError.getString("message");
                                LoggedInUser loggedInUser =new LoggedInUser(message);
                                loginInFailureDetails(loggedInUser);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else if(code == Constants.INTERNAL_SERVER_ERROR){
                        login(user);
                    }else{
                        loginInFailureDetails(new LoggedInUser("Error : "+code));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                LoggedInUser loggedInUser =new LoggedInUser(t.getMessage());
                loginInFailureDetails(loggedInUser);
                //return new Result.Success<>(loggedInUser);
            }
        });
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
      //  LoggedInUser loggedInUser=new LoggedInUser("");
       // new Result.Failure<>(loggedInUser);
        return null;
    }

    private Result<LoggedInUser> loginInSuccessDetails(LoggedInUser loggedInUser){
        return new Result.Success<>(loggedInUser);
    }

    private Result<LoggedInUser> loginInFailureDetails(LoggedInUser loggedInUser){
        return new Result.Failure<>(loggedInUser);
    }
}