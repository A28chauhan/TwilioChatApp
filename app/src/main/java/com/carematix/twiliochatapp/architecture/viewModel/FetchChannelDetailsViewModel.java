package com.carematix.twiliochatapp.architecture.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.carematix.twiliochatapp.adapter.UserListAdapter;
import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.data.Result;
import com.carematix.twiliochatapp.fetchchannel.data.FetchInDetails;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public
class FetchChannelDetailsViewModel extends AndroidViewModel {

    UserListAdapter.ViewHolder viewHolder;
    String programUserId=null;
    String attendeeProgramUserId= null;
    MutableLiveData<FetchInDetails> mutableLiveData=new MutableLiveData<>();

    public FetchChannelDetailsViewModel(Application application){
        super(application);
        //callApi(programUserId,attendeeProgramUserId);

    }

    public UserListAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(UserListAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public MutableLiveData<FetchInDetails> getMutableLiveData() {
        return mutableLiveData;
    }

    public void setProgramUserId(String programUserId) {
        this.programUserId = programUserId;
    }

    public void setAttendeeProgramUserId(String attendeeProgramUserId) {
        this.attendeeProgramUserId = attendeeProgramUserId;
    }

    ApiInterface apiService= null;
    public void callApi(String programUserId, String attendeeProgramUserId, UserListAdapter.ViewHolder holder){

        if(programUserId != null && attendeeProgramUserId != null){
            apiService= null;
            apiService = ApiClient.getClient1().create(ApiInterface.class);
            Call<ChannelDetails> call = apiService.activeChannel(programUserId,attendeeProgramUserId, Constants.X_DRO_SOURCE);
            call.enqueue(new Callback<ChannelDetails>() {
                @Override
                public void onResponse(Call<ChannelDetails> call, Response<ChannelDetails> response) {
                    try {
                        int code = response.code();
                        if (code == 200) {
                            if(response.body().getMessage().contains("No active")){
                                //fetchInDetailsResult = fetchFailureDetails(response.body().getMessage());
                            }else{
                                Logs.d("responce data ","attendeeProgramUserId  "+attendeeProgramUserId + "responce "+response.body().getData().getChannelSid());
                                FetchInDetails fetchInDetails=new FetchInDetails(attendeeProgramUserId,response,holder);
                                mutableLiveData.setValue(fetchInDetails);
                            }
                        }else{
                            FetchInDetails fetchInDetails=new FetchInDetails("Error : "+code);
                            mutableLiveData.setValue(fetchInDetails);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ChannelDetails> call, Throwable t) {
                    FetchInDetails fetchInDetails=new FetchInDetails("Error : "+t.getMessage());
                    mutableLiveData.setValue(fetchInDetails);

                }
            });
        }


    }

}
