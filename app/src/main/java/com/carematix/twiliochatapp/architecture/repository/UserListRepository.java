package com.carematix.twiliochatapp.architecture.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.roomdatabase.AppDatabase;
import com.carematix.twiliochatapp.architecture.roomdatabase.UserListDao;
import com.carematix.twiliochatapp.architecture.table.UserAllList;

import java.util.List;

public
class UserListRepository {

    private UserListDao userChatDao;

    private static volatile UserListRepository instance;

    public UserListRepository(Application application){
        AppDatabase db =AppDatabase.getDatabase(application);
        userChatDao = db.userListDao();
    }



    public UserListDao getUserChatDao() {
        return userChatDao;
    }

    public LiveData<List<UserAllList>> getAllData(){
        return userChatDao.getAllList();
    }

    public LiveData<List<UserAllList>> getUserByID(String programUserId){
        return userChatDao.getUserByID(programUserId);
    }

    public void setUserChatDao(UserListDao userChatDao) {
        this.userChatDao = userChatDao;
    }

    public void insert(final UserAllList userChat){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userChatDao.insert(userChat);
        });
    }

    public void delete(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userChatDao.deleteAll();
        });

    }


    public UserListRepository(UserListDao userChatDao){
        userChatDao.deleteAll();
    }

    public static UserListRepository getInstance(UserListDao userChatDao) {
        if (instance == null) {
            synchronized(UserListRepository.class) {
                if (instance == null)
                    instance = new UserListRepository(userChatDao);
            }
        }
        return instance;
    }
}
