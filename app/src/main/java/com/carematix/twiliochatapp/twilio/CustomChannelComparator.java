package com.carematix.twiliochatapp.twilio;
import java.util.Comparator;
public
class CustomChannelComparator implements Comparator<ChannelModel>{

    @Override
    public int compare(ChannelModel lhs, ChannelModel rhs)
    {
        return lhs.getFriendlyName().compareTo(rhs.getFriendlyName());
    }
}
