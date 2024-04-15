package com.ktm.kthtechshop.activity_and_fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapters.Adapter_CreateOrderProductsRclView;
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.CreateOrderResponse;
import com.ktm.kthtechshop.dto.CreateOrderSendToServer;
import com.ktm.kthtechshop.dto.GetHaveTotalPageResponse;
import com.ktm.kthtechshop.dto.ProductOptionWithProductName;
import com.ktm.kthtechshop.dto.UserFullDetail;
import com.ktm.kthtechshop.model.CreateOrderIntentModel;
import com.ktm.kthtechshop.model.CreateOrderProductOptionItemModel;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderActivity extends AppCompatActivity {
    public static String productsIdAndAmountIntentKeyName = "productsIdAndAmount";

    private ApiServiceObject apiServiceObject;
    private AppSharedPreferences appSharedPreferences;
    private String accessToken;
    private RecyclerView productsRclView;
    private TextInputLayout recipientName_TxtInpLayout, phoneNumber_TxtInpLayout, address_TxtInpLayout;
    private Spinner paymentMethodSpinner;
    private TextView totalCost0, totalCost1, totalCost2;
    private Button paymentBtn;
    private ArrayList<CreateOrderIntentModel> productsIdAndAmount;
    private ArrayList<ProductOptionWithProductName> products;
    private boolean isWaiting = false;
    private int totalCost = 0;

    public void onBackPressed() {
        if (isWaiting) {
            Toast.makeText(this, "Vui Lòng chờ", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        apiServiceObject = new ApiServiceObject();
        appSharedPreferences = new AppSharedPreferences(this);
        refChildCOmponents();
        productsRclView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        accessToken = appSharedPreferences.getBearerAccessToken();
        findViewById(R.id.Global_BackBtn).setOnClickListener(v -> finish());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getSerializable(CreateOrderActivity.productsIdAndAmountIntentKeyName) instanceof ArrayList<?>) {
                productsIdAndAmount = (ArrayList<CreateOrderIntentModel>) extras.getSerializable(CreateOrderActivity.productsIdAndAmountIntentKeyName);
            }
        }

        if (productsIdAndAmount == null || productsIdAndAmount.isEmpty()) {
            Toast.makeText(CreateOrderActivity.this, "Lấy thông tin sản phẩm thất bại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ArrayList<String> paymentMethodList = new ArrayList<>();
        paymentMethodList.add("Thanh toán khi nhận hàng");
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                paymentMethodList);
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(ad);
        apiServiceObject.apiServices.getCustomerDetail(accessToken).enqueue(new Callback<UserFullDetail>() {
            @Override
            public void onResponse(@NonNull Call<UserFullDetail> call, @NonNull Response<UserFullDetail> response) {
                if (response.isSuccessful()) {
                    UserFullDetail bd = response.body();
                    if (bd == null) return;
                    Objects.requireNonNull(recipientName_TxtInpLayout.getEditText()).setText(bd.lastName + " " + bd.firstName);
                    Objects.requireNonNull(phoneNumber_TxtInpLayout.getEditText()).setText(bd.phoneNumber);
                    Objects.requireNonNull(address_TxtInpLayout.getEditText()).setText(bd.address);
                    return;
                }
                Toast.makeText(CreateOrderActivity.this, "Lấy thông tin người dùng thất bại", Toast.LENGTH_SHORT).show();
                finish();

            }

            @Override
            public void onFailure(@NonNull Call<UserFullDetail> call, @NonNull Throwable t) {
                Toast.makeText(CreateOrderActivity.this, "Lấy thông tin người dùng thất bại", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        HashMap<String, String> hashMap = new HashMap<>();
        List<String> osId = new ArrayList<>();
        for (int i = 0; i < productsIdAndAmount.size(); i++) {
            osId.add(String.valueOf(productsIdAndAmount.get(i).productOptionId));
        }

        hashMap.put("product_per_page", String.valueOf(productsIdAndAmount.size()));
        apiServiceObject.apiServices.getProductOptionBasicList(hashMap, osId).enqueue(new Callback<GetHaveTotalPageResponse<ArrayList<ProductOptionWithProductName>>>() {
            @Override
            public void onResponse(@NonNull Call<GetHaveTotalPageResponse<ArrayList<ProductOptionWithProductName>>> call, @NonNull Response<GetHaveTotalPageResponse<ArrayList<ProductOptionWithProductName>>> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Toast.makeText(CreateOrderActivity.this, "Lấy thông tin sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    products = response.body().value;
                    ArrayList<CreateOrderProductOptionItemModel> ls = new ArrayList<>();
                    for (ProductOptionWithProductName item : products
                    ) {
                        for (CreateOrderIntentModel i : productsIdAndAmount
                        ) {
                            if (i.productOptionId == item.id) {
                                ls.add(new CreateOrderProductOptionItemModel(item, i.amount));
                                totalCost += i.amount * item.sellingPrice;
                                break;
                            }
                        }
                    }
                    totalCost0.setText(Utils.formatPrice(totalCost));
                    totalCost1.setText(Utils.formatPrice(totalCost + 20000));
                    totalCost2.setText(Utils.formatPrice(totalCost));

                    productsRclView.setAdapter(new Adapter_CreateOrderProductsRclView(CreateOrderActivity.this, ls));
                } else {
                    Toast.makeText(CreateOrderActivity.this, "Lấy thông tin sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetHaveTotalPageResponse<ArrayList<ProductOptionWithProductName>>> call, @NonNull Throwable t) {
                Toast.makeText(CreateOrderActivity.this, "Lấy thông tin sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        paymentBtn.setOnClickListener(v -> {
            if (isWaiting) {
                Toast.makeText(this, "Vui Lòng chờ", Toast.LENGTH_SHORT).show();
                return;
            }
            isWaiting = true;
            apiServiceObject.apiServices.createOrder(accessToken, new CreateOrderSendToServer(productsIdAndAmount)).enqueue(new Callback<CreateOrderResponse>() {
                @Override
                public void onResponse(@NonNull Call<CreateOrderResponse> call, @NonNull Response<CreateOrderResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CreateOrderActivity.this, "Đặt hàng thành công, mã đơn hàng" + response.body().orderId, Toast.LENGTH_SHORT).show();
                        isWaiting = false;
                        finish();
                        return;
                    }
                    isWaiting = false;
                    Toast.makeText(CreateOrderActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(@NonNull Call<CreateOrderResponse> call, @NonNull Throwable t) {
                    isWaiting = false;
                    Toast.makeText(CreateOrderActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();

                }
            });
        });

    }

    void refChildCOmponents() {
        recipientName_TxtInpLayout = findViewById(R.id.CreateOrderActivity_RecipientName);
        address_TxtInpLayout = findViewById(R.id.CreateOrderActivity_Address);
        phoneNumber_TxtInpLayout = findViewById(R.id.CreateOrderActivity_PhoneNumber);
        productsRclView = findViewById(R.id.CreateOrderActivity_Products_rclView);
        paymentMethodSpinner = findViewById(R.id.CreateOrderActivity_PaymentMethodSpinner);
        productsRclView = findViewById(R.id.CreateOrderActivity_Products_rclView);
        totalCost1 = findViewById(R.id.CreateOrderActivity_TotalCost1);
        totalCost0 = findViewById(R.id.CreateOrderActivity_TotalCost);
        totalCost2 = findViewById(R.id.CreateOrderActivity_TotalCost2);

        paymentBtn = findViewById(R.id.CreateOrderActivity_Payment_Btn);
    }
}