package com.carematix.twiliochatapp.listener;

import com.carematix.twiliochatapp.twilio.ChannelModel;
import com.twilio.chat.Channel;

import java.util.Map;

public
interface OnclickListener {

    void onClick(int attendeeProgramUserId, String programUserId, int pos, Channel channels1, String name);
    void onLongClickListener(int attendeeProgramUserId, String programUserId,Channel channels1,String name);

    // void onClick(int attendeeProgramUserId, String programUserId, int pos, Map<String, ChannelModel> channels1,String name);
   // void onLongClickListener(int attendeeProgramUserId, String programUserId,final Map<String, ChannelModel> channels1,String name);

}
