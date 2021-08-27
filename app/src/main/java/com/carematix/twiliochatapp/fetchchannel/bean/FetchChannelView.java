package com.carematix.twiliochatapp.fetchchannel.bean;

import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.bean.login.UserResult;

import retrofit2.Response;

public
class FetchChannelView {

    String channelSId;
    public Response<ChannelDetails> userResultResponse;

    public FetchChannelView(String channelSId) {
        this.channelSId = channelSId;
    }

    public FetchChannelView(Response<ChannelDetails> userResultResponse) {
        this.userResultResponse = userResultResponse;
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
}
