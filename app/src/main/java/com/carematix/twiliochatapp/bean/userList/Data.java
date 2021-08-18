package com.carematix.twiliochatapp.bean.userList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public

class Data {

    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
