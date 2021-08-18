package com.carematix.twiliochatapp.architecture.table;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Channel_list")
public
class UserChannelList {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "sid")
    public String sid;

    @ColumnInfo(name = "friendlyName")
    public String friendlyName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
