package com.carematix.twiliochatapp.twilio;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.accessToken.AccessTokenFetcher;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.listener.LoadChannelListener;
import com.carematix.twiliochatapp.listener.TaskCompletionListener;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Paginator;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelManager implements ChatClientListener {
    private static ChannelManager sharedManager = new ChannelManager();
    private ChatClientManager chatClientManager;
    private List<Channel> channels;
    private ChatClientListener listener;
    private Handler handler;

    private ChannelManager() {
        this.chatClientManager = TwilioApplication.get().getChatClientManager();
        //this.channelExtractor = new ChannelExtractor();
        this.listener = this;
        handler = setupListenerHandler();
    }

    public static ChannelManager getInstance() {
        return sharedManager;
    }

    public List<Channel> getChannels() {
        return channels;
    }



    private String getStringResource(int id) {
        Resources resources = TwilioApplication.get().getResources();
        return resources.getString(id);
    }

    @Override
    public void onChannelAdded(Channel channel) {
        if (listener != null) {
            listener.onChannelAdded(channel);
        }
    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
        if (listener != null) {
            listener.onChannelUpdated(channel, updateReason);
        }
    }

    @Override
    public void onChannelDeleted(Channel channel) {
        if (listener != null) {
            listener.onChannelDeleted(channel);
        }
    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {
        if (listener != null) {
            listener.onChannelSynchronizationChange(channel);
        }
    }

    @Override
    public void onError(ErrorInfo errorInfo) {
        if (listener != null) {
            listener.onError(errorInfo);
        }
    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

    }

    @Override
    public void onChannelJoined(Channel channel) {

    }

    @Override
    public void onChannelInvited(Channel channel) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {
        if (listener != null) {
            listener.onUserUpdated(user, updateReason);
        }
    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onNewMessageNotification(String s, String s1, long l) {

    }

    @Override
    public void onAddedToChannelNotification(String s) {

    }

    @Override
    public void onInvitedToChannelNotification(String s) {

    }

    @Override
    public void onRemovedFromChannelNotification(String s) {

    }

    @Override
    public void onNotificationSubscribed() {

    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

    }

    @Override
    public void onTokenExpired() {
        refreshAccessToken();
    }

    @Override
    public void onTokenAboutToExpire() {
        refreshAccessToken();
    }

    private void refreshAccessToken() {
        AccessTokenFetcher accessTokenFetcher = chatClientManager.getAccessTokenFetcher();
        accessTokenFetcher.fetch(new TaskCompletionListener<String, String>() {
            @Override
            public void onSuccess(String token) {
                ChannelManager.this.chatClientManager.getChatClient().updateToken(token, new StatusListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TwilioApplication.TAG, "Successfully updated access token.");
                    }
                });
            }

            @Override
            public void onError(String message) {
                Log.e(TwilioApplication.TAG,"Error trying to fetch token: " + message);
            }
        });
    }

    private Handler setupListenerHandler() {
        Looper looper;
        Handler handler;
        if ((looper = Looper.myLooper()) != null) {
            handler = new Handler(looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            handler = new Handler(looper);
        } else {
            throw new IllegalArgumentException("Channel Listener must have a Looper.");
        }
        return handler;
    }
}