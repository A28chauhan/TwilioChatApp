package com.carematix.twiliochatapp.fetchchannel.bean;

import androidx.annotation.Nullable;



public
class FetchChannelResult {

    @Nullable
    private FetchChannelView success;
    @Nullable
    private Integer error;
    @Nullable
    private String errorMsg;

    public FetchChannelResult(@Nullable String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public FetchChannelResult(@Nullable Integer error) {
        this.error = error;
    }

    public FetchChannelResult(@Nullable FetchChannelView success) {
        this.success = success;
    }

    @Nullable
    public String getErrorMsg() {
        return errorMsg;
    }

    @Nullable
    public FetchChannelView getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
