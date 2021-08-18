package com.carematix.twiliochatapp.architecture.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.roomdatabase.AppDatabase;
import com.carematix.twiliochatapp.architecture.roomdatabase.ChannelListDao;
import com.carematix.twiliochatapp.architecture.table.ChannelList;

import java.util.List;

public
class UserChannelRepository {

    private ChannelListDao channelListDao;

    private static volatile UserChannelRepository instance;


    public UserChannelRepository(Application application){
        AppDatabase db =AppDatabase.getDatabase(application);
        channelListDao = db.channelListDao();
    }


    public void insert(final ChannelList channelList){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            channelListDao.insert(channelList);
        });
    }

    public void delete(){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            channelListDao.deleteAll();
        });

    }

    public LiveData<List<ChannelList>> getChannelDetails(String programUserId,String attendeeProgramUserID){
        return channelListDao.getChannelDetails(programUserId,attendeeProgramUserID);
    }

    public UserChannelRepository(ChannelListDao channelListDao){
        channelListDao.deleteAll();
    }

    public static UserChannelRepository getInstance(ChannelListDao channelListDao) {
        if (instance == null) {
            synchronized(UserChannelRepository.class) {
                if (instance == null)
                    instance = new UserChannelRepository(channelListDao);
            }
        }
        return instance;
    }
}
