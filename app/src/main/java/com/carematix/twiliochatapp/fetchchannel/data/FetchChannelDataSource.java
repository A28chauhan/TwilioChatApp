package com.carematix.twiliochatapp.fetchchannel.data;

import com.carematix.twiliochatapp.bean.User;
import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.bean.login.UserResult;
import com.carematix.twiliochatapp.data.Result;
import com.carematix.twiliochatapp.data.model.LoggedInUser;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public
class FetchChannelDataSource {
    Result<FetchInDetails> fetchInDetailsResult=null;
    ApiInterface apiService= null;
    public Result<FetchInDetails> fetchChannel(final String attendeeProgramUserId,final String programUserId){
        // handle login
        try {
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
                                fetchInDetailsResult = fetchFailureDetails(response.body().getMessage());
                            }else{
                                // setAllChannel(response,attendeeProgramUserId);
                                fetchInDetailsResult = fetchSuccessDetails(response,attendeeProgramUserId);
                            }
                        }else{
                            fetchInDetailsResult = fetchFailureDetails("Error : "+code);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ChannelDetails> call, Throwable t) {
                    LoggedInUser loggedInUser =new LoggedInUser(t.getMessage());
                    fetchInDetailsResult = fetchFailureDetails(t.getMessage().toString());
                }
            });

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
        return fetchInDetailsResult;
    }

    private Result<FetchInDetails> fetchSuccessDetails(Response<ChannelDetails> response ,String attendeeProgramUserId){
        //return new Result.Success<>(new FetchInDetails(attendeeProgramUserId,response));
        return new Result.Success<>(new FetchInDetails(attendeeProgramUserId,response));
    }

    private Result<FetchInDetails> fetchFailureDetails(String errorMsg){
        return new Result.Failure<>(new FetchInDetails(errorMsg));
    }
}
