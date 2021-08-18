package com.carematix.twiliochatapp;


import android.content.Context;

import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.listener.TaskCompletionListener;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ErrorInfo;

public class ChatClientBuilder extends CallbackListener<ChatClient> {

    private Context context;
    private TaskCompletionListener<ChatClient, String> buildListener;

    public ChatClientBuilder(Context context) {
        this.context = context;
    }


    public void build(String token, final TaskCompletionListener<ChatClient, String> listener) {
        try {
            ChatClient.Properties props =
                    new ChatClient.Properties.Builder()
                            .setRegion("us1")
                            .createProperties();

            this.buildListener = listener;
            Logs.d("ChatClientBuilder","context : "+context.getApplicationContext());
            Logs.d("ChatClientBuilder","token : "+token);
            Logs.d("ChatClientBuilder","props : "+props);
            ChatClient.create(context.getApplicationContext(),
                    token,
                    props,
                    this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onSuccess(ChatClient chatClient) {
        buildListener.onSuccess(chatClient);
    }

    @Override
    public void onError(ErrorInfo errorInfo) {
        buildListener.onError(errorInfo.getMessage());
        super.onError(errorInfo);
    }
}
