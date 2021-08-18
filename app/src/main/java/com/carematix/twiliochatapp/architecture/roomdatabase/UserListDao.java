package com.carematix.twiliochatapp.architecture.roomdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.carematix.twiliochatapp.architecture.table.UserAllList;

import java.util.List;

@Dao
public
interface UserListDao {

    @Query("SELECT * FROM User_List")
    LiveData<List<UserAllList>> getAllList();


    @Query("SELECT * FROM User_List WHERE droProgramUserId IN (:programUserId) LIMIT 1")
    LiveData<List<UserAllList>> getUserByID(String programUserId);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserAllList users);

    @Query("DELETE FROM User_List")
    void deleteAll();


}
