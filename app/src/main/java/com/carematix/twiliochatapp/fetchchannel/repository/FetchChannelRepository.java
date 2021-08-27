package com.carematix.twiliochatapp.fetchchannel.repository;

import android.app.Application;

import com.carematix.twiliochatapp.architecture.roomdatabase.AppDatabase;
import com.carematix.twiliochatapp.bean.User;
import com.carematix.twiliochatapp.data.LoginDataSource;
import com.carematix.twiliochatapp.data.LoginRepository;
import com.carematix.twiliochatapp.data.Result;
import com.carematix.twiliochatapp.data.model.LoggedInUser;
import com.carematix.twiliochatapp.fetchchannel.data.FetchChannelDataSource;
import com.carematix.twiliochatapp.fetchchannel.data.FetchInDetails;

public
class FetchChannelRepository {

    private static volatile FetchChannelRepository instance;

    private FetchChannelDataSource dataSource;

    FetchInDetails fetchInDetails;

    private void fetchInDetails(FetchInDetails fetchInDetails) {
        this.fetchInDetails = fetchInDetails;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public FetchChannelRepository(Application application){
        //AppDatabase db =AppDatabase.getDatabase(application);
        //userChatDao = db.userChannelDao();
    }

    // private constructor : singleton access
    private FetchChannelRepository(FetchChannelDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static FetchChannelRepository getInstance(FetchChannelDataSource dataSource) {
        if (instance == null) {
            instance = new FetchChannelRepository(dataSource);
        }
        return instance;
    }


    public Result<FetchInDetails> fetchChannelList(String attendeeID,String programUserId) {

        Result<FetchInDetails> result = dataSource.fetchChannel(attendeeID,programUserId);
        if(result != null)
            if (result instanceof Result.Success) {
                fetchInDetails(((Result.Success<FetchInDetails>) result).getData());
            }
        return result;
    }
}
