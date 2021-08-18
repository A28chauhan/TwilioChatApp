package com.carematix.twiliochatapp.ui.login;

import com.carematix.twiliochatapp.bean.login.UserResult;

import retrofit2.Response;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI
    public Response<UserResult> userResultResponse;

    public LoggedInUserView(Response<UserResult> userResultResponse) {
        this.userResultResponse = userResultResponse;
    }

    public Response<UserResult> getUserResultResponse() {
        return userResultResponse;
    }

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}