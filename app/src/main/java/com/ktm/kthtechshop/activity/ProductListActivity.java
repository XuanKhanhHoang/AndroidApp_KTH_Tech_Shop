package com.ktm.kthtechshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends NeededCallApiActivity {

    private ArrayList<ProductPreviewItem> productList;

    private Adapter_ProductPreviewRclView adapterProductPreviewRclView;

    private RecyclerView productListRclView;
    private Integer page, totalPage;
    private ScrollView scrollView;
    private TextView loadingTxtView;
    Map<String, String> coreQueryParams;

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
        productListRclView.setLayoutManager(new GridLayoutManager(this, 2));
        productListRclView.setHasFixedSize(true);
        productList = new ArrayList<>();
        adapterProductPreviewRclView = new Adapter_ProductPreviewRclView(getApplicationContext(), productList);
        productListRclView.setAdapter(adapterProductPreviewRclView);
        Intent intent = getIntent();
        coreQueryParams = new HashMap<>();
        coreQueryParams = (HashMap<String, String>) intent.getSerializableExtra("queryParams");
        if (coreQueryParams == null) {
        }
        GetDataFromApi(page, true);

        scrollView.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                ViewGroup viewGroup = (ViewGroup) v;
                if (scrollY == viewGroup.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    if (page < totalPage + 2) page++;
                    loadingTxtView.setVisibility(View.VISIBLE);
                    GetDataFromApi(page, false);
                }
            }
        });

    }

    private void GetDataFromApi(Integer page, boolean isFirstGet) {
        if (!isFirstGet && page > totalPage) {
            Toast.makeText(this, "That's all the data..", Toast.LENGTH_SHORT).show();
            loadingTxtView.setVisibility(View.GONE);
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
                    findViewById(R.id.loadingText).setVisibility(View.GONE);
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