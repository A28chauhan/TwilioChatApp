package com.carematix.twiliochatapp.helper;

import android.util.Log;

public
class Logs {

    public static boolean isTrue = true;
    public static void d(String title,String msg){
        if(isTrue){
            Log.d("Logs : "+title," Msg : " + msg);
        }

    }

    public static void e(String title,String msg){
        if(isTrue) {
            Log.d("Logs : " + title, " Msg : " + msg);
        }

    }
}
