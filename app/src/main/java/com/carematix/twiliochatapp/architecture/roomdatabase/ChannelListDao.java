package com.carematix.twiliochatapp.architecture.roomdatabase;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.carematix.twiliochatapp.architecture.table.ChannelList;
import com.carematix.twiliochatapp.architecture.table.UserAllList;

import java.util.List;

@Dao
public
interface ChannelListDao {

    @Query("SELECT * FROM User_Channel_List")
    LiveData<List<ChannelList>> getAllChannel();


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ChannelList channelList);

    @Query("DELETE FROM User_Channel_List")
    void deleteAll();


    @Query("SELECT * FROM User_Channel_List WHERE organizerProgramUserId IN (:programUserID) AND attendeeProgramUserId IN (:attendeeProgramUserId) ORDER BY id ASC " )
    LiveData<List<ChannelList>> getChannelDetails(String programUserID ,String attendeeProgramUserId);
}
