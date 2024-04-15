package com.ktm.kthtechshop.activity_and_fragment;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ktm.kthtechshop.api.ApiServices;
import com.ktm.kthtechshop.localhostIp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NeededCallApiActivity extends AppCompatActivity {
    ApiServices apiServices;

    protected void initApiService() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(localhostIp.LOCALHOST_IP.getValue() + ":8081/api/v1/") //for ld
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiServices = retrofit.create(ApiServices.class);
    }


}
