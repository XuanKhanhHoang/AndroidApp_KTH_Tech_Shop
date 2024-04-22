package com.ktm.kthtechshop.activity_and_fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapters.Adapter_ProductPreviewRclView;
import com.ktm.kthtechshop.adapters.Adapter_Spinner_FilterBrandItem;
import com.ktm.kthtechshop.adapters.Adapter_Spinner_FilterCategoryItem;
import com.ktm.kthtechshop.dto.Brand;
import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.ProductPreviewItem;
import com.ktm.kthtechshop.model.ProductPreview;
import com.ktm.kthtechshop.utils.AppSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends HaveProductSearchApiActivity {

    private ArrayList<ProductPreview> productList;

    private ArrayList<CategoryItem> catList;
    private ArrayList<Brand> brList;
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
    private AppSharedPreferences appSharedPreferences;
    private ArrayList<CategoryItem> categoryItems;
    private ArrayList<Brand> brandItems;

    private void startShimmer() {
        productList_ProductPreviewShimmerContainer1.setVisibility(View.VISIBLE);
        productList_ProductPreviewShimmerContainer2.setVisibility(View.VISIBLE);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApiService();
        appSharedPreferences = new AppSharedPreferences(this);
        setContentView(R.layout.activity_product_list);
        refChildComponent();
        backBtn.setOnClickListener(v -> {
            finish();
        });
        startShimmer();

        productListRclView.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
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
        handleFilter();
        filterBtn.setOnClickListener(v -> {
            findViewById(R.id.ProductList_FilterDrawer).setVisibility(View.VISIBLE);
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
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = response.body();
                    if (pr != null && page == 1 && pr.totalPage == 0) {
                        findViewById(R.id.no_product_textView).setVisibility(View.VISIBLE);
                        productList.clear();
                        totalPage = 0;
                        productsAdapter.notifyDataSetChanged();
                    } else if (pr != null) {
                        findViewById(R.id.no_product_textView).setVisibility(View.GONE);
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
        filterBtn = findViewById(R.id.ProductList_FilterBtn);
    }

    private void handleFilter() {
//        String categoryJson = appSharedPreferences.getCategory();
        Spinner cSp = findViewById(R.id.FilterProductLayout_CategorySpinner);
        catList = new ArrayList<>();
        catList.add(new CategoryItem("Không chọn", "", "-1"));
        final int[] id = {0};
        apiServices.getCategoryList().enqueue(new Callback<ArrayList<CategoryItem>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryItem>> call, Response<ArrayList<CategoryItem>> response) {
                if (response.isSuccessful()) {
                    catList.addAll(response.body());
                    if (coreQueryParams.get("category_id") != null)
                        for (int i = 0; i < response.body().size(); i++) {
                            if (response.body().get(i).getId().equals(coreQueryParams.get("category_id"))) {
                                id[0] = i;
                                break;
                            }
                        }

                    Adapter_Spinner_FilterCategoryItem ad = new Adapter_Spinner_FilterCategoryItem(ProductListActivity.this, android.R.layout.simple_spinner_item, catList);
                    cSp.setAdapter(ad);
                    cSp.setSelection(id[0]);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CategoryItem>> call, @NonNull Throwable t) {

            }
        });

        Spinner bSp = findViewById(R.id.FilterProductLayout_BrandSpinner);
        brandItems = new ArrayList<>();
        brList = new ArrayList<>();
        brList.add(new Brand(-1, "Không chọn"));
        apiServices.getBrandList().enqueue(new Callback<ArrayList<Brand>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Brand>> call, @NonNull Response<ArrayList<Brand>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    brList.addAll(response.body());
                    Adapter_Spinner_FilterBrandItem ad = new Adapter_Spinner_FilterBrandItem(ProductListActivity.this, android.R.layout.simple_spinner_item, brList);
                    bSp.setAdapter(ad);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Brand>> call, @NonNull Throwable t) {

            }
        });

        TextInputLayout maxP, minP;
        maxP = findViewById(R.id.FilterProductLayout_MaxPrice);
        minP = findViewById(R.id.FilterProductLayout_MinPrice);

        setFilterEDTLayout(minP);
        setFilterEDTLayout(maxP);
        findViewById(R.id.FilterProductLayout_Cancel).setOnClickListener(v ->
                findViewById(R.id.ProductList_FilterDrawer).setVisibility(View.GONE)
        );
        findViewById(R.id.FilterProductLayout_Filter).setOnClickListener(v -> {
            String minPrice = minP.getEditText().getText().toString();
            String maxPrice = maxP.getEditText().getText().toString();
            int brandId = ((Brand) bSp.getSelectedItem()).id;
            String categoryId = ((CategoryItem) cSp.getSelectedItem()).getId();
            page = 1;


            findViewById(R.id.ProductList_FilterDrawer).setVisibility(View.GONE);
            curQueryParams = new HashMap<>(coreQueryParams);
            if (!maxPrice.isEmpty()) curQueryParams.put("max_price", maxPrice);
            if (!minPrice.isEmpty()) curQueryParams.put("min_price", minPrice);
            if (brandId != -1) curQueryParams.put("brand_id", String.valueOf(brandId));
            if (!categoryId.equals("-1")) curQueryParams.put("category_id", categoryId);
            GetDataFromApi(page, curQueryParams, true, true);
        });
    }

    private void setFilterEDTLayout(TextInputLayout t) {
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            try {
                Integer input = Integer.parseInt(source.toString());
                if (source.length() == 0) {
                    return null;
                }
                if (input >= 0) {
                    if (t.getEditText().getText().length() == 0 && input > 0) {
                        return null;
                    }
                    try {
                        Integer nextVal = Integer.parseInt(t.getEditText().getText().toString() + input.toString());
                        if (nextVal > 0) {
                            return null;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            } catch (NumberFormatException e) {
            }
            return "";
        };
        t.getEditText().setFilters(new InputFilter[]{inputFilter});
    }
}
