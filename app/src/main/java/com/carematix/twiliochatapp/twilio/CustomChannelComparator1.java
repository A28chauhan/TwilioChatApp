package com.carematix.twiliochatapp.twilio;

import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.twilio.chat.Channel;

import java.util.Comparator;

public
class CustomChannelComparator1 implements Comparator<Channel> {
    private String defaultChannelName;
    public CustomChannelComparator1() {
        defaultChannelName =
                TwilioApplication.get().getResources().getString(R.string.default_channel_name);
    }

    @Override
    public int compare(Channel lhs, Channel rhs) {
        if (lhs.getFriendlyName().contentEquals(defaultChannelName)) {
            return -100;
        } else if (rhs.getFriendlyName().contentEquals(defaultChannelName)) {
            return 100;
        }
        return lhs.getFriendlyName().toLowerCase().compareTo(rhs.getFriendlyName().toLowerCase());
    }
}
