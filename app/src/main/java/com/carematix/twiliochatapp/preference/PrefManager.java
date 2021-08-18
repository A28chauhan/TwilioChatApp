package com.carematix.twiliochatapp.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.carematix.twiliochatapp.helper.Constants;

public
class PrefManager {

    Context mContext;
    SharedPreferences sh;
    SharedPreferences.Editor editor;

    public PrefManager(Context context){
        try {
            this.mContext = context;
            sh = mContext.getSharedPreferences(PrefConstants.PREFERENCE_NAME ,Context.MODE_PRIVATE);
            editor = sh.edit();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStringValue(String prefName,String prefValue){
        //set string value
        try {
          //  prefName = DBEncryption.encrypt(prefName,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
           // prefValue = DBEncryption.encrypt(prefValue,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putString(prefName,prefValue);
        editor.commit();
    }

    // get string value
    public String getStringValue(String prefName){
        String sss= null;
        try {
          //  String securityKey = defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW);
          //  String ioKey= defaultPreference.getStringValue(DefConstants.SECRET_KEY_TMP_26_BELOW);
          //  prefName = DBEncryption.encrypt(prefName,securityKey);

            sss = sh.getString(prefName,null);
           // sss = DBEncryption.decrypt(sss,securityKey,ioKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sss;
    }

    // set boolean value
    public void setBooleanValue(String prefName,boolean prefValue){
        try {
           // prefName = DBEncryption.encrypt(prefName,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putBoolean(prefName,prefValue);
        editor.commit();
    }

    // get boolean value
    public boolean getBooleanValue(String prefName){
        //get boolean value
        try {
           // prefName = DBEncryption.encrypt(prefName,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sh.getBoolean(prefName,false);
    }

    // set integer value
    public void setIntegerValue(String prefName,int prefValue){
        try {
          //  prefName = DBEncryption.encrypt(prefName,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putInt(prefName,prefValue);
        editor.commit();
    }

    // get integer value
    public int getIntegerValue(String prefName){
        try {
           // prefName = DBEncryption.encrypt(prefName,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sh.getInt(prefName , 0);
    }

    // set long value in preference
    public void setLongValue(String prefName,long prefValue){
        try {
          //  prefName = DBEncryption.encrypt(prefName,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putLong(prefName,prefValue);
        editor.commit();
    }
    // get long value
    public long getLongValue(String prefName){
        try {
         //   prefName = DBEncryption.encrypt(prefName,defaultPreference.getStringValue(DefConstants.SECRET_KEY_API_26_BELOW));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sh.getLong(prefName,0);}

    public void clearPref(){
        editor.clear();
        editor.commit();
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(Constants.KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return sh.getString(Constants.KEY_NOTIFICATIONS, null);
    }

}
