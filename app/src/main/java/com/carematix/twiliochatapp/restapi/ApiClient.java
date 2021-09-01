package com.carematix.twiliochatapp.restapi;


import android.os.Build;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit retrofit = null;

    public static Retrofit getClient(){
        retrofit = null;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(00, TimeUnit.SECONDS)
                .writeTimeout(00, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)//.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getClient1(){
        retrofit = null;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(00, TimeUnit.SECONDS)
                .writeTimeout(00, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL1)//.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    public static Retrofit getClientSurvey(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(00, TimeUnit.SECONDS)
                .writeTimeout(00, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)//.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
