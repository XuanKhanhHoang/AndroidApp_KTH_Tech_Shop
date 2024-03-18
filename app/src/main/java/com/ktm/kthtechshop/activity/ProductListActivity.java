package com.ktm.kthtechshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapter.Adapter_ProductPreviewRclView;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.ProductPreviewItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends HaveProductSearchApiActivity {

    private ArrayList<ProductPreviewItem> productList;

    private Adapter_ProductPreviewRclView adapterProductPreviewRclView;

    private RecyclerView productListRclView;
    private Integer page, totalPage;
    private ScrollView scrollView;
    private TextView loadingTxtView;
    private Map<String, String> coreQueryParams;
    private boolean isWaiting = false;
    private ImageButton backBtn;
    private LinearLayout productList_ProductPreviewShimmerContainer1, productList_ProductPreviewShimmerContainer2;
    private ShimmerFrameLayout productList_ProductPreviewShimmer1, productList_ProductPreviewShimmer2, productList_ProductPreviewShimmer3, productList_ProductPreviewShimmer4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApiService();
        setContentView(R.layout.activity_product_list);
        page = 1;
        totalPage = 0;
        productListRclView = findViewById(R.id.productList_rclview);
        scrollView = findViewById(R.id.scrollView);
        loadingTxtView = findViewById(R.id.loadingText);
        backBtn = findViewById(R.id.backBtn);
        productList_ProductPreviewShimmerContainer1 = findViewById(R.id.ProductList_ProductPreviewShimmerContainer1);
        productList_ProductPreviewShimmerContainer2 = findViewById(R.id.ProductList_ProductPreviewShimmerContainer2);
        productList_ProductPreviewShimmer1 = findViewById(R.id.ProductList_ProductPreviewShimmer1);
        productList_ProductPreviewShimmer2 = findViewById(R.id.ProductList_ProductPreviewShimmer2);
        productList_ProductPreviewShimmer3 = findViewById(R.id.ProductList_ProductPreviewShimmer3);
        productList_ProductPreviewShimmer4 = findViewById(R.id.ProductList_ProductPreviewShimmer4);
        productList_ProductPreviewShimmer1.startShimmer();
        productList_ProductPreviewShimmer2.startShimmer();
        productList_ProductPreviewShimmer3.startShimmer();
        productList_ProductPreviewShimmer4.startShimmer();

        productListRclView.setLayoutManager(new GridLayoutManager(this, 2));
        productListRclView.setHasFixedSize(true);
        productListRclView.setNestedScrollingEnabled(false);
        productList = new ArrayList<>();
        adapterProductPreviewRclView = new Adapter_ProductPreviewRclView(getApplicationContext(), productList);
        productListRclView.setAdapter(adapterProductPreviewRclView);
        Intent intent = getIntent();
        coreQueryParams = new HashMap<>();
        if (intent != null && intent.getSerializableExtra("queryParams") != null)
            coreQueryParams = (HashMap<String, String>) intent.getSerializableExtra("queryParams");
        setUpSearchFunction();
        if (coreQueryParams.get("keyword") != null && !coreQueryParams.get("keyword").isEmpty() && searchEditText != null) {
            searchEditText.setText(coreQueryParams.get("keyword"));
        }
        GetDataFromApi(page, true);
        backBtn.setOnClickListener(v -> {
            finish();
        });
        scrollView.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                ViewGroup viewGroup = (ViewGroup) v;
                if (!isWaiting && scrollY == viewGroup.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (page < totalPage + 2) page++;
                    loadingTxtView.setVisibility(View.VISIBLE);
                    isWaiting = true;
                    new Handler().postDelayed(() -> {
                        isWaiting = false;
                        loadingTxtView.setVisibility(View.GONE);
                    }, 1300);
                    GetDataFromApi(page, false);
                }
            }
        });

    }

    private void GetDataFromApi(Integer page, boolean isFirstGet) {
        if (!isFirstGet && page > totalPage) {
            Toast.makeText(this, "That's all the data..", Toast.LENGTH_SHORT).show();
            if (!isWaiting) loadingTxtView.setVisibility(View.GONE);
            return;
        }

        coreQueryParams.put("product_per_page", "6");
        coreQueryParams.put("page", page.toString());
        apiServices.getProductList(coreQueryParams).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = response.body();
                    if (pr != null && page == 1 && pr.totalPage == 0)
                        findViewById(R.id.no_product_textView).setVisibility(View.VISIBLE);
                    if (pr != null) productList.addAll(pr.data);
                    if (!isWaiting) findViewById(R.id.loadingText).setVisibility(View.GONE);
                    productList_ProductPreviewShimmer1.stopShimmer();
                    productList_ProductPreviewShimmer2.stopShimmer();
                    productList_ProductPreviewShimmer3.stopShimmer();
                    productList_ProductPreviewShimmer4.stopShimmer();
                    productList_ProductPreviewShimmerContainer1.setVisibility(View.GONE);
                    productList_ProductPreviewShimmerContainer2.setVisibility(View.GONE);
                    adapterProductPreviewRclView.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProductListActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


}