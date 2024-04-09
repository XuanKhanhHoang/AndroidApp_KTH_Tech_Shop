package com.ktm.kthtechshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ktm.kthtechshop.AppSharedPreferences;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapter.Adapter_CartRclView;
import com.ktm.kthtechshop.dto.CartItem;
import com.ktm.kthtechshop.dto.CartItemSaved;
import com.ktm.kthtechshop.dto.GetHaveTotalPageResponse;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.others.Cart_CartItem_Checked_Listener;
import com.ktm.kthtechshop.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends NeededCallApiActivity implements Cart_CartItem_Checked_Listener {
    private AppSharedPreferences appSharedPreferences;
    private RecyclerView cartRclView;
    private ArrayList<CartItem> cartItems;
    //    private boolean isCheckingAccount = true;
    private ImageButton backBtn;
    private ArrayList<CartItemSaved> checkingCartItems;
    private Integer totalCost;

    private Button PaymentBtn;
    private TextView totalCostTxtView;
    private CheckBox setAllIsCheckedCkBox;
    private String accessToken;
    ShimmerFrameLayout shimmer1, shimmer2, shimmer3, shimmer4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initApiService();
        appSharedPreferences = new AppSharedPreferences(this);
        accessToken = appSharedPreferences.getAccessToken();
        checkingCartItems = new ArrayList<>();

        //ref components
        totalCostTxtView = findViewById(R.id.Cart_TotalCost_txtView);
        setAllIsCheckedCkBox = findViewById(R.id.Cart_CheckAll_ckb);
        cartRclView = findViewById(R.id.Cart_CartProduct_rclView);
        shimmer1 = findViewById(R.id.Cart_CartProduct_Shimmer1);
        shimmer2 = findViewById(R.id.Cart_CartProduct_Shimmer2);
        shimmer3 = findViewById(R.id.Cart_CartProduct_Shimmer3);
        shimmer4 = findViewById(R.id.Cart_CartProduct_Shimmer4);
        //set default cost
        totalCostTxtView.setText("0");
        totalCost = 0;
        //shimmer start
        shimmer1.startShimmer();
        shimmer2.startShimmer();
        shimmer3.startShimmer();
        shimmer4.startShimmer();
        //
        cartRclView.setHasFixedSize(true);
        authorizationUser();
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            finish();
        });
        setAllIsCheckedCkBox.setOnCheckedChangeListener(null);
        setAllIsCheckedCkBox.setChecked(false);
        setAllIsCheckedCkBox.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox) v).isChecked();
            if (isChecked) {
                for (int i = 0; i < cartRclView.getChildCount(); i++) {
                    View view = cartRclView.getChildAt(i);
                    CheckBox checkBox = view.findViewById(R.id.CartItemLayout_CheckBox);
                    checkBox.setChecked(true);
                    setAllIsCheckedCkBox.setChecked(true);
                }
            } else {
                for (int i = 0; i < cartRclView.getChildCount(); i++) {
                    View view = cartRclView.getChildAt(i);
                    CheckBox checkBox = view.findViewById(R.id.CartItemLayout_CheckBox);
                    checkBox.setChecked(false);
                }
            }
        });

    }

    public void authorizationUser() {
        if (accessToken.isEmpty() || !appSharedPreferences.getIsAuth()) {
            Toast.makeText(this, "Vui lòng đăng nhập ", Toast.LENGTH_SHORT).show();
            finish();
            Intent it = new Intent(this, LoginActivity.class);
            CartActivity.this.startActivity(it);
        } else {
            apiServices.loginWithAccessToken("Bearer " + accessToken).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
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
//                    isCheckingAccount = false;

                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Toast.makeText(CartActivity.this, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
                    appSharedPreferences.clear();
//                    isCheckingAccount = false;
                }
            });

        }
    }

    private void getCart(String accessToken) {
        apiServices.getCart(accessToken).enqueue(new Callback<GetHaveTotalPageResponse<ArrayList<CartItem>>>() {
            @Override
            public void onResponse(@NonNull Call<GetHaveTotalPageResponse<ArrayList<CartItem>>> call, @NonNull Response<GetHaveTotalPageResponse<ArrayList<CartItem>>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<CartItem> res = response.body().value;
                    String cartJson = appSharedPreferences.getCart();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    ArrayList<CartItemSaved> cartItemsWillBeSave = new ArrayList<>();
                    if (cartJson != null && !cartJson.isEmpty()) {
                        Type type = new TypeToken<ArrayList<CartItemSaved>>() {
                        }.getType();
                        ArrayList<CartItemSaved> cart = gson.fromJson(cartJson, type);
                        HashMap<Integer, CartItemSaved> cartMap = new HashMap<>();
                        for (CartItemSaved item : cart) {
                            cartMap.put(item.cartId, item);
                        }
                        for (int i = 0; i < res.size(); i++) {
                            CartItem item = res.get(i);
                            CartItemSaved cartItem = cartMap.get(item.cartId);
                            if (cartItem != null) {
                                item.amount = cartItem.amount;
                            } else {
                                item.amount = 1;
                            }
                            cartItemsWillBeSave.add(new CartItemSaved(item.cartId, item.option.id, item.amount));
                            res.set(i, item);
                        }
                    }
                    appSharedPreferences.setCart(gson.toJson(cartItemsWillBeSave));
                    cartItems = res;
                    cartRclView.setAdapter(new Adapter_CartRclView(CartActivity.this, cartItems, CartActivity.this));
                    cartRclView.setLayoutManager(new LinearLayoutManager(CartActivity.this, RecyclerView.VERTICAL, false));
                    shimmer1.stopShimmer();
                    shimmer2.stopShimmer();
                    shimmer3.stopShimmer();
                    shimmer4.stopShimmer();
                    findViewById(R.id.Cart_CartProduct_Shimmer_lnView).setVisibility(View.GONE);
                } else {
                    Toast.makeText(CartActivity.this, "get cart failed", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(@NonNull Call<GetHaveTotalPageResponse<ArrayList<CartItem>>> call, @NonNull Throwable t) {
                Toast.makeText(CartActivity.this, "fetch cart failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void OnCartItemClick(int itemPosition) {
        setAllIsCheckedCkBox.setChecked(false);
        CartItem ct = cartItems.get(itemPosition);
        for (int i = 0; i < checkingCartItems.size(); i++) {
            if (cartItems.get(itemPosition).cartId == checkingCartItems.get(i).cartId) {
                totalCost -= ct.amount * ct.option.sellingPrice;
                checkingCartItems.remove(i);
                totalCostTxtView.setText(Utils.formatPrice(totalCost));
                return;
            }
        }
        checkingCartItems.add(new CartItemSaved(ct.cartId, ct.option.id, ct.amount));
        totalCost += ct.amount * ct.option.sellingPrice;
        totalCostTxtView.setText(Utils.formatPrice(totalCost));
    }

    @Override
    public void OnCartItemDelete(int cartId, int sellingPriceItem) {
        for (int i = 0; i < checkingCartItems.size(); i++) {
            if (cartId == checkingCartItems.get(i).cartId) {
                totalCost -= checkingCartItems.get(i).amount * sellingPriceItem;
                checkingCartItems.remove(i);
                totalCostTxtView.setText(Utils.formatPrice(totalCost));
                return;
            }
        }
    }

    @Override
    public void OnCartItemAmountChange(int itemPosition, int amountToSet) {
        CartItem ct = cartItems.get(itemPosition);
        for (int i = 0; i < checkingCartItems.size(); i++) {
            if (cartItems.get(itemPosition).cartId == checkingCartItems.get(i).cartId) {
                CartItemSaved itemSaved = checkingCartItems.get(i);
                if (itemSaved.amount == amountToSet) return;
                totalCost = itemSaved.amount > amountToSet ? totalCost - ct.option.sellingPrice : totalCost + ct.option.sellingPrice;
                itemSaved.amount = amountToSet;
                checkingCartItems.set(i, itemSaved);
                cartItems.set(itemPosition, ct);
                totalCostTxtView.setText(Utils.formatPrice(totalCost));
                return;
            }
        }
    }
}