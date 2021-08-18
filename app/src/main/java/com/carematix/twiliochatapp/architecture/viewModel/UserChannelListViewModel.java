package com.carematix.twiliochatapp.architecture.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.repository.UserChannelListRepository;
import com.carematix.twiliochatapp.architecture.table.UserChannelList;

import java.util.List;

public
class UserChannelListViewModel extends AndroidViewModel {

    private UserChannelListRepository userListRepository;
    private LiveData<List<UserChannelList>> mUserListData;
    private List<UserChannelList> mUserListDataA;

    public UserChannelListViewModel(Application application){
        super(application);
        userListRepository = new UserChannelListRepository(application);
    }

    public LiveData<List<UserChannelList>> getUserNameChatData(String sid,String friendlyName){
        mUserListData = userListRepository.getmUserList(sid,friendlyName);
        return mUserListData;
    }


    public LiveData<List<UserChannelList>> getUserNameData(String sid){
        mUserListData = userListRepository.getUserNameData(sid);
        return mUserListData;
    }

    public LiveData<List<UserChannelList>> getUserDataLikeName(String sid){
        mUserListData = userListRepository.getUserDataLikeName(sid);
        return mUserListData;
    }

    public List<UserChannelList> getUserDataLikeNameA(String sid){
        mUserListDataA = userListRepository.getUserDataLikeNameA(sid);
        return mUserListDataA;
    }

    public void insert(UserChannelList userChannel){
        userListRepository.insert(userChannel);
    }

    public void delete(){
        userListRepository.delete();
    }
}
