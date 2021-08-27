package com.carematix.twiliochatapp.fetchchannel.data;

import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;

import retrofit2.Response;

public
class FetchInDetails {


    String attendeUserID;
    Response<ChannelDetails> channelDetailsResponse;

    String errorMsg;

    public FetchInDetails(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public FetchInDetails(String attendeUserID, Response<ChannelDetails> channelDetailsResponse) {
        this.attendeUserID = attendeUserID;
        this.channelDetailsResponse = channelDetailsResponse;
    }

    public void setAttendeUserID(String attendeUserID) {
        this.attendeUserID = attendeUserID;
    }

    public void setChannelDetailsResponse(Response<ChannelDetails> channelDetailsResponse) {
        this.channelDetailsResponse = channelDetailsResponse;
    }

    public String getAttendeUserID() {
        return attendeUserID;
    }

    public Response<ChannelDetails> getChannelDetailsResponse() {
        return channelDetailsResponse;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
