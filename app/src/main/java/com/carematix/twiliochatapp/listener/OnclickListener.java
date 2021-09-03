package com.carematix.twiliochatapp.listener;

import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.twilio.chat.Channel;

public
interface OnclickListener {
    void onClick(UserAllList userAllList, Channel channels1, String name);
}
