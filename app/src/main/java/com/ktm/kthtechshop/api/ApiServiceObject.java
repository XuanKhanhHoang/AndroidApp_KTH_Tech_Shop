package com.ktm.kthtechshop.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ktm.kthtechshop.localhostIp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceObject {
    public ApiServices apiServices;

    protected void initApiService() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(localhostIp.LOCALHOST_IP.getValue() + ":8081/api/v1/") //for ld
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiServices = retrofit.create(ApiServices.class);
    }

    public ApiServiceObject() {
        initApiService();
    }
}
