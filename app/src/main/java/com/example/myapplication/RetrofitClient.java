package com.example.myapplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.102:3000/api/";
    private static RetrofitClient mInstace;
    private Retrofit retrofit;
    private RetrofitClient()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static synchronized RetrofitClient getmInstance() {
        if(mInstace == null)
        {
            mInstace = new RetrofitClient();
        }
        return mInstace;
    }
    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
