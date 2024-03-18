package com.ktm.kthtechshop.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.dto.CategoryItemViewModel;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.ProductPreviewItem;
import com.ktm.kthtechshop.dto.PromotionBannerItem;
import com.ktm.kthtechshop.localhostIp;
import com.ktm.kthtechshop.others.CategoryViewModel;
import com.ktm.kthtechshop.staticData.PromotionBannerType;
import com.ktm.kthtechshop.utils.ImageLoadFromURL;
import com.ktm.kthtechshop.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageFragment extends Fragment {
    private ApiServiceObject apiServiceObject;
    static ArrayList<PromotionBannerItem> promotionStaticBannerList;
    static ArrayList<SlideModel> promotionBannerSlideItems;
    static CategoryViewModel categoryViewModel;
    private RecyclerView categoryRclView, promotionStaticBannerRclView, flashSaleRclView, allProductRclView, smartphoneProductRclView, laptopProductRclView;

    private ArrayList<ProductPreviewItem> flashSaleProductList, allProductArrayList, smartphoneProductList, laptopProductList;
    private ImageSlider promotionBannerSlider;
    private AppSharedPreferences appSharedPreferences;
    private ImageView userImgView;
    private Button smartPhoneBtnMore, laptopBtnMore, latestProductBtnMore;
    private boolean isCheckingAccount = true;
    private ImageButton cartBtn;
    public final Integer smartphoneCategoryId = 1, laptopCategoryId = 2;
    private ArrayList<CategoryItem> categoryList;
    private ArrayList<CategoryItemViewModel> categoryItemArrayList;
    private ShimmerFrameLayout shimmerFrameLayoutCategory1, shimmerFrameLayoutCategory2, shimmerFrameLayoutCategory3, shimmerFrameLayoutCategory4;
    private LinearLayout shimmerFrameLayoutCategoryContainer;
    protected EditText searchEditText;

    //    private Call<ProductListResponse> callLatestProduct, callLaptopProduct, callSmartPhoneProduct;

    private Context context;
    private Activity activity;
    View view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home_page, container, false);
        activity = getActivity();
        appSharedPreferences = new AppSharedPreferences((AppCompatActivity) context);
        apiServiceObject = new ApiServiceObject();
        //Init Array Lists
        initLists();
        //ref child view component
        refChildComponents();
        //setup rclviews
        setUpRclViews();
        //Call api
        checkValidUserSession();
        //singleton categoryViewModel
        if (categoryViewModel == null) {
            shimmerFrameLayoutCategory1.startShimmer();
            shimmerFrameLayoutCategory2.startShimmer();
            shimmerFrameLayoutCategory3.startShimmer();
            shimmerFrameLayoutCategory4.startShimmer();
            categoryViewModel = new ViewModelProvider((ViewModelStoreOwner) activity).get(CategoryViewModel.class);
        }
        //set callback function on getCategories

        categoryViewModel.getCategories().observe((LifecycleOwner) activity, categories -> {
            if (categories != null) {
                categoryItemArrayList = categories;
                shimmerFrameLayoutCategoryContainer.setVisibility(View.GONE);
                categoryRclView.setAdapter(new Adapter_CategoryRclView(context, categories));
            }

        });
        //is categories  unload
        if (categoryViewModel.getCategories().getValue() == null) {
            // Fetch data from server and set to ViewModel
            handleGetCategoriesFromApi();
        }


        //Get item
        getPromotionBanner();
        //FlashSale is hardcode now
        getFlashSaleProduct();
        //Get Product
        getLatestProduct();
        getSmartPhoneProduct();
        getLaptopProduct();
        //setup button click listener
        setUpButtonsOnClickListen();
        setUpSearchFunction();
        return view;

    }

    private void handleGetCategoriesFromApi() {
        apiServiceObject.apiServices.getCategoryList().enqueue(new Callback<ArrayList<CategoryItem>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CategoryItem>> call, @NonNull Response<ArrayList<CategoryItem>> response) {
                if (response.isSuccessful()) {
                    Executor executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    categoryItemArrayList.clear();
                    categoryList = response.body();
                    if (categoryList != null)
                        for (int i = 0; i < categoryList.size(); i++) {
                            int finalI1 = i;
                            executor.execute(() -> {
                                // Công việc nền, ví dụ: tải bitmap từ URL
                                CategoryItem ct = categoryList.get(finalI1);
                                Bitmap bitmap = Utils.getBitmapFromURL(localhostIp.LOCALHOST_IP.getValue() + ":3000" + ct.getIcon());
                                handler.post(() -> {
                                    categoryItemArrayList.add(new CategoryItemViewModel(ct.getName(), ct.getIcon(), ct.getId(), bitmap));
                                    if (finalI1 == (categoryList.size() - 1)) {
                                        categoryViewModel.setCategories(categoryItemArrayList);
                                        //it will run to  callback function which was  set in behind
                                    }
                                });

                            });

                        }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CategoryItem>> call, @NonNull Throwable t) {
                Toast.makeText(activity, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void refChildComponents() {
        searchEditText = view.findViewById(R.id.Global_ProductSearch_EditText);
        promotionBannerSlider = view.findViewById(R.id.promotionBannerSlider);
        categoryRclView = view.findViewById(R.id.rclview_category_list);
        promotionStaticBannerRclView = view.findViewById(R.id.promotionStaticBanner);
        allProductRclView = view.findViewById(R.id.allProductlist_rclview);
        smartphoneProductRclView = view.findViewById(R.id.smartphone_rclview);
        laptopProductRclView = view.findViewById(R.id.laptop_rclview);
        flashSaleRclView = view.findViewById(R.id.flashsale_rclview);
        userImgView = view.findViewById(R.id.HomePage_UserIcon_imgView);
        smartPhoneBtnMore = view.findViewById(R.id.HomPage_Btn_smartphoneHeader_more);
        laptopBtnMore = view.findViewById(R.id.HomPage_Btn_LaptopHeader_more);
        latestProductBtnMore = view.findViewById(R.id.HomPage_Btn_newProductHeader_more);

        shimmerFrameLayoutCategory1 = view.findViewById(R.id.HomePage_ShimmerCategory1);
        shimmerFrameLayoutCategory2 = view.findViewById(R.id.HomePage_ShimmerCategory2);
        shimmerFrameLayoutCategory3 = view.findViewById(R.id.HomePage_ShimmerCategory3);
        shimmerFrameLayoutCategory4 = view.findViewById(R.id.HomePage_ShimmerCategory4);
        shimmerFrameLayoutCategoryContainer = view.findViewById(R.id.HomePage_ShimmerCategory_Container);
    }

    protected void initLists() {

        categoryItemArrayList = new ArrayList<>();
        flashSaleProductList = new ArrayList<>();
        allProductArrayList = new ArrayList<>();
        categoryList = new ArrayList<>();
    }

    protected void setUpSearchFunction() {
        if (searchEditText == null) return;
        searchEditText.setSingleLine();
        searchEditText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                Intent it = new Intent(v.getContext(), ProductListActivity.class);
                Map<String, String> mp = new HashMap<String, String>();
                String searchTerm = searchEditText.getText().toString();
                if (!searchTerm.isEmpty()) mp.put("keyword", searchTerm);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("queryParams", (Serializable) mp);
                v.getContext().startActivity(it);
                return true;
            }
            return false;
        });
    }

    protected void setUpRclViews() {
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        categoryRclView.setLayoutManager(flexboxLayoutManager);
        categoryRclView.setHasFixedSize(true);
        promotionStaticBannerRclView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        allProductRclView.setLayoutManager(new GridLayoutManager(context, 2));
        promotionStaticBannerRclView.setHasFixedSize(true);
        allProductRclView.setHasFixedSize(true);
        flashSaleRclView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        flashSaleRclView.setHasFixedSize(true);
        smartphoneProductRclView.setLayoutManager(new GridLayoutManager(context, 2));
        smartphoneProductRclView.setHasFixedSize(true);
        laptopProductRclView.setLayoutManager(new GridLayoutManager(context, 2));
        laptopProductRclView.setHasFixedSize(true);
    }

    private void getPromotionBanner() {
        if (promotionBannerSlideItems == null && promotionStaticBannerList == null) {
            promotionBannerSlideItems = new ArrayList<>();
            promotionStaticBannerList = new ArrayList<>();
            ArrayList<SlideModel> tmp = new ArrayList<SlideModel>();
            tmp.add(new SlideModel(R.drawable.img_loading_text, ScaleTypes.CENTER_INSIDE));
            promotionBannerSlider.setImageList(tmp);
            apiServiceObject.apiServices.getListPromotionBanner().enqueue(new Callback<ArrayList<PromotionBannerItem>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<PromotionBannerItem>> call, @NonNull Response<ArrayList<PromotionBannerItem>> response) {
                    if (response.isSuccessful()) {
                        ArrayList<PromotionBannerItem> tmp = response.body();
                        //Split to static banner and carousel banner
                        if (tmp != null && tmp.size() > 0) {
                            for (PromotionBannerItem item : tmp) {
                                item.setImage(localhostIp.LOCALHOST_IP.getValue() + ":3000" + item.getImage());
                                if (item.getType() == PromotionBannerType.CAROUSEL.getValue()) {
                                    promotionBannerSlideItems.add(new SlideModel(item.getImage(), ScaleTypes.CENTER_INSIDE));
                                } else {
                                    promotionStaticBannerList.add(item);
                                }
                            }

                        }
                        if (promotionStaticBannerList.size() != 0)
                            promotionStaticBannerRclView.setVisibility(View.VISIBLE);
                        promotionStaticBannerRclView.setAdapter(new Adapter_PromotionBannerStaticRclView(context, promotionStaticBannerList));
                        promotionBannerSlider.setImageList(promotionBannerSlideItems);

                    } else {
                        promotionStaticBannerRclView.setVisibility(View.GONE);
                        Toast.makeText(activity, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<PromotionBannerItem>> call, @NonNull Throwable t) {
                    promotionStaticBannerRclView.setVisibility(View.GONE);
                    Toast.makeText(activity, "Call api failed", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            if (promotionStaticBannerList.size() == 0)
                promotionStaticBannerRclView.setVisibility(View.GONE);
            promotionStaticBannerRclView.setAdapter(new Adapter_PromotionBannerStaticRclView(context, promotionStaticBannerList));
            promotionBannerSlider.setImageList(promotionBannerSlideItems);
        }

    }

    private void getFlashSaleProduct() {
        float rating = 3.4f;
        Integer productId = 1;
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 ", 20000000, 20000000, productId, rating));
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 ", 20000000, 20000000, productId, rating));
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 abc  ", 3000000, 200000, 20, productId, rating));
        flashSaleProductList.add(new ProductPreviewItem(" ", "samsung S23 Ultra 2023 abc", 3000000, 200000, 20, productId, rating));
        flashSaleRclView.setAdapter(new Adapter_FlashSalePreviewRclView(context, flashSaleProductList));

    }

    private void getLatestProduct() {
        Map<String, String> mp = new HashMap<String, String>();
        mp.put("product_per_page", "6");
        apiServiceObject.apiServices.getProductList(mp).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = (ProductListResponse) (response.body());
                    if (pr != null && pr.totalPage > 0)
                        view.findViewById(R.id.allProductNoProduct_textView).setVisibility(View.INVISIBLE);
                    assert pr != null;
                    allProductArrayList = pr.data;
                    allProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, allProductArrayList));
                } else {
                    Toast.makeText(activity, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductListResponse> call, @NonNull Throwable t) {
                Toast.makeText(activity, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSmartPhoneProduct() {
        Map<String, String> mp1 = new HashMap<String, String>();
        mp1.put("category_id", "1");
        apiServiceObject.apiServices.getProductList(mp1).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = (ProductListResponse) (response.body());
                    if (pr != null && pr.totalPage > 0)
                        view.findViewById(R.id.smartphoneListNoProduct_textView).setVisibility(View.INVISIBLE);
                    assert pr != null;
                    smartphoneProductList = pr.data;
                    smartphoneProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, smartphoneProductList));
                } else {
                    Toast.makeText(activity, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductListResponse> call, @NonNull Throwable t) {
                Toast.makeText(activity, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLaptopProduct() {
        Map<String, String> mp2 = new HashMap<String, String>();
        mp2.put("category_id", "2");
        apiServiceObject.apiServices.getProductList(mp2).enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                if (response.isSuccessful()) {
                    ProductListResponse pr = (ProductListResponse) (response.body());
                    if (pr != null && pr.totalPage > 0)
                        view.findViewById(R.id.laptop_no_product_textView).setVisibility(View.INVISIBLE);
                    assert pr != null;
                    laptopProductList = pr.data;
                    laptopProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, laptopProductList));
                } else {
                    Toast.makeText(activity, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductListResponse> call, @NonNull Throwable t) {
                Toast.makeText(activity, "Call api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkValidUserSession() {
        String accessToken = appSharedPreferences.getAccessToken();
        if (!accessToken.isEmpty() || appSharedPreferences.getIsAuth()) {
            apiServiceObject.apiServices.loginWithAccessToken("Bearer " + accessToken).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        appSharedPreferences.setAllAttribute(response.body().value.userId,
                                response.body().accessToken, response.body().value.firstName,
                                response.body().value.avatar);
                        new ImageLoadFromURL(localhostIp.LOCALHOST_IP.getValue() + ":3000" + response.body().value.avatar, userImgView, R.drawable.icon_user).execute();
                    } else {
                        Toast.makeText(activity, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
//                        appSharedPreferences.clear();
                        appSharedPreferences.setIsAuth(false);
                        userImgView.setImageResource(R.drawable.icon_user);
                    }
                    isCheckingAccount = false;

                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Toast.makeText(activity, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
//                    appSharedPreferences.clear();
                    appSharedPreferences.setIsAuth(false);
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

        ImageButton cartBtn = view.findViewById(R.id.HomePage_CartBtn);
        cartBtn.setOnClickListener(v -> {
            Intent it = new Intent(activity, CartActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(it);
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
        latestProductBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(it);
        });
    }
}