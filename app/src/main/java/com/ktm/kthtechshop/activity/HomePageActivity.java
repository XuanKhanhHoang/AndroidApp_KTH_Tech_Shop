package com.ktm.kthtechshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.ktm.kthtechshop.AppSharedPreferences;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapter.Adapter_CategoryRclView;
import com.ktm.kthtechshop.adapter.Adapter_FlashSalePreviewRclView;
import com.ktm.kthtechshop.adapter.Adapter_ProductPreviewRclView;
import com.ktm.kthtechshop.adapter.Adapter_PromotionBannerStaticRclView;
import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.ProductPreviewItem;
import com.ktm.kthtechshop.dto.PromotionBannerItem;
import com.ktm.kthtechshop.localhostIp;
import com.ktm.kthtechshop.others.CategoryViewModel;
import com.ktm.kthtechshop.staticData.PromotionBannerType;
import com.ktm.kthtechshop.utils.ImageLoadFromURL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageActivity extends HaveProductSearchApiActivity {

    private RecyclerView categoryRclView, promotionStaticBannerRclView, flashSaleRclView, allProductRclView, smartphoneProductRclView, laptopProductRclView;
    private ArrayList<SlideModel> promotionBannerSlideItems;
    private ArrayList<PromotionBannerItem> promotionBannerList, promotionStaticBannerList;
    private ArrayList<CategoryItem> categoryItemArrayList;
    private ArrayList<ProductPreviewItem> flashSaleProductList, allProductArrayList, smartphoneProductList, laptopProductList;
    private ImageSlider promotionBannerSlider;
    private AppSharedPreferences appSharedPreferences;
    private ImageView userImgView;
    private Button smartPhoneBtnMore, laptopBtnMore, latestProductBtnMore;
    private boolean isCheckingAccount = true;
    private ImageButton cartBtn;
    public final Integer smartphoneCategoryId = 1, laptopCategoryId = 2;
    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSharedPreferences = new AppSharedPreferences(this);
        setContentView(R.layout.activity_home_page);
        //Init api service
        initApiService();
        //Init Array Lists
        initLists();
        //ref child view component
        refChildComponents();
        //setup rclviews
        setUpRclViews();
        //Call api
        checkValidUserSession();
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getCategories().observe(this, categories -> {
            // Update UI
        });

        // Check if categories data is already loaded
        if (categoryViewModel.getCategories().getValue() == null) {
            // Fetch data from server and set to ViewModel
        }
        getPromotionBanner();
        categoryItemArrayList.add(new CategoryItem("Loading...", "1", "-1"));
        categoryRclView.setAdapter(new Adapter_CategoryRclView(getApplicationContext(), categoryItemArrayList));
        ArrayList tmp = new ArrayList<SlideModel>();
        tmp.add(new SlideModel(R.drawable.img_loading_text, ScaleTypes.FIT));
        promotionBannerSlider.setImageList(tmp);
        getCategory();
        getFlashSaleProduct();
        getLatestProduct();
        getSmartPhoneProduct();
        getLaptopProduct();
        //setup button click listener
        setUpButtonsOnClickListen();
        setUpSearchFunction();

    }

    protected void refChildComponents() {
        promotionBannerSlider = findViewById(R.id.promotionBannerSlider);
        categoryRclView = findViewById(R.id.rclview_category_list);
        promotionStaticBannerRclView = findViewById(R.id.promotionStaticBanner);
        allProductRclView = findViewById(R.id.allProductlist_rclview);
        smartphoneProductRclView = findViewById(R.id.smartphone_rclview);
        laptopProductRclView = findViewById(R.id.laptop_rclview);
        flashSaleRclView = findViewById(R.id.flashsale_rclview);
        userImgView = findViewById(R.id.HomePage_UserIcon_imgView);
        smartPhoneBtnMore = findViewById(R.id.HomPage_Btn_smartphoneHeader_more);
        laptopBtnMore = findViewById(R.id.HomPage_Btn_LaptopHeader_more);
        latestProductBtnMore = findViewById(R.id.HomPage_Btn_newProductHeader_more);

    }

    protected void initLists() {
        promotionBannerSlideItems = new ArrayList<>();
        promotionBannerList = new ArrayList<>();
        promotionStaticBannerList = new ArrayList<>();
        categoryItemArrayList = new ArrayList<>();
        flashSaleProductList = new ArrayList<>();
        allProductArrayList = new ArrayList<>();
    }

    protected void setUpRclViews() {
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getApplicationContext());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        categoryRclView.setLayoutManager(flexboxLayoutManager);
        categoryRclView.setHasFixedSize(true);
        promotionStaticBannerRclView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        allProductRclView.setLayoutManager(new GridLayoutManager(this, 2));
        promotionStaticBannerRclView.setHasFixedSize(true);
        allProductRclView.setHasFixedSize(true);
        flashSaleRclView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        flashSaleRclView.setHasFixedSize(true);
        smartphoneProductRclView.setLayoutManager(new GridLayoutManager(this, 2));
        smartphoneProductRclView.setHasFixedSize(true);
        laptopProductRclView.setLayoutManager(new GridLayoutManager(this, 2));
        laptopProductRclView.setHasFixedSize(true);
    }

    private void getPromotionBanner() {
        apiServices.getListPromotionBanner().enqueue(new Callback<ArrayList<PromotionBannerItem>>() {
            @Override
            public void onResponse(Call<ArrayList<PromotionBannerItem>> call, Response<ArrayList<PromotionBannerItem>> response) {
                if (response.isSuccessful()) {
                    promotionBannerList = response.body();
                    //Split to static banner and carousel banner
                    if (promotionBannerList != null && promotionBannerList.size() > 0) {
                        for (PromotionBannerItem item : promotionBannerList) {
                            item.setImage(localhostIp.LOCALHOST_IP.getValue() + ":3000" + item.getImage());
                            if (item.getType() == PromotionBannerType.CAROUSEL.getValue()) {
                                promotionBannerSlideItems.add(new SlideModel(item.getImage(), ScaleTypes.FIT));

                            } else {
                                promotionStaticBannerList.add(item);
                            }
                        }

                    }
                    promotionStaticBannerRclView.setVisibility(View.VISIBLE);
                    promotionBannerSlider.setImageList(promotionBannerSlideItems);
                    promotionStaticBannerRclView.setAdapter(new Adapter_PromotionBannerStaticRclView(getApplicationContext(), promotionStaticBannerList));

                } else {
                    Toast.makeText(HomePageActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PromotionBannerItem>> call, Throwable t) {
                Toast.makeText(HomePageActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCategory() {
        apiServices.getCategoryList().enqueue(new Callback<ArrayList<CategoryItem>>() {
            @Override
            public void onResponse(Call<ArrayList<CategoryItem>> call, Response<ArrayList<CategoryItem>> response) {
                if (response.isSuccessful()) {
                    categoryItemArrayList = response.body();
                    categoryRclView.setAdapter(new Adapter_CategoryRclView(getApplicationContext(), categoryItemArrayList));
                } else {
                    Toast.makeText(HomePageActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CategoryItem>> call, Throwable t) {
                Toast.makeText(HomePageActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFlashSaleProduct() {
        float rating = 3.4f;
        Integer productId = 1;
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 ", 20000000, 20000000, productId, rating));
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 ", 20000000, 20000000, productId, rating));
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 abc  ", 3000000, 200000, 20, productId, rating));
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 abc", 3000000, 200000, 20, productId, rating));
        flashSaleRclView.setAdapter(new Adapter_FlashSalePreviewRclView(getApplicationContext(), flashSaleProductList));

    }

    private void getLatestProduct() {
        Map<String, String> mp = new HashMap<String, String>();
        mp.put("product_per_page", "6");
        apiServices.getProductList(mp).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = (ProductListResponse) (response.body());
                    if (pr != null && pr.totalPage > 0)
                        findViewById(R.id.allProductNoProduct_textView).setVisibility(View.INVISIBLE);
                    allProductArrayList = pr.data;
                    allProductRclView.setAdapter(new Adapter_ProductPreviewRclView(getApplicationContext(), allProductArrayList));
                } else {
                    Toast.makeText(HomePageActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                Toast.makeText(HomePageActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSmartPhoneProduct() {
        Map<String, String> mp1 = new HashMap<String, String>();
        mp1.put("category_id", "1");
        apiServices.getProductList(mp1).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = (ProductListResponse) (response.body());
                    if (pr != null && pr.totalPage > 0)
                        findViewById(R.id.smartphoneListNoProduct_textView).setVisibility(View.INVISIBLE);
                    smartphoneProductList = pr.data;
                    smartphoneProductRclView.setAdapter(new Adapter_ProductPreviewRclView(getApplicationContext(), smartphoneProductList));
                } else {
                    Toast.makeText(HomePageActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                Toast.makeText(HomePageActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLaptopProduct() {
        Map<String, String> mp2 = new HashMap<String, String>();
        mp2.put("category_id", "2");
        apiServices.getProductList(mp2).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = (ProductListResponse) (response.body());
                    if (pr != null && pr.totalPage > 0)
                        findViewById(R.id.laptop_no_product_textView).setVisibility(View.INVISIBLE);
                    laptopProductList = pr.data;
                    laptopProductRclView.setAdapter(new Adapter_ProductPreviewRclView(getApplicationContext(), laptopProductList));
                } else {
                    Toast.makeText(HomePageActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                Toast.makeText(HomePageActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkValidUserSession() {
        String accessToken = appSharedPreferences.getAccessToken();
        if (!accessToken.isEmpty() || appSharedPreferences.getIsAuth()) {
            apiServices.loginWithAccessToken("Bearer " + accessToken).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        appSharedPreferences.setAllAttribute(response.body().value.userId,
                                response.body().accessToken, response.body().value.firstName,
                                response.body().value.avatar);
                        new ImageLoadFromURL(localhostIp.LOCALHOST_IP.getValue() + ":3000" + response.body().value.avatar, userImgView, R.drawable.icon_user).execute();
                    } else {
                        Toast.makeText(HomePageActivity.this, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
                        appSharedPreferences.clear();
                        userImgView.setImageResource(R.drawable.icon_user);
                    }
                    isCheckingAccount = false;

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(HomePageActivity.this, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
                    appSharedPreferences.clear();
                    userImgView.setImageResource(R.drawable.icon_user);
                    isCheckingAccount = false;
                }
            });
        }
        isCheckingAccount = false;
    }

    private void setUpButtonsOnClickListen() {
        userImgView.setOnClickListener(v -> {
            if (!isCheckingAccount) {
                if (!appSharedPreferences.getIsAuth() || appSharedPreferences.getAccessToken().isEmpty()) {
                    Intent it = new Intent(v.getContext(), LoginActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(it);
                } else {
                    //to user activity
                }
            }
        });
        ImageButton cartBtn = findViewById(R.id.HomePage_CartBtn);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(HomePageActivity.this, CartActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HomePageActivity.this.startActivity(it);
            }
        });
        latestProductBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(it);
        });
        smartPhoneBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("category_id", smartphoneCategoryId.toString());
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("queryParams", (Serializable) mp);
            v.getContext().startActivity(it);
        });
        laptopBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("category_id", laptopCategoryId.toString());
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("queryParams", (Serializable) mp);
            v.getContext().startActivity(it);
        });
        userImgView.setOnClickListener(v -> {
            if (!isCheckingAccount) {
                if (!appSharedPreferences.getIsAuth() || appSharedPreferences.getAccessToken().isEmpty()) {
                    Intent it = new Intent(v.getContext(), LoginActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(it);
                } else {
                    //to user activity
                }
            }
        });
        latestProductBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(it);
        });
        smartPhoneBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("category_id", smartphoneCategoryId.toString());
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("queryParams", (Serializable) mp);
            v.getContext().startActivity(it);
        });
        laptopBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("category_id", laptopCategoryId.toString());
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("queryParams", (Serializable) mp);
            v.getContext().startActivity(it);
        });
    }
}