package com.ktm.kthtechshop.api;

import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.dto.GetCartResponse;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.dto.Login_UserInfo;
import com.ktm.kthtechshop.dto.ProductDetail;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.PromotionBannerItem;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    @GET("webinfo/promotionbanner")
    Call<ArrayList<PromotionBannerItem>> getListPromotionBanner();

    @GET("product/categorylist")
    Call<ArrayList<CategoryItem>> getCategoryList();

    @GET("product/productlist")
    Call<ProductListResponse> getProductList(@QueryMap Map<String, String> options);

    @GET("product/productdetail")
    Call<ProductDetail> getProductDetail(@Query("product_id") Integer productId);

    @POST("auth/login")
    Call<LoginResponse> loginWithPassword(@Body Login_UserInfo userInfo);

    @POST("auth/refreshinfobyaccesstoken")
    Call<LoginResponse> loginWithAccessToken(@Header("Authorization") String accessToken);

    @GET("cart/getcart")
    Call<GetCartResponse> getCart(@Header("Authorization") String accessToken);
}

