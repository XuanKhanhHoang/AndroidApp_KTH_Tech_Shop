package com.ktm.kthtechshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ktm.kthtechshop.AppSharedPreferences;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapter.Adapter_CartRclView;
import com.ktm.kthtechshop.dto.CartItem;
import com.ktm.kthtechshop.dto.GetCartResponse;
import com.ktm.kthtechshop.dto.LoginResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends NeededCallApiActivity {
    private AppSharedPreferences appSharedPreferences;
    private String accessToken;
    private RecyclerView cartRclView;
    private ArrayList<CartItem> cartItems;
    private boolean isCheckingAccount = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        appSharedPreferences = new AppSharedPreferences(this);
        accessToken = appSharedPreferences.getAccessToken();
        initApiService();
        if (accessToken.isEmpty() || !appSharedPreferences.getIsAuth()) {
            Toast.makeText(this, "Vui lòng đăng nhập ", Toast.LENGTH_SHORT).show();
            Intent it = new Intent(this, LoginActivity.class);
            CartActivity.this.startActivity(it);
        } else {
            apiServices.loginWithAccessToken("Bearer " + accessToken).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        appSharedPreferences.setAllAttribute(response.body().value.userId,
                                response.body().accessToken, response.body().value.firstName,
                                response.body().value.avatar);
                        getCart("Bearer " + response.body().accessToken);
                    } else {
                        Toast.makeText(CartActivity.this, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
                        appSharedPreferences.clear();

                        Intent it = new Intent(CartActivity.this, LoginActivity.class);
                        CartActivity.this.startActivity(it);
                    }
                    isCheckingAccount = false;

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(CartActivity.this, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
                    appSharedPreferences.clear();
                    isCheckingAccount = false;
                }
            });

        }
        cartRclView = findViewById(R.id.Cart_CartProduct_rclView);
        cartRclView.setHasFixedSize(true);

    }

    private void getCart(String accessToken) {
        apiServices.getCart(accessToken).enqueue(new Callback<GetCartResponse>() {
            @Override
            public void onResponse(Call<GetCartResponse> call, Response<GetCartResponse> response) {
                if (response.isSuccessful()) {
                    ArrayList<CartItem> res = response.body().value;
                    String cartJson = appSharedPreferences.getCart();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    if (cartJson != null && !cartJson.isEmpty()) {
                        Type type = new TypeToken<ArrayList<CartItem>>() {
                        }.getType();
                        ArrayList<CartItem> cart = gson.fromJson(cartJson, type);
                        HashMap<Integer, CartItem> cartMap = new HashMap<>();
                        for (CartItem item : cart) {
                            cartMap.put(item.cartId, item);
                        }

                        for (int i = 0; i < res.size(); i++) {
                            CartItem item = res.get(i);
                            CartItem cartItem = cartMap.get(item.cartId);
                            if (cartItem != null) {
                                item.amount = cartItem.amount;
                            } else {
                                item.amount = 1;
                            }
                            res.set(i, item);
                        }
                    }
                    appSharedPreferences.setCart(gson.toJson(res));
                    cartItems = res;
                    cartRclView.setAdapter(new Adapter_CartRclView(CartActivity.this, cartItems));
                    cartRclView.setLayoutManager(new LinearLayoutManager(CartActivity.this, RecyclerView.VERTICAL, false));
                } else {
                    Toast.makeText(CartActivity.this, "get cart failed", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<GetCartResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "fetch cart failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}