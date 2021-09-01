package com.carematix.twiliochatapp.fetchchannel.data;

import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.data.Result;

import java.util.HashMap;

import retrofit2.Response;

public
class FetchInDetails {


    String attendeUserID;
    Response<ChannelDetails> channelDetailsResponse;

    String errorMsg;

    HashMap<String, FetchInDetails>  hashMapFetchInDetails;

    public FetchInDetails(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public FetchInDetails(String attendeUserID, Response<ChannelDetails> channelDetailsResponse) {
        this.attendeUserID = attendeUserID;
        this.channelDetailsResponse = channelDetailsResponse;
    }

    public FetchInDetails(String attendeUserID, Response<ChannelDetails> channelDetailsResponse, HashMap<String, FetchInDetails> hashMapFetchInDetails) {
        this.attendeUserID = attendeUserID;
        this.channelDetailsResponse = channelDetailsResponse;
        this.hashMapFetchInDetails = hashMapFetchInDetails;
    }

    public HashMap<String, FetchInDetails> getHashMapFetchInDetails() {
        return hashMapFetchInDetails;
    }

    public void setHashMapFetchInDetails(HashMap<String, FetchInDetails> hashMapFetchInDetails) {
        this.hashMapFetchInDetails = hashMapFetchInDetails;
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
