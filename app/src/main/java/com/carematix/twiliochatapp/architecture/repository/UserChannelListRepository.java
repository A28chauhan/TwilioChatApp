package com.carematix.twiliochatapp.architecture.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.roomdatabase.AppDatabase;
import com.carematix.twiliochatapp.architecture.roomdatabase.UserChannelDao;
import com.carematix.twiliochatapp.architecture.table.UserChannelList;

import java.util.List;

public
class UserChannelListRepository {

    private UserChannelDao userChatDao;
    private LiveData<List<UserChannelList>> mUserChat;
    private List<UserChannelList> mUserChatA;

    private static volatile UserChannelListRepository instance;

    public UserChannelListRepository(Application application){
        AppDatabase db =AppDatabase.getDatabase(application);
        userChatDao = db.userChannelDao();
    }

    public UserChannelListRepository(UserChannelDao userChatDao){
        userChatDao.deleteAll();
    }

    public LiveData<List<UserChannelList>> getmUserList(String sid,String friendlyName) {
        mUserChat =userChatDao.getAll(sid,friendlyName);
        return mUserChat;
    }


    public LiveData<List<UserChannelList>> getUserNameData(String sid) {
        mUserChat =userChatDao.loadAllByIds(sid);
        return mUserChat;
    }

    public LiveData<List<UserChannelList>> getUserDataLikeName(String sid) {
        String ss = "%"+sid+"%";
        mUserChat =userChatDao.loadDataLikeByIds(ss);
        return mUserChat;
    }

    public List<UserChannelList> getUserDataLikeNameA(String sid) {
        String ss = "%"+sid+"%";
        mUserChatA =userChatDao.loadDataLikeByIdsA(ss);
        return mUserChatA;
    }


    public void insert(final UserChannelList userChat){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userChatDao.insertAll(userChat);
        });
    }

    public void delete(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            userChatDao.deleteAll();
        });

    }


    public static UserChannelListRepository getInstance(UserChannelDao userChatDao) {
        if (instance == null) {
            synchronized(UserChannelListRepository.class) {
                if (instance == null)
                    instance = new UserChannelListRepository(userChatDao);
            }
        }
        return instance;
    }
}
