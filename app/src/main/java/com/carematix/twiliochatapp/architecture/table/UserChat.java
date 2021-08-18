package com.carematix.twiliochatapp.architecture.table;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "User_Chat")
public
class UserChat {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "chat_description")
    public String chat_description;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "user_id")
    public String uid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChat_description() {
        return chat_description;
    }

    public void setChat_description(String chat_description) {
        this.chat_description = chat_description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
