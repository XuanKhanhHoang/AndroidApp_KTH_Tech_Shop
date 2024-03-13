package com.ktm.kthtechshop.others;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ktm.kthtechshop.api.ApiServices;
import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.localhostIp;

import java.io.Closeable;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<ArrayList<CategoryItem>> categories;
    ApiServices apiServices;

    public CategoryViewModel() {
        super();
        initApiService();
    }

    public CategoryViewModel(@NonNull Closeable... closeables) {
        super(closeables);
        initApiService();
    }

    protected void initApiService() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(localhostIp.LOCALHOST_IP.getValue() + ":8081/api/v1/") //for ld
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiServices = retrofit.create(ApiServices.class);
    }

    public LiveData<ArrayList<CategoryItem>> getCategories() {
        if (categories == null) {
            categories = new MutableLiveData<>();
//            apiServices.getCategoryList().enqueue(new Callback<ArrayList<CategoryItem>>() {
//                @Override
//                public void onResponse(Call<ArrayList<CategoryItem>> call, Response<ArrayList<CategoryItem>> response) {
//                    if (response.isSuccessful()) {
//                        categoryItemArrayList = response.body();
//                        categoryRclView.setAdapter(new Adapter_CategoryRclView(getApplicationContext(), categoryItemArrayList));
//                    } else {
//                        Toast.makeText(HomePageActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ArrayList<CategoryItem>> call, Throwable t) {
//                    Toast.makeText(HomePageActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
        return categories;
    }

    public void setCategories(ArrayList<CategoryItem> categories) {
        this.categories.setValue(categories);
    }
}
