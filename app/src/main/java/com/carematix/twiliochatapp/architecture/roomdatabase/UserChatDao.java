package com.carematix.twiliochatapp.architecture.roomdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.carematix.twiliochatapp.architecture.table.UserChat;

import java.util.List;

@Dao
public
interface UserChatDao {

    @Query("SELECT * FROM User_Chat WHERE user_id IN (:userIds)")
    LiveData<List<UserChat>> getAll(String userIds);

    @Query("SELECT * FROM User_Chat WHERE user_id IN (:userIds) LIMIT 1")
    LiveData<List<UserChat>> loadAllByIds(String userIds);

    // @Query("SELECT * FROM User_Chat WHERE title LIKE :first ") //LIMIT 20
    // UserChat findByName(String first, String last);

    //@Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(UserChat users);

    @Delete
    void delete(UserChat user);

    @Query("DELETE FROM User_Chat")
    void deleteAll();

    @Query("DELETE FROM User_Chat WHERE user_id in (:channelId)")
    void deleteAll(String channelId);
}
