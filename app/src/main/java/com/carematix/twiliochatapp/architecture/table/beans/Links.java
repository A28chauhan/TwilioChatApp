package com.carematix.twiliochatapp.architecture.table.beans;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class Links {

    @SerializedName("user_channels")
    @Expose
    private String userChannels;
    @SerializedName("user_bindings")
    @Expose
    private String userBindings;

    public Links(String userChannels, String userBindings) {
        this.userChannels = userChannels;
        this.userBindings = userBindings;
    }

    public String getUserChannels() {
        return userChannels;
    }



    public void setUserChannels(String userChannels) {
        this.userChannels = userChannels;
    }

    public String getUserBindings() {
        return userBindings;
    }

    public void setUserBindings(String userBindings) {
        this.userBindings = userBindings;
    }

}
