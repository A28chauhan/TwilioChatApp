package com.carematix.twiliochatapp.architecture.table;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "User_Channel_List")
public
class ChannelList {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "organizerProgramUserId")
    private String organizerProgramUserId;

    @ColumnInfo(name = "attendeeProgramUserId")
    private String attendeeProgramUserId;

    @ColumnInfo(name = "channelSid")
    private String channelSid;

    public ChannelList(String organizerProgramUserId, String attendeeProgramUserId, String channelSid) {
        this.organizerProgramUserId = organizerProgramUserId;
        this.attendeeProgramUserId = attendeeProgramUserId;
        this.channelSid = channelSid;
    }

    public String getOrganizerProgramUserId() {
        return organizerProgramUserId;
    }

    public void setOrganizerProgramUserId(String organizerProgramUserId) {
        this.organizerProgramUserId = organizerProgramUserId;
    }

    public String getAttendeeProgramUserId() {
        return attendeeProgramUserId;
    }

    public void setAttendeeProgramUserId(String attendeeProgramUserId) {
        this.attendeeProgramUserId = attendeeProgramUserId;
    }

    public String getChannelSid() {
        return channelSid;
    }

    public void setChannelSid(String channelSid) {
        this.channelSid = channelSid;
    }
}
