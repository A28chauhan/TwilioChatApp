package com.carematix.twiliochatapp.listener;

public
interface LoginListener {

    void onLoginStarted();
    void onLoginFinished();
    void onLoginError(String errorMessage);
    void onLogoutFinished();
}
