package com.carematix.twiliochatapp.twilio;

import com.carematix.twiliochatapp.application.TwilioApplication;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;

public class ToastStatusListener extends StatusListener {

    private final String okText;
    private final String errorText;

    public ToastStatusListener(String ok, String error) {
        okText = ok;
        errorText = error;
    }

    @Override
    public void onSuccess()
    {
        TwilioApplication.get().showToast(okText);
    }

    @Override
    public void onError(ErrorInfo errorInfo)
    {
        TwilioApplication.get().showError(errorText, errorInfo);
    }
}
