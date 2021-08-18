package com.carematix.twiliochatapp.twilio;

import com.twilio.chat.Members;
import com.twilio.chat.Message;

public
class MessageItem {

    Message message;
    Members members;
    String  currentUser;

    public MessageItem(Message message, Members members, String currentUser)
    {
        this.message = message;
        this.members = members;
        this.currentUser = currentUser;
    }

    public Message getMessage()
    {
        return message;
    }

    public Members getMembers()
    {
        return members;
    }
}
