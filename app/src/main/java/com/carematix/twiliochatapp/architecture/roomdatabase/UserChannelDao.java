package com.carematix.twiliochatapp.architecture.roomdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.carematix.twiliochatapp.architecture.table.UserChannelList;

import java.util.List;

@Dao
public
interface UserChannelDao {


    @Query("SELECT * FROM Channel_list WHERE sid IN (:userIds) or friendlyName IN (:userName)")
    LiveData<List<UserChannelList>> getAll(String userIds, String userName);

    @Query("SELECT * FROM Channel_list WHERE friendlyName IN (:userIds) LIMIT 1")
    LiveData<List<UserChannelList>> loadAllByIds(String userIds);


    @Query("SELECT * FROM Channel_list WHERE sid LIKE (:userIds)  ORDER BY friendlyName DESC LIMIT 1")
    LiveData<List<UserChannelList>> loadDataLikeByIds(String userIds);

    @Query("SELECT * FROM Channel_list WHERE friendlyName LIKE (:userIds) ORDER BY friendlyName DESC LIMIT 1")
    List<UserChannelList> loadDataLikeByIdsA(String userIds);

    //@Insert onConflict = OnConflictStrategy.IGNORE
    @Insert()
    void insertAll(UserChannelList users);

    @Delete
    void delete(UserChannelList user);

    @Query("DELETE FROM Channel_list")
    void deleteAll();
}
