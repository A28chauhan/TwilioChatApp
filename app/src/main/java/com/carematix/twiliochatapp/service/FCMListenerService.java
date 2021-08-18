package com.carematix.twiliochatapp.service;

import androidx.annotation.NonNull;

import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.NotificationUtils;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.twilio.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.twilio.chat.ChatClient;
import com.twilio.chat.NotificationPayload;


public
class FCMListenerService extends FirebaseMessagingService {

    private static final Logger logger = Logger.getLogger(FCMListenerService.class);

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Logs.d("onMessageReceived for FCM","FCM");

        Logs.d("onMessageReceived","From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logs.d("onMessageReceived","Data Message Body: " + remoteMessage.getData());

            NotificationPayload payload = new NotificationPayload(remoteMessage.getData());

            ChatClient client = TwilioApplication.get().getChatClientManager().getChatClient();
            if (client != null) {
                client.handleNotification(payload);
            }

            NotificationPayload.Type type = payload.getType();

            if (type == NotificationPayload.Type.UNKNOWN) return; // Ignore everything we don't support

            String title = "Twilio Notification";

            if (type == NotificationPayload.Type.NEW_MESSAGE)
                title = "Twilio: New Message";
            if (type == NotificationPayload.Type.ADDED_TO_CHANNEL)
                title = "Twilio: Added to Channel";
            if (type == NotificationPayload.Type.INVITED_TO_CHANNEL)
                title = "Twilio: Invited to Channel";
            if (type == NotificationPayload.Type.REMOVED_FROM_CHANNEL)
                title = "Twilio: Removed from Channel";

            String body = remoteMessage.getData().get(0);
            title = remoteMessage.getData().get(0);
            handleNotification(remoteMessage.getData().get(0),body,title);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logs.d("onMessageReceived","Notification Message Body: " + remoteMessage.getNotification().getBody());
            Logs.e("onMessageReceived","We do not parse notification body - leave it to system");
        }
    }

    //onDialogInterfaceListener onDialogInterfaceListener;
    public void handleNotification(String msg,String body,String title){

        boolean isAppBackGround = Utils.isAppIsInBackground(getApplicationContext());
        if (!isAppBackGround) {
            PrefManager prefManager = new PrefManager(FCMListenerService.this);
            if(prefManager.getBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK)) {
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }
        }else{
            // If the app is in background, firebase itself handles the notification

        }

        showDialogCall(isAppBackGround);

    }

    public void showDialogCall(boolean isAppBackground){
        if(!isAppBackground){
            PrefManager prefManager =new PrefManager(FCMListenerService.this);

            String bb = prefManager.getStringValue(PrefConstants.WHICH_SCREEN);
            if(bb.contains("chat")){
              //  onDialogInterfaceListener = ChatFragment.onDialogInterfaceListener1;
               // onDialogInterfaceListener.onSuccess();
            }else{
               // onDialogInterfaceListener = MainActivity.onDialogInterfaceListener;
               // onDialogInterfaceListener.onSuccess();
            }
        }
    }
}
