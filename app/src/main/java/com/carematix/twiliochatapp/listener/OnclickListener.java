package com.carematix.twiliochatapp.listener;

import com.carematix.twiliochatapp.twilio.ChannelModel;

import java.util.Map;

public
interface OnclickListener {

    void onClick(int attendeeProgramUserId, String programUserId, int pos, Map<String, ChannelModel> channels1,String name);
    void onLongClickListener(int attendeeProgramUserId, String programUserId,final Map<String, ChannelModel> channels1,String name);

}
