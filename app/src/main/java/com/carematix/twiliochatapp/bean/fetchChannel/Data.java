package com.carematix.twiliochatapp.bean.fetchChannel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
   class Data   {

    @SerializedName("channelSid")
    @Expose
    private String channelSid;

    public String getChannelSid() {
        return channelSid;
    }

    public void setChannelSid(String channelSid) {
        this.channelSid = channelSid;
    }
}
