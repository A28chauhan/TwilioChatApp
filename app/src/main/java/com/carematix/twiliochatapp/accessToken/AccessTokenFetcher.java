package com.carematix.twiliochatapp.accessToken;

import android.content.Context;


import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.bean.accesstoken.TokenResponse;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.listener.TaskCompletionListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public
class AccessTokenFetcher {

    private Context context;
    public AccessTokenFetcher(Context context) {
        this.context = context;
    }

    public void fetch(final TaskCompletionListener<String, String> listener) {
        PrefManager prefManager =new PrefManager(context);
        ApiInterface apiServiceToken = ApiClient.getClient1().create(ApiInterface.class);
        String programUserId =prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        Call<TokenResponse> call= apiServiceToken.getToken(programUserId, Constants.X_DRO_SOURCE);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                try {
                    int code = response.raw().code();
                    String token = null;
                    if(code == Constants.OK){
                        token= response.body().getData().getToken();
                        prefManager.setStringValue(PrefConstants.TN_TOKEN,token);
                        listener.onSuccess(token);
                    }else{
                        listener.onError("Failed to parse token JSON response");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logs.e(TwilioApplication.TAG, e.getLocalizedMessage());
                    listener.onError("Failed to parse token JSON response");
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Logs.e(TwilioApplication.TAG, t.getLocalizedMessage());
                listener.onError("Failed to fetch token");
            }
        });
    }

}
