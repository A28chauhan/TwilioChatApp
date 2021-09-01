package com.carematix.twiliochatapp.fetchchannel.bean;

import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.bean.login.UserResult;
import com.carematix.twiliochatapp.fetchchannel.data.FetchInDetails;

import java.util.HashMap;

import retrofit2.Response;

public
class FetchChannelView {

    String channelSId;
    public Response<ChannelDetails> userResultResponse;

    public HashMap<String, FetchInDetails> fetchInDetails;

    public FetchChannelView(String channelSId) {
        this.channelSId = channelSId;
    }

    public FetchChannelView(Response<ChannelDetails> userResultResponse) {
        this.userResultResponse = userResultResponse;
    }

    public FetchChannelView(String channelSId, Response<ChannelDetails> userResultResponse, HashMap<String, FetchInDetails> fetchInDetails) {
        this.channelSId = channelSId;
        this.userResultResponse = userResultResponse;
        this.fetchInDetails = fetchInDetails;
    }

    public FetchChannelView(String channelSId, Response<ChannelDetails> userResultResponse) {
        this.channelSId = channelSId;
        this.userResultResponse = userResultResponse;
    }

    public String getChannelSId() {
        return channelSId;
    }

    public Response<ChannelDetails> getUserResultResponse() {
        return userResultResponse;
    }

    public HashMap<String, FetchInDetails> getFetchInDetails() {
        return fetchInDetails;
    }
}
