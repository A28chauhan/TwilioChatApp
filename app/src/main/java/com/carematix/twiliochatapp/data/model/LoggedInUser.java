package com.carematix.twiliochatapp.data.model;

import com.carematix.twiliochatapp.bean.login.UserResult;

import retrofit2.Response;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser extends Object{

    private String userId;
    private String displayName;

    private String error;

    private Response<UserResult> resultResponse;

    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public LoggedInUser(String error) {
        this.error = error;
    }

    public LoggedInUser(Response<UserResult> resultResponse) {
        this.resultResponse = resultResponse;
    }

    public Response<UserResult> getResultResponse() {
        return resultResponse;
    }

    public void setResultResponse(Response<UserResult> resultResponse) {
        this.resultResponse = resultResponse;
    }

    public String getError() {
        return error;
    }



    public void setError(String error) {
        this.error = error;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}