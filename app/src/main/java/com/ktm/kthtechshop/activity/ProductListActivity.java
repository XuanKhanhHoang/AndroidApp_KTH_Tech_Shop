package com.ktm.kthtechshop.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapter.Adapter_ProductPreviewRclView;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.ProductPreviewItem;
import com.ktm.kthtechshop.model.ProductPreview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends HaveProductSearchApiActivity {

    private ArrayList<ProductPreview> productList;


    private RecyclerView productListRclView;
    private Integer page = 1, totalPage = 0;
    private TextView loadingTxtView;
    private Map<String, String> coreQueryParams, curQueryParams;
    private boolean isWaiting = false;
    private Button latestProductBtn, minimumPriceProductBtn, fiveStarProductBtn, filterBtn;
    private LinearLayout productList_ProductPreviewShimmerContainer1, productList_ProductPreviewShimmerContainer2;
    private int stateFilter = 1;
    private Adapter_ProductPreviewRclView productsAdapter;
    private ScrollView scrollView;
    private ImageButton backBtn;

    private void startShimmer() {
        productList_ProductPreviewShimmerContainer1.setVisibility(View.VISIBLE);
        productList_ProductPreviewShimmerContainer2.setVisibility(View.VISIBLE);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApiService();
        setContentView(R.layout.activity_product_list);
        refChildComponent();
        backBtn.setOnClickListener(v -> {
            finish();
        });
        startShimmer();

        productListRclView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
//        productListRclView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        productList = new ArrayList<>();
        productsAdapter = new Adapter_ProductPreviewRclView(getApplicationContext(), productList);
        productListRclView.setAdapter(productsAdapter);

        Intent intent = getIntent();
        coreQueryParams = new HashMap<>();
        if (intent != null && intent.getSerializableExtra("queryParams") != null)
            coreQueryParams = (HashMap<String, String>) intent.getSerializableExtra("queryParams");
        setUpSearchFunction();
        if (coreQueryParams.get("keyword") != null && !Objects.requireNonNull(coreQueryParams.get("keyword")).isEmpty() && searchEditText != null) {
            searchEditText.setText(coreQueryParams.get("keyword"));
        }
        curQueryParams = new HashMap<>(coreQueryParams);
        GetDataFromApi(page, coreQueryParams, true, false);


        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            ViewGroup viewGroup = (ViewGroup) v;
            if (isWaiting) return;
            if (scrollY == viewGroup.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                if (page < totalPage) page++;
                else return;
                loadingTxtView.setVisibility(View.VISIBLE);
                isWaiting = true;

                GetDataFromApi(page, curQueryParams, false, false);

            }
        });
        latestProductBtn.setOnClickListener(v -> {
            if (stateFilter == 1) return;
            productList.clear();
            productsAdapter.notifyDataSetChanged();
//            productListRclView.setAdapter(new Adapter_ProductPreviewRclView(ProductListActivity.this, productList));

            startShimmer();
            stateFilter = 1;
            latestProductBtn.setBackgroundResource(R.drawable.shape_rectangle_solid_bottom_border_main);
            minimumPriceProductBtn.setBackgroundColor(Color.WHITE);
            fiveStarProductBtn.setBackgroundColor(Color.WHITE);
            curQueryParams = new HashMap<>(coreQueryParams);
            new Handler().postDelayed(() -> {
                page = 1;
                GetDataFromApi(page, coreQueryParams, true, true);
            }, 300);


        });
        minimumPriceProductBtn.setOnClickListener(v -> {

            if (stateFilter == 2) return;
            stateFilter = 2;
            productList.clear();
            productsAdapter.notifyDataSetChanged();
//            productListRclView.setAdapter(new Adapter_ProductPreviewRclView(ProductListActivity.this, productList));

            startShimmer();
            minimumPriceProductBtn.setBackgroundResource(R.drawable.shape_rectangle_solid_bottom_border_main);
            latestProductBtn.setBackgroundColor(Color.WHITE);
            fiveStarProductBtn.setBackgroundColor(Color.WHITE);
            new Handler().postDelayed(() -> {
                page = 1;
                curQueryParams = new HashMap<>(coreQueryParams);
                curQueryParams.put("order_col", "price_sell");
                curQueryParams.put("order_type", "ASC");
                GetDataFromApi(page, curQueryParams, true, true);

            }, 300);
        });
        fiveStarProductBtn.setOnClickListener(v -> {

            if (stateFilter == 3) return;
            stateFilter = 3;
            productList.clear();
            productsAdapter.notifyDataSetChanged();
//            productListRclView.setAdapter(new Adapter_ProductPreviewRclView(ProductListActivity.this, productList));
            startShimmer();
            fiveStarProductBtn.setBackgroundResource(R.drawable.shape_rectangle_solid_bottom_border_main);
            latestProductBtn.setBackgroundColor(Color.WHITE);
            minimumPriceProductBtn.setBackgroundColor(Color.WHITE);
            new Handler().postDelayed(() -> {
                page = 1;
                curQueryParams = new HashMap<>(coreQueryParams);
                curQueryParams.put("rating", "5");
                GetDataFromApi(page, curQueryParams, true, true);

            }, 300);


        });
    }

    private void GetDataFromApi(Integer page, Map<String, String> params, boolean isFirstGet, boolean isOverrideData) {
        if (!isFirstGet && page > totalPage) {
            if (!isWaiting) loadingTxtView.setVisibility(View.GONE);
            return;
        }
        if (params != null) {
            params.put("product_per_page", "6");
            params.put("page", page.toString());
        }

        apiServices.getProductList(params).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = response.body();
                    if (pr != null && page == 1 && pr.totalPage == 0)
                        findViewById(R.id.no_product_textView).setVisibility(View.VISIBLE);
                    if (pr != null) {
                        if (isOverrideData) productList.clear();
                        for (ProductPreviewItem item :
                                pr.data) {
                            productList.add(new ProductPreview(item));
                        }
                        totalPage = pr.totalPage;
                    }
                    if (!isWaiting) findViewById(R.id.loadingText).setVisibility(View.GONE);
                    productList_ProductPreviewShimmerContainer1.setVisibility(View.GONE);
                    productList_ProductPreviewShimmerContainer2.setVisibility(View.GONE);
//                    productListRclView.setAdapter(new Adapter_ProductPreviewRclView(ProductListActivity.this, productList));
                    productsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductListActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(() -> {
                    isWaiting = false;
                    loadingTxtView.setVisibility(View.GONE);
                }, 400);
            }

            @Override
            public void onFailure(@NonNull Call<ProductListResponse> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void refChildComponent() {
        productListRclView = findViewById(R.id.productList_rclview);
        scrollView = findViewById(R.id.scrollView);
        loadingTxtView = findViewById(R.id.loadingText);
        backBtn = findViewById(R.id.backBtn);
        productList_ProductPreviewShimmerContainer1 = findViewById(R.id.ProductList_ProductPreviewShimmerContainer1);
        productList_ProductPreviewShimmerContainer2 = findViewById(R.id.ProductList_ProductPreviewShimmerContainer2);
        latestProductBtn = findViewById(R.id.ProductList_LatestProductBtn);
        minimumPriceProductBtn = findViewById(R.id.ProductList_MinimumProductBtn);
        fiveStarProductBtn = findViewById(R.id.ProductList_FiveStarProductBtn);
    }

}