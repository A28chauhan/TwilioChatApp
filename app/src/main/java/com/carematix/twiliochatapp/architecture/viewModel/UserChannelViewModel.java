package com.carematix.twiliochatapp.architecture.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.carematix.twiliochatapp.architecture.repository.UserChannelRepository;
import com.carematix.twiliochatapp.architecture.table.ChannelList;
import com.carematix.twiliochatapp.architecture.table.UserAllList;

import java.util.List;

public
class UserChannelViewModel extends AndroidViewModel {


    private UserChannelRepository userChannelRepository;

    public UserChannelViewModel(Application application){
        super(application);
        userChannelRepository = new UserChannelRepository(application);
    }

    public void insert(ChannelList channelList){
        userChannelRepository.insert(channelList);
    }

    public void delete(){
        userChannelRepository.delete();
    }

    public LiveData<List<ChannelList>> getChannelDetails(String programUserId,String attendeeProgramUserID){
        return userChannelRepository.getChannelDetails(programUserId,attendeeProgramUserID);
    }


}
