package com.carematix.twiliochatapp.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class Data {

    @SerializedName("roleId")
    @Expose
    private Integer roleId;
    @SerializedName("userSid")
    @Expose
    private String userSid;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getUserSid() {
        return userSid;
    }

    public void setUserSid(String userSid) {
        this.userSid = userSid;
    }
}
