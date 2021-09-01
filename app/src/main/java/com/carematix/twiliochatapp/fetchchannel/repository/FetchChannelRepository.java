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
import com.carematix.twiliochatapp.helper.Logs;
import com.twilio.chat.Channel;

import java.util.HashMap;
import java.util.Map;

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

    public FetchInDetails getFetchInDetails() {
        return fetchInDetails;
    }

    public void setFetchInDetails(FetchInDetails fetchInDetails) {
        this.fetchInDetails = fetchInDetails;
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
                //FetchInDetails data = ((Result.Success<FetchInDetails>) result).getData();
                fetchInDetails(((Result.Success<FetchInDetails>) result).getData());
            }
        return result;
    }

    /*public Result<FetchInDetails> fetchChannelList(String attendeeID,String programUserId) {
        Result<FetchInDetails> result=null;

        HashMap<String,Result<FetchInDetails>> hashMap = dataSource.fetchChannel(attendeeID,programUserId);

        try {
            if(hashMap.size() > 0)
            for(Map.Entry<String, Result<FetchInDetails>> result1 : hashMap.entrySet()){
                Logs.d("fetchChannelList responce data ","attendeeProgramUserId  key"+result1.getKey()+" "+hashMap.size());
                if(attendeeID.equals(result1.getKey()))
                result = result1.getValue();
                if(result != null)
                    if (result instanceof Result.Success) {
                        FetchInDetails data = ((Result.Success<FetchInDetails>) result).getData();
                        fetchInDetails(((Result.Success<FetchInDetails>) result).getData());
                        setFetchInDetails(((Result.Success<FetchInDetails>) result).getData());
                    }
                return result;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }*/



}
