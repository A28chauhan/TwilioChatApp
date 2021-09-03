package com.carematix.twiliochatapp.fetchchannel.viewmodel;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.architecture.repository.UserChannelListRepository;
import com.carematix.twiliochatapp.data.Result;
import com.carematix.twiliochatapp.data.model.LoggedInUser;
import com.carematix.twiliochatapp.fetchchannel.bean.FetchChannelResult;
import com.carematix.twiliochatapp.fetchchannel.bean.FetchChannelView;
import com.carematix.twiliochatapp.fetchchannel.data.FetchInDetails;
import com.carematix.twiliochatapp.fetchchannel.repository.FetchChannelRepository;
import com.carematix.twiliochatapp.helper.Logs;


public class FetchChannelViewModel extends ViewModel {

    private MutableLiveData<FetchChannelResult> fetchChannelResult = new MutableLiveData<>();

    FetchChannelRepository fetchChannelRepository;

    public FetchChannelViewModel(FetchChannelRepository fetchChannelRepository) {
        this.fetchChannelRepository = fetchChannelRepository;
    }


    public MutableLiveData<FetchChannelResult> getFetchChannelResult() {
        return fetchChannelResult;
    }


    public void fetchChannel(String attendeeID, String programUserId) {
        // can be launched in a separate asynchronous job
        Result<FetchInDetails> result = fetchChannelRepository.fetchChannelList(attendeeID, programUserId);
        if (result instanceof Result.Success) {
            Logs.d("responce data ","attendeeProgramUserId  "+  "responce ");

            FetchInDetails data = ((Result.Success<FetchInDetails>) result).getData();
            fetchChannelResult.setValue(new FetchChannelResult(new FetchChannelView(data.getAttendeUserID(),data.getChannelDetailsResponse(), data.getHashMapFetchInDetails())));
        } else if (result instanceof Result.Failure){
            fetchChannelResult.setValue(new FetchChannelResult(R.string.channel_not_found));
        }else{

        }
    }
}
