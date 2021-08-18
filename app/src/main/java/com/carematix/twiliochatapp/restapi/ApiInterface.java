package com.carematix.twiliochatapp.restapi;

import com.carematix.twiliochatapp.bean.FetchUser;
import com.carematix.twiliochatapp.bean.User;
import com.carematix.twiliochatapp.bean.accesstoken.TokenResponse;
import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.bean.fetchChannel.LeaveChannel;
import com.carematix.twiliochatapp.bean.login.DroLanguage;
import com.carematix.twiliochatapp.bean.login.UserResult;
import com.carematix.twiliochatapp.bean.userList.UserDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @POST(ApiConstants.AUTH_URL)
    Call<UserResult> postUserDetails(@Header("Content-Type") String content, @Header("X-DRO-SOURCE") String source,
                                     @Header("X-DRO-TIMEZONE") String timeZone, @Header("X-DRO-LANGUAGE") String languageCode, @Body User paramUser);

    @GET(ApiConstants.GET_PROGRAM_LANGUAGE)
    Call<List<DroLanguage>> getLanguageData(@Header("X-DRO-SOURCE") String source, @Header("X-DRO-TIMEZONE") String timeZone,
                                            @Header("X-DRO-LANGUAGE") String languageCode, @Path("ID") String id);
    @FormUrlEncoded
    @POST(ApiConstants.FETCH_USER)
    Call<FetchUser> fetchUser(@Field("programUserId") String programUserId, @Field("device") String device);

    @FormUrlEncoded
    @POST(ApiConstants.GET_TOKEN)
    Call<TokenResponse> getToken(@Field("programUserId") String programUserId, @Field("device") String device);


    @FormUrlEncoded
    @POST(ApiConstants.GET_USER_LIST)
    Call<UserDetails> getUserList(@Field("programUserId") int programUserId, @Field("roleId") int roleId, @Field("device") String device);

    @FormUrlEncoded
    @POST(ApiConstants.FETCH_CHANNEL)
    Call<ChannelDetails> fetchChannel(@Field("oraganiserProgramUserId") String programUserId, @Field("attandeeProgramUserId") String attendProgramUserId, @Field("device") String device);


    @FormUrlEncoded
    @POST(ApiConstants.LEAVE_CHANNEL)
    Call<LeaveChannel> leaveChannel(@Field("programUserId") String programUserId, @Field("channelId") String channelId, @Field("device") String device);



}
