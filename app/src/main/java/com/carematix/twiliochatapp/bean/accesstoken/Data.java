package com.carematix.twiliochatapp.bean.accesstoken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class Data {

    @SerializedName("identity")
    @Expose
    private String identity;
    @SerializedName("token")
    @Expose
    private String token;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
