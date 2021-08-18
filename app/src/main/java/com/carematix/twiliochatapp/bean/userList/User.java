package com.carematix.twiliochatapp.bean.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class User {

    @SerializedName("droUserId")
    @Expose
    private Integer droUserId;
    @SerializedName("droUserRoleId")
    @Expose
    private Integer droUserRoleId;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("droProgramUserId")
    @Expose
    private Integer droProgramUserId;

    public Integer getDroUserId() {
        return droUserId;
    }

    public void setDroUserId(Integer droUserId) {
        this.droUserId = droUserId;
    }

    public Integer getDroUserRoleId() {
        return droUserRoleId;
    }

    public void setDroUserRoleId(Integer droUserRoleId) {
        this.droUserRoleId = droUserRoleId;
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

    public Integer getDroProgramUserId() {
        return droProgramUserId;
    }

    public void setDroProgramUserId(Integer droProgramUserId) {
        this.droProgramUserId = droProgramUserId;
    }
}
