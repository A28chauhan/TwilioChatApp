package com.carematix.twiliochatapp.bean.login;

import com.carematix.twiliochatapp.bean.login.ProgramInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserResult implements Serializable {

    @SerializedName("userName")
    @Expose
    String userName;
    @SerializedName("firstName")
    @Expose
    String firstName;
    @SerializedName("lastName")
    @Expose
    String lastName;
    @SerializedName("userImage")
    @Expose
    String userImage;
    @SerializedName("lastVisitedDate")
    @Expose
    long lastVisitedDate;
    @SerializedName("programUserId")
    @Expose
    long programUserId;
    @SerializedName("userId")
    @Expose
    long userId;
    @SerializedName("programInfo")
    @Expose
    ProgramInfo programInfo;

    @SerializedName("token")
    @Expose
    String token;
    @SerializedName("firstLogin")
    @Expose
    boolean firstLogin;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public long getLastVisitedDate() {
        return lastVisitedDate;
    }

    public void setLastVisitedDate(long lastVisitedDate) {
        this.lastVisitedDate = lastVisitedDate;
    }

    public long getProgramUserId() {
        return programUserId;
    }

    public void setProgramUserId(long programUserId) {
        this.programUserId = programUserId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ProgramInfo getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(ProgramInfo programInfo) {
        this.programInfo = programInfo;
    }

}
