package com.carematix.twiliochatapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.twilio.chat.ChatClient;
import com.twilio.chat.NotificationPayload;


public
class FCMListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Logs.d("onMessageReceived","onMessageReceived");
        if (remoteMessage.getData().size() > 0) {

            String title = null;
            try {
                NotificationPayload payload = new NotificationPayload(remoteMessage.getData());

                Logs.d("onMessageReceived","onMessageReceived"+remoteMessage.getData());
                ChatClient client = TwilioApplication.get().getChatClientManager().getChatClient();
                if (client != null) {
                    client.handleNotification(payload);
                }

                NotificationPayload.Type type = payload.getType();

                if (type == NotificationPayload.Type.UNKNOWN) return; // Ignore everything we don't support

                title = "New Notification";

                if (type == NotificationPayload.Type.NEW_MESSAGE)
                    title = "New Message";
                if (type == NotificationPayload.Type.ADDED_TO_CHANNEL)
                    title = "Added to Channel";
                if (type == NotificationPayload.Type.INVITED_TO_CHANNEL)
                    title = "Invited to Channel";
                if (type == NotificationPayload.Type.REMOVED_FROM_CHANNEL)
                    title = "Removed from Channel";

            } catch (Exception e) {
                e.printStackTrace();
            }
            String body = remoteMessage.getData().get("twi_body");
            String title1 = remoteMessage.getData().get("author");
            String chId = remoteMessage.getData().get("channel_id");
            if(title1 == null){
                title1 = title;
            }
            handleNotification(body,title1);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Logs.d("onMessageReceived","Notification Message Body: " + remoteMessage.getNotification().getBody());
            //Logs.e("onMessageReceived","We do not parse notification body - leave it to system");
        }
    }

    //onDialogInterfaceListener onDialogInterfaceListener;
    public void handleNotification(String body,String title){
        try {
            PrefManager prefManager = new PrefManager(FCMListenerService.this);
            boolean isAppBackGround = Utils.isAppIsInBackground(getApplicationContext());
            if (!isAppBackGround) {
                //if(prefManager.getBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK)) {
                    // play notification sound
                   // NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                   // notificationUtils.playNotificationSound();
                //}
                //showDialogCall(isAppBackGround,chId);
            }else{
                // If the app is in background, firebase itself handles the notification
                try {
                    boolean isLogin=prefManager.getBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK);
                    if(isLogin){
                        showNotificationInADialog(title, body);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNotificationInADialog(String title,String msg){
        try {

            int index=msg.indexOf(":");
            String msg1 =msg.substring(index+1);

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY );
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.KEY_NOTIFICATIONS)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(msg1)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY );
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.KEY_NOTIFICATIONS)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(msg1)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);



                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(Constants.KEY_NOTIFICATIONS, title, importance);
                channel.setDescription(msg);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

                NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(this);
                // notificationId is a unique int for each notification that you must define
                notificationManager1.notify(Constants.NOTIFICATION_ID_BIG_IMAGE,  builder.build());
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
