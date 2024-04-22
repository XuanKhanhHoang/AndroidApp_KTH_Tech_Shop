package com.ktm.kthtechshop.activity_and_fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TextView;
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
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapters.Adapter_CategoryRclView;
import com.ktm.kthtechshop.adapters.Adapter_FlashSalePreviewRclView;
import com.ktm.kthtechshop.adapters.Adapter_ProductPreviewRclView;
import com.ktm.kthtechshop.adapters.Adapter_PromotionBannerStaticRclView;
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.dto.CategoryItemViewModel;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.ProductPreviewItem;
import com.ktm.kthtechshop.dto.PromotionBannerItem;
import com.ktm.kthtechshop.model.ProductPreview;
import com.ktm.kthtechshop.static_data.PromotionBannerType;
import com.ktm.kthtechshop.utils.AppGson;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.ImageLoadFromURL;
import com.ktm.kthtechshop.utils.Utils;
import com.ktm.kthtechshop.view_models.CategoryViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageFragment extends Fragment {
    static ArrayList<PromotionBannerItem> promotionStaticBannerList;
    static ArrayList<SlideModel> promotionBannerSlideItems;
    static CategoryViewModel categoryViewModel;
    private ArrayList<ProductPreview> allProductArrayList, smartphoneProductList, tabletProductList;
    static ArrayList<ProductPreviewItem> flashSaleProductList;
    private ApiServiceObject apiServiceObject;
    private RecyclerView categoryRclView, promotionStaticBannerRclView, flashSaleRclView, allProductRclView, smartphoneProductRclView, tabletProductRclView;

    private ImageSlider promotionBannerSlider;
    private AppSharedPreferences appSharedPreferences;
    private ImageView userImgView;
    private Button smartPhoneBtnMore, tabletBtnMore, latestProductBtnMore;
    private boolean isCheckingAccount = true;
    private ImageButton cartBtn;
    public final Integer smartphoneCategoryId = 1, tabletCategoryId = 2;
    private ArrayList<CategoryItem> categoryList;
    private ArrayList<CategoryItemViewModel> categoryItemArrayList;
    private ShimmerFrameLayout shimmerFrameLayoutCategory1, shimmerFrameLayoutCategory2, shimmerFrameLayoutCategory3, shimmerFrameLayoutCategory4;
    private LinearLayout shimmerFrameLayoutCategoryContainer;
    protected EditText searchEditText;

    private Call<ProductListResponse> callLatestProduct, callTabletProduct, callSmartPhoneProduct;
    private Call<ArrayList<PromotionBannerItem>> callPromotionBanner;
    private Call<ArrayList<CategoryItem>> callCategory;
    private Call<LoginResponse> callCheckingValidUserSession;
    private TextView flashSale_h1, flashSale_h2, flashSale_m1, flashSale_m2;

    private boolean smartphoneProductIsSet, latestProductIsSet, tabletProductIsSet;
    private Context context;
    private Activity activity;
    private View view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = getActivity();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        if (!appSharedPreferences.getAccessToken().isEmpty()) checkValidUserSession();
        else {
            userImgView.setImageResource(R.drawable.icon_user);
        }
        if (!tabletProductIsSet) {
            handleTabletProduct();
        }
        if (!smartphoneProductIsSet) {
            handleSmartPhoneProduct();
        }
        if (!latestProductIsSet) {
            handleLatestProduct();
        }

    }

    private HomePageFragment() {
    }

    @SuppressLint("StaticFieldLeak")
    static HomePageFragment homePageFragment;

    public static HomePageFragment getInstance() {
        if (homePageFragment != null) {
            return homePageFragment;
        } else {
            homePageFragment = new HomePageFragment();
            return new HomePageFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_home_page, container, false);
        appSharedPreferences = new AppSharedPreferences((AppCompatActivity) context);
        apiServiceObject = new ApiServiceObject();
        latestProductIsSet = false;
        smartphoneProductIsSet = false;
        tabletProductIsSet = false;
        //ref child view component
        refChildComponents();
        //setup rclViews
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
        categoryViewModel.getCategories().observe((LifecycleOwner) context, categories -> {
            if (categories != null) {
                categoryItemArrayList = categories;
                shimmerFrameLayoutCategoryContainer.setVisibility(View.GONE);
                categoryRclView.setAdapter(new Adapter_CategoryRclView(context, categories));
            }

        });
        //is categories  unload
        if (categoryViewModel.getCategories().getValue() == null) {
            // Fetch data from server and set to ViewModel
            categoryItemArrayList = new ArrayList<>();
            categoryList = new ArrayList<>();
            handleGetCategoriesFromApi();
        }
        //Get item
        getPromotionBanner();
        //FlashSale is hardcode now
        SetFlashSaleProduct();
        //Get Product
        handleLatestProduct();
        handleSmartPhoneProduct();
        handleTabletProduct();
        //setup button click listener
        setUpButtonsOnClickListen();
        setUpSearchFunction();
        return view;

    }

    private void handleGetCategoriesFromApi() {
        callCategory = apiServiceObject.apiServices.getCategoryList();
        callCategory.enqueue(new Callback<ArrayList<CategoryItem>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<CategoryItem>> call, @NonNull Response<ArrayList<CategoryItem>> response) {
                if (response.isSuccessful()) {
                    if (call.isCanceled()) {
                        return;
                    }
                    Executor executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    categoryItemArrayList.clear();
                    categoryList = response.body();
                    appSharedPreferences.setCategory(AppGson.getGson().toJson(categoryList));
                    if (categoryList != null)
                        for (int i = 0; i < categoryList.size(); i++) {
                            int finalI1 = i;
                            executor.execute(() -> {
                                CategoryItem ct = categoryList.get(finalI1);
                                Bitmap bitmap = Utils.getBitmapFromURL(ct.getIcon());
                                handler.post(() -> {
                                    categoryItemArrayList.add(new CategoryItemViewModel(ct.getName(), ct.getIcon(), ct.getId(), bitmap));
                                    if (finalI1 == (categoryList.size() - 1)) {
                                        categoryViewModel.setCategories(categoryItemArrayList);
                                        //it will jump to  callback function which was  set in behind
                                    }
                                });

                            });

                        }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<CategoryItem>> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;
                }
                Toast.makeText(activity, "Có lỗi xảy ra khi tải danh sách loại hàng", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callCategory == null || callPromotionBanner == null || callLatestProduct == null || callTabletProduct == null || callSmartPhoneProduct == null)
            return;
        callCategory.cancel();
        callPromotionBanner.cancel();
        callTabletProduct.cancel();
        callLatestProduct.cancel();
        callSmartPhoneProduct.cancel();
        if (callCheckingValidUserSession == null) return;
        callCheckingValidUserSession.cancel();
    }

    protected void refChildComponents() {
        searchEditText = view.findViewById(R.id.Global_ProductSearch_EditText);
        promotionBannerSlider = view.findViewById(R.id.promotionBannerSlider);
        categoryRclView = view.findViewById(R.id.rclview_category_list);
        promotionStaticBannerRclView = view.findViewById(R.id.promotionStaticBanner);
        allProductRclView = view.findViewById(R.id.allProductlist_rclview);
        smartphoneProductRclView = view.findViewById(R.id.smartphone_rclview);
        tabletProductRclView = view.findViewById(R.id.Tablet_rclview);
        flashSaleRclView = view.findViewById(R.id.HomepageFragment_FlashSale_rclview);
        userImgView = view.findViewById(R.id.HomePage_UserIcon_imgView);
        smartPhoneBtnMore = view.findViewById(R.id.HomPage_Btn_smartphoneHeader_more);
        tabletBtnMore = view.findViewById(R.id.HomPage_Btn_TabletHeader_more);
        latestProductBtnMore = view.findViewById(R.id.HomPage_Btn_newProductHeader_more);
        flashSale_h1 = view.findViewById(R.id.HomepageFragment_FlashSale_h1);
        flashSale_h2 = view.findViewById(R.id.HomepageFragment_FlashSale_h2);
        flashSale_m1 = view.findViewById(R.id.HomepageFragment_FlashSale_m1);
        flashSale_m2 = view.findViewById(R.id.HomepageFragment_FlashSale_m2);
        shimmerFrameLayoutCategory1 = view.findViewById(R.id.HomePage_ShimmerCategory1);
        shimmerFrameLayoutCategory2 = view.findViewById(R.id.HomePage_ShimmerCategory2);
        shimmerFrameLayoutCategory3 = view.findViewById(R.id.HomePage_ShimmerCategory3);
        shimmerFrameLayoutCategory4 = view.findViewById(R.id.HomePage_ShimmerCategory4);
        shimmerFrameLayoutCategoryContainer = view.findViewById(R.id.HomePage_ShimmerCategory_Container);
    }


    private void setUpSearchFunction() {
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

    private void setUpRclViews() {
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
        tabletProductRclView.setLayoutManager(new GridLayoutManager(context, 2));
        tabletProductRclView.setHasFixedSize(true);
    }

    private void getPromotionBanner() {
        if (promotionBannerSlideItems == null && promotionStaticBannerList == null) {
            ArrayList<SlideModel> tmp = new ArrayList<SlideModel>();
            tmp.add(new SlideModel(R.drawable.img_loading_text, ScaleTypes.CENTER_INSIDE));
            promotionBannerSlider.setImageList(tmp);
            callPromotionBanner = apiServiceObject.apiServices.getListPromotionBanner();
            callPromotionBanner.enqueue(new Callback<ArrayList<PromotionBannerItem>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<PromotionBannerItem>> call, @NonNull Response<ArrayList<PromotionBannerItem>> response) {
                    if (call.isCanceled()) {
                        return;
                    }
                    if (response.isSuccessful()) {
                        ArrayList<PromotionBannerItem> tmp = response.body();
                        //Split to static banner and carousel banner
                        promotionBannerSlideItems = new ArrayList<>();
                        promotionStaticBannerList = new ArrayList<>();

                        if (tmp != null && !tmp.isEmpty()) {
                            for (PromotionBannerItem item : tmp) {
                                item.setImage(item.getImage());
                                if (item.getType() == PromotionBannerType.CAROUSEL.getValue()) {
                                    promotionBannerSlideItems.add(new SlideModel(item.getImage(), ScaleTypes.FIT));
                                } else {
                                    promotionStaticBannerList.add(item);
                                }
                            }

                        }
                        if (!promotionStaticBannerList.isEmpty())
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
                    if (call.isCanceled()) {
                        return;
                    }
                    promotionStaticBannerRclView.setVisibility(View.GONE);
                    Toast.makeText(activity, "Có lỗi xảy ra khi tải danh sách khuyến mại", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            if (promotionStaticBannerList.isEmpty())
                promotionStaticBannerRclView.setVisibility(View.GONE);
            promotionStaticBannerRclView.setAdapter(new Adapter_PromotionBannerStaticRclView(context, promotionStaticBannerList));
            promotionBannerSlider.setImageList(promotionBannerSlideItems);
        }

    }

    private void SetFlashSaleProduct() {
        Handler handler = new Handler();
        final long[] remainingTimeInSeconds = new long[1];
        String time = "03:24";//hh:mm
        String[] units = time.split(":");
        int hours = Integer.parseInt(units[0]);
        int minutes = Integer.parseInt(units[1]);
        remainingTimeInSeconds[0] = TimeUnit.HOURS.toSeconds(hours) +
                TimeUnit.MINUTES.toSeconds(minutes);
        Runnable updateElement = new Runnable() {
            @Override
            public void run() {
                if (remainingTimeInSeconds[0] > 0) {
                    long h = TimeUnit.SECONDS.toHours(remainingTimeInSeconds[0]);
                    long m = TimeUnit.SECONDS.toMinutes(remainingTimeInSeconds[0]) % 60;

                    flashSale_h1.setText(String.valueOf(h / 10));
                    flashSale_h2.setText(String.valueOf(h % 10));
                    flashSale_m1.setText(String.valueOf(m / 10));
                    flashSale_m2.setText(String.valueOf(m % 10));
                    remainingTimeInSeconds[0] -= 60;

                    handler.postDelayed(this, 60000);
                }
            }
        };
        handler.post(updateElement);
        if (flashSaleProductList == null) {
            flashSaleProductList = new ArrayList<>();
            float rating = 3.4f;
            Integer productId = 1;
            flashSaleProductList.add(new ProductPreviewItem(" ", "Samsung Galaxy S23 ", 18000000, 20000000, 10, productId, rating));
            flashSaleProductList.add(new ProductPreviewItem(" ", "Samsung Galaxy S22", 18000000, 20000000, 10, productId, rating));
            flashSaleProductList.add(new ProductPreviewItem(" ", "Samsung Galaxy S23 ultra", 18000000, 20000000, 10, productId, rating));
            flashSaleProductList.add(new ProductPreviewItem(" ", "Samsung Galaxy S23 edge", 18000000, 20000000, 10, productId, rating));
            flashSaleProductList.add(new ProductPreviewItem(" ", "Samsung Galaxy S22 plus", 18000000, 20000000, 10, productId, rating));
            flashSaleProductList.add(new ProductPreviewItem(" ", "Samsung Galaxy S22 ultra", 18000000, 20000000, 10, productId, rating));
        }
        flashSaleRclView.setAdapter(new Adapter_FlashSalePreviewRclView(context, flashSaleProductList));


    }

    private void handleLatestProduct() {
        latestProductBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(it);
        });
        if (allProductArrayList == null) {
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("product_per_page", "6");
            mp.put("page", "1");
            callLatestProduct = apiServiceObject.apiServices.getProductList(mp);
            callLatestProduct.enqueue(new Callback<ProductListResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                    if (response.isSuccessful()) {
                        if (call.isCanceled()) {
                            return;
                        }
                        ProductListResponse pr = response.body();
                        if (pr != null && pr.totalPage > 0)
                            view.findViewById(R.id.allProductNoProduct_textView).setVisibility(View.INVISIBLE);
                        assert pr != null;
                        allProductArrayList = new ArrayList<>();
                        for (ProductPreviewItem item :
                                pr.data) {
                            allProductArrayList.add(new ProductPreview(item));
                        }
                        latestProductIsSet = true;
                        allProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, allProductArrayList));
                    } else {
                        Toast.makeText(activity, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductListResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled()) {
                        return;
                    }
                    Toast.makeText(activity, "Có lỗi xảy ra khi tải danh sách sản phẩm mới", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (!allProductArrayList.isEmpty())
                view.findViewById(R.id.allProductNoProduct_textView).setVisibility(View.INVISIBLE);
            allProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, allProductArrayList));
        }
    }

    private void handleSmartPhoneProduct() {
        smartPhoneBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("category_id", smartphoneCategoryId.toString());
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("queryParams", (Serializable) mp);
            v.getContext().startActivity(it);
        });
        if (smartphoneProductList == null) {
            Map<String, String> mp1 = new HashMap<String, String>();
            mp1.put("category_id", "1");
            mp1.put("page", "1");
            callSmartPhoneProduct = apiServiceObject.apiServices.getProductList(mp1);
            callSmartPhoneProduct.enqueue(new Callback<ProductListResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                    if (response.isSuccessful()) {
                        if (call.isCanceled()) {
                            return;
                        }
                        ProductListResponse pr = response.body();
                        if (pr != null && pr.totalPage > 0)
                            view.findViewById(R.id.smartphoneListNoProduct_textView).setVisibility(View.INVISIBLE);
                        assert pr != null;
                        smartphoneProductIsSet = true;
                        smartphoneProductList = new ArrayList<>();
                        for (ProductPreviewItem item :
                                pr.data) {
                            smartphoneProductList.add(new ProductPreview(item));
                        }
                        smartphoneProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, smartphoneProductList));
                    } else {
                        Toast.makeText(activity, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductListResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled()) {
                        return;
                    }
                    Toast.makeText(activity, "Có lỗi xảy ra khi tải danh sách sản phẩm điện thoại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (!smartphoneProductList.isEmpty())
                view.findViewById(R.id.smartphoneListNoProduct_textView).setVisibility(View.INVISIBLE);
            smartphoneProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, smartphoneProductList));
        }
    }

    private void handleTabletProduct() {
        tabletBtnMore.setOnClickListener((v) -> {
            Intent it = new Intent(v.getContext(), ProductListActivity.class);
            Map<String, String> mp = new HashMap<String, String>();
            mp.put("category_id", tabletCategoryId.toString());

            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("queryParams", (Serializable) mp);
            v.getContext().startActivity(it);
        });
        if (tabletProductList == null) {
            Map<String, String> mp2 = new HashMap<String, String>();
            mp2.put("category_id", "2");
            mp2.put("page", "1");

            callTabletProduct = apiServiceObject.apiServices.getProductList(mp2);
            callTabletProduct.enqueue(new Callback<ProductListResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                    if (response.isSuccessful()) {
                        if (call.isCanceled()) {
                            return;
                        }
                        tabletProductIsSet = true;
                        ProductListResponse pr = response.body();
                        if (pr != null && pr.totalPage > 0)
                            view.findViewById(R.id.Tablet_no_product_textView).setVisibility(View.INVISIBLE);
                        assert pr != null;
                        tabletProductList = new ArrayList<>();
                        for (ProductPreviewItem item :
                                pr.data) {
                            tabletProductList.add(new ProductPreview(item));
                        }
                        tabletProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, tabletProductList));
                    } else {
                        Toast.makeText(activity, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductListResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled()) {
                        return;
                    }
                    Toast.makeText(activity, "Có lỗi xảy ra khi tải danh sách sản phẩm tablet", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (!tabletProductList.isEmpty())
                view.findViewById(R.id.Tablet_no_product_textView).setVisibility(View.INVISIBLE);
            tabletProductRclView.setAdapter(new Adapter_ProductPreviewRclView(context, tabletProductList));
        }
    }

    private void checkValidUserSession() {
        String accessToken = appSharedPreferences.getAccessToken();
        if (!accessToken.isEmpty() || appSharedPreferences.getIsAuth()) {
            callCheckingValidUserSession = apiServiceObject.apiServices.loginWithAccessToken("Bearer " + accessToken);
            callCheckingValidUserSession.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (call.isCanceled()) {
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        appSharedPreferences.setAllAttribute(response.body().value.userId,
                                response.body().accessToken, response.body().value.firstName,
                                response.body().value.avatar);
                        new ImageLoadFromURL(response.body().value.avatar, userImgView, R.drawable.icon_user).execute();
                    } else {
                        Toast.makeText(activity, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
//                        appSharedPreferences.clear();
                        appSharedPreferences.setIsAuth(false);
                        userImgView.setImageResource(R.drawable.icon_user);
                        userImgView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    isCheckingAccount = false;

                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled()) {
                        return;
                    }
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
                    Intent it = new Intent(v.getContext(), UserActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(it);
                }
            }
        });

        ImageButton cartBtn = view.findViewById(R.id.HomePage_CartBtn);
        cartBtn.setOnClickListener(v -> {
            Intent it = new Intent(activity, CartActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(it);
        });
    }
}