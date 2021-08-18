package com.carematix.twiliochatapp.architecture.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.repository.UserChatRepository;
import com.carematix.twiliochatapp.architecture.table.UserChat;

import java.util.List;

public
class UserChatViewModel extends AndroidViewModel {

    private UserChatRepository userChatRepository;
    private LiveData<List<UserChat>> mUserChatData;
    private String position;

    public UserChatViewModel(Application application){
        super(application);
        userChatRepository = new UserChatRepository(application);
    }

    public LiveData<List<UserChat>> getUserChatData(String position){
        this.position=position;
        mUserChatData = userChatRepository.getmUserChat(String.valueOf(position));
        return mUserChatData;
    }

    public LiveData<List<UserChat>> getLastUserChatData(String position){
        this.position=position;
        mUserChatData = userChatRepository.getLastUserChat(String.valueOf(position));
        return mUserChatData;
    }

    public void delete(){
        userChatRepository.delete();
    }

    public void delete(String channelId){
        userChatRepository.delete(channelId);
    }

    public void insert(UserChat userChat){
        userChatRepository.insert(userChat);
    }
}