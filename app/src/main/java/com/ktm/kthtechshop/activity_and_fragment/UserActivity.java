package com.ktm.kthtechshop.activity_and_fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapters.Adapter_OrdersPreviewRclView;
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.GetHaveTotalPageResponse;
import com.ktm.kthtechshop.dto.Order;
import com.ktm.kthtechshop.dto.UserFullDetail;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private ApiServiceObject apiServiceObject;
    private AppSharedPreferences appSharedPreferences;
    private ImageView avatar;
    private TextView userName, firstName, lastName, gender, email, phoneNumber, address;
    private String accessToken;
    private RecyclerView deliveringOrder, allOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        apiServiceObject = new ApiServiceObject();
        appSharedPreferences = new AppSharedPreferences(this);
        accessToken = appSharedPreferences.getBearerAccessToken();
        if (!appSharedPreferences.getIsAuth() || accessToken.isEmpty()) {
            Intent it = new Intent(UserActivity.this, LoginActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            UserActivity.this.startActivity(it);
            finish();
        }

        avatar = findViewById(R.id.UserActivity_UserAvatar);
        firstName = findViewById(R.id.UserActivity_FirstName);
        lastName = findViewById(R.id.UserActivity_Last_Name);
        gender = findViewById(R.id.UserActivity_Gender);
        email = findViewById(R.id.UserActivity_Email);
        phoneNumber = findViewById(R.id.UserActivity_PhoneNumber);
        address = findViewById(R.id.UserActivity_Address);
        userName = findViewById(R.id.UserActivity_UserName);

        deliveringOrder = findViewById(R.id.UserActivity_DeliveringOrders_RclView);
        allOrder = findViewById(R.id.UserActivity_AllOrders_RclView);
        ((TextView) findViewById(R.id.UserActivity_ToEdit)).setOnClickListener(v -> {
            Intent it = new Intent(UserActivity.this, UserEditInfoActivity.class);
            UserActivity.this.startActivity(it);
        });
        findViewById(R.id.Global_BackBtn).setOnClickListener(v -> finish());
        deliveringOrder.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        allOrder.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        apiServiceObject.apiServices.getCustomerDetail(accessToken).enqueue(new Callback<UserFullDetail>() {
            @Override
            public void onResponse(@NonNull Call<UserFullDetail> call, @NonNull Response<UserFullDetail> response) {
                if (response.isSuccessful()) {
                    UserFullDetail bd = response.body();
                    firstName.setText(bd.firstName);
                    lastName.setText(bd.lastName);
                    userName.setText(bd.loginName);
                    gender.setText(bd.gender ? "Nam" : "Nữ");
                    email.setText(bd.email);
                    phoneNumber.setText(bd.phoneNumber);
                    address.setText(bd.address);
                    Executor executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executor.execute(() -> {
                        Bitmap bitmap = Utils.getBitmapFromURL(bd.avatar);
                        handler.post(() -> {
                            if (bitmap != null) {
                                avatar.setImageBitmap(bitmap);
                            }
                        });
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFullDetail> call, @NonNull Throwable t) {

            }
        });

        apiServiceObject.apiServices.getOrderList(accessToken, 2, 1).enqueue(new Callback<GetHaveTotalPageResponse<ArrayList<Order>>>() {
            @Override
            public void onResponse(@NonNull Call<GetHaveTotalPageResponse<ArrayList<Order>>> call, @NonNull Response<GetHaveTotalPageResponse<ArrayList<Order>>> response) {
                if (response.isSuccessful()) {
                    deliveringOrder.setAdapter(new Adapter_OrdersPreviewRclView(UserActivity.this, response.body().value));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetHaveTotalPageResponse<ArrayList<Order>>> call, @NonNull Throwable t) {

            }
        });
        apiServiceObject.apiServices.getOrderList(accessToken, 1).enqueue(new Callback<GetHaveTotalPageResponse<ArrayList<Order>>>() {
            @Override
            public void onResponse(@NonNull Call<GetHaveTotalPageResponse<ArrayList<Order>>> call, @NonNull Response<GetHaveTotalPageResponse<ArrayList<Order>>> response) {
                if (response.isSuccessful()) {
                    allOrder.setAdapter(new Adapter_OrdersPreviewRclView(UserActivity.this, response.body().value));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetHaveTotalPageResponse<ArrayList<Order>>> call, @NonNull Throwable t) {
                int b = 0;
                b++;
            }
        });
    }
}