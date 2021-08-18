package com.carematix.twiliochatapp.helper;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {

    public static String DEBUG_TAG= Utils.class.getSimpleName();
    public static boolean isInserted= true;

    public static String toBase64(String message){
        byte [] data;
        try {
            data = message.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data,Base64.DEFAULT);
            return base64;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Decode the password.
    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String capitalizeAll(String paramString) {
        String str = "";
        for (int i = 0; i < paramString.length(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(Character.toUpperCase(paramString.charAt(i)));
            str = stringBuilder.toString();
        }
        return str;
    }

    public static void showToast(String msg, Context mContext){
        // show toast...
        if(isInserted){
            Toast.makeText(mContext,""+msg,Toast.LENGTH_SHORT).show();
        }
    }

    // network change request check.
    public static boolean onNetworkChange(Context mContext){
        Boolean connected = false;
        boolean isWifiConn= false;

        NetworkInfo networkInfo = null;
        try {
            ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if(networkInfo.isConnected()){
                isWifiConn = networkInfo.isConnected();
            }else{
                networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }

            // boolean isMobileConn = networkInfo.isConnected();

            if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
                //showToast(mContext.getResources().getString(R.string.network_info_status),mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            connected = false;
        }*/
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + networkInfo);

        return connected;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static String setTime(Date timeInMillies){
        String time=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm aaa ");
            time = sdf.format(timeInMillies);
            // Date date = fmt.parse(timeInMillies);
            //return fmt.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    public static String setDateTime(Date timeInMillies){
        String time=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(" dd MMM yyyy ");
            time = sdf.format(timeInMillies);
            // Date date = fmt.parse(timeInMillies);
            //return fmt.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    public static String getDateTime(long timeIn){
        String timeConvert= null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" dd MMM yyyy ");
            timeConvert = simpleDateFormat.format(new Date(timeIn));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeConvert;
    }

    public static String getDateC(long timeIn){
        String timeConvert= null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
            timeConvert = simpleDateFormat.format(new Date(timeIn));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeConvert;
    }

    public static String getDateC1(Date timeIn){
        String timeConvert= null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
            timeConvert = simpleDateFormat.format(timeIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeConvert;
    }
}
