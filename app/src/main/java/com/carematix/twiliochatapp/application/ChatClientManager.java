package com.carematix.twiliochatapp.application;

import android.content.Context;

import com.carematix.twiliochatapp.ChatClientBuilder;
import com.carematix.twiliochatapp.accessToken.AccessTokenFetcher;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.listener.TaskCompletionListener;
import com.carematix.twiliochatapp.twilio.ToastStatusListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;

public
class ChatClientManager {

    private ChatClient chatClient;
    private Context context;
    private AccessTokenFetcher accessTokenFetcher;
    private ChatClientBuilder chatClientBuilder;

    private String fcmToken="";

    public ChatClientManager(Context mContext) {
        this.context = mContext;
        this.accessTokenFetcher = new AccessTokenFetcher(mContext);
        this.chatClientBuilder = new ChatClientBuilder(mContext);
    }

    public void addClientListener(ChatClientListener listener) {
        if (this.chatClient != null) {
            this.chatClient.addListener(listener);
        }
    }

    public void removeClientListener(ChatClientListener listener) {
        if (this.chatClient != null) {
            this.chatClient.removeListener(listener);
        }
    }

    public ChatClient getChatClient() {
        return this.chatClient;
    }


    public void setFCMToken(String token)
    {
        String fcmToken= null;
        try {
            fcmToken =token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logs.d("setFCMToken","setFCMToken "+fcmToken);
        this.fcmToken = fcmToken;
        if (this.chatClient != null) {
            setupFcmToken();
        }
    }


    public void unRegisterFCMToken(String token)
    {
        String fcmToken= null;
        try {
            fcmToken =token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logs.d("setFCMToken","setFCMToken "+fcmToken);
        this.fcmToken = fcmToken;
        if (this.chatClient != null) {
            unregisterFcmToken();
        }
    }

    private void setupFcmToken()
    {
        try {
            if(fcmToken != null && !fcmToken.equals("")){
                this.chatClient.registerFCMToken(new ChatClient.FCMToken(this.fcmToken),
                        new ToastStatusListener("Firebase Messaging registration successful",
                                "Firebase Messaging registration not successful"));
            }else{
                Logs.e("setFCMToken","setFCMToken "+fcmToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterFcmToken()
    {
        try {
            if(fcmToken != null && !fcmToken.equals(""))
                this.chatClient.unregisterFCMToken(new ChatClient.FCMToken(this.fcmToken),
                        new ToastStatusListener("Firebase Messaging unregistration successful",
                                "Firebase Messaging unregistration not successful"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setChatClient(ChatClient client) {
        this.chatClient = client;
    }

    public void connectClient(final TaskCompletionListener<Void, String> listener1) {
        // ChatClient.setLogLevel(android.util.Log.DEBUG);
        accessTokenFetcher.fetch(new TaskCompletionListener<String, String>() {
            @Override
            public void onSuccess(String token) {
                Logs.d("onSUccess"," :"+token);
                buildClient(token, listener1);
            }

            @Override
            public void onError(String message) {
                if (listener1 != null) {
                    listener1.onError(message);
                }
            }
        });
    }

    private void buildClient(String token, final TaskCompletionListener<Void, String> listener) {
        chatClientBuilder.build(token, new TaskCompletionListener<ChatClient, String>() {
            @Override
            public void onSuccess(ChatClient chatClient) {
                ChatClientManager.this.chatClient = chatClient;
                listener.onSuccess(null);
            }

            @Override
            public void onError(String message) {
                listener.onError(message);
            }
        });
    }

    public void shutdown() {
        if(chatClient != null) {
            chatClient.shutdown();
        }
    }

    public AccessTokenFetcher getAccessTokenFetcher() {
        return accessTokenFetcher;
    }
}
