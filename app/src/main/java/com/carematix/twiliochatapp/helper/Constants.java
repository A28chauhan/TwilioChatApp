package com.carematix.twiliochatapp.helper;

public
class Constants {


    public static final String EXTRA_CHANNEL="com.twilio.chat.Channel";
    public static final String EXTRA_CHANNEL_SID="C_SID";
    public static final String EXTRA_ID="id";
    public static final String EXTRA_NAME="name";
    public static final String EXTRA_TYPE="type";


    // details set in parameters
    public static String CONTENT_TYPE="application/json";
    public static String CONTENT_TYPE_1="multipart/form-data";
    public static String X_DRO_SOURCE="ANDROID";
    public static String LANGUAGE="EN";
    public static String TIMEZONE="Asia/Calcutta";
    public static String LANGUAGE_F_NAME="English";

    public static boolean appendNotificationMessages = true;

    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String KEY_NOTIFICATIONS = "notifications";

    // Network Error code
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int INTERNAL_SERVER_ERROR = 500;

}
