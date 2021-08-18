package com.carematix.twiliochatapp.architecture.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.roomdatabase.AppDatabase;
import com.carematix.twiliochatapp.architecture.roomdatabase.UserChatDao;
import com.carematix.twiliochatapp.architecture.table.UserChat;

import java.util.List;

public
class UserChatRepository {

    private UserChatDao userChatDao;
    private LiveData<List<UserChat>> mUserChat;

    private static volatile  UserChatRepository instance;

    public UserChatRepository(Application application){
        AppDatabase db =AppDatabase.getDatabase(application);
        userChatDao = db.userChatDao();
        //mUserChat =userChatDao.getAll(uid);
    }

    public UserChatRepository(UserChatDao userChatDao){
        // AppDatabase db =AppDatabase.getDatabase(application);
        userChatDao.deleteAll();
        //mUserChat =userChatDao.getAll(uid);
    }

    public LiveData<List<UserChat>> getmUserChat(String uId) {
        mUserChat =userChatDao.getAll(uId);
        return mUserChat;
    }



    public LiveData<List<UserChat>> getLastUserChat(String uId) {
        mUserChat =userChatDao.loadAllByIds(uId);
        return mUserChat;
    }

    public void insert(final UserChat userChat){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userChatDao.insertAll(userChat);
        });
    }


    public void delete(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userChatDao.deleteAll();
        });
    }

    public void delete(String channelId){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userChatDao.deleteAll(channelId);
        });
    }


    public static UserChatRepository getInstance(UserChatDao userChatDao) {
        if (instance == null) {
            synchronized(UserChatRepository.class) {
                if (instance == null)
                    instance = new UserChatRepository(userChatDao);
            }
        }
        return instance;
    }
}
