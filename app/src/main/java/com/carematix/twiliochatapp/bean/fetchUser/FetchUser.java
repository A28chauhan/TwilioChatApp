package com.carematix.twiliochatapp.bean.fetchUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class FetchUser {

    @SerializedName("programUserId")
    @Expose
    private Integer programUserId;
    @SerializedName("device")
    @Expose
    private String device;

    public FetchUser(Integer programUserId, String device) {
        this.programUserId = programUserId;
        this.device = device;
    }

    public Integer getProgramUserId() {
        return programUserId;
    }

    public void setProgramUserId(Integer programUserId) {
        this.programUserId = programUserId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
