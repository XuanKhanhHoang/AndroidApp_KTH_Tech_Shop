package com.ktm.kthtechshop.api;

import com.ktm.kthtechshop.dto.AddCartSendToServer;
import com.ktm.kthtechshop.dto.AddOptionToCartResponse;
import com.ktm.kthtechshop.dto.Brand;
import com.ktm.kthtechshop.dto.CartItem;
import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.dto.CreateOrderResponse;
import com.ktm.kthtechshop.dto.CreateOrderSendToServer;
import com.ktm.kthtechshop.dto.DeleteCartItem;
import com.ktm.kthtechshop.dto.GetHaveTotalPageResponse;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.dto.Login_UserInfo;
import com.ktm.kthtechshop.dto.Order;
import com.ktm.kthtechshop.dto.ProductDetail;
import com.ktm.kthtechshop.dto.ProductListResponse;
import com.ktm.kthtechshop.dto.ProductOptionWithProductName;
import com.ktm.kthtechshop.dto.PromotionBannerItem;
import com.ktm.kthtechshop.dto.SendAccessToken;
import com.ktm.kthtechshop.dto.UserFullDetail;
import com.ktm.kthtechshop.dto.updateUserDetailResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServices {
    @GET("webinfo/promotionbanner")
    Call<ArrayList<PromotionBannerItem>> getListPromotionBanner();

    @GET("product/categorylist")
    Call<ArrayList<CategoryItem>> getCategoryList();

    @GET("product/brandlist")
    Call<ArrayList<Brand>> getBrandList();

    @GET("product/productlist")
    Call<ProductListResponse> getProductList(@QueryMap Map<String, String> options);

    @GET("product/get_product_option_basic_info_list")
    Call<GetHaveTotalPageResponse<ArrayList<ProductOptionWithProductName>>> getProductOptionBasicList(@QueryMap Map<String, String> options, @Query("products_option_id") List<String> productsOptionIds);

    @GET("product/productdetail")
    Call<ProductDetail> getProductDetail(@Query("product_id") Integer productId);

    @POST("auth/login")
    Call<LoginResponse> loginWithPassword(@Body Login_UserInfo userInfo);

    @POST("auth/refreshinfobyaccesstoken")
    Call<LoginResponse> loginWithAccessToken(@Header("Authorization") String accessToken);

    @GET("cart/getcart")
    Call<GetHaveTotalPageResponse<ArrayList<CartItem>>> getCart(@Header("Authorization") String accessToken);

    @POST("cart/addproduct")
    Call<AddOptionToCartResponse> addOptionToCart(@Header("Authorization") String accessToken, @Body() AddCartSendToServer optionId);

    @HTTP(method = "DELETE", path = "cart/deleteproduct", hasBody = true)
    Call<DeleteCartItem> deleteOptionInCart(@Header("Authorization") String accessToken, @Body() DeleteCartItem deleteCartId);

    @GET("order/orderlist")
    Call<GetHaveTotalPageResponse<ArrayList<Order>>> getOrderList(@Header("Authorization") String accessToken, @Query("status_id") int statusId, @Query("page") int page);

    @GET("order/orderlist")
    Call<GetHaveTotalPageResponse<ArrayList<Order>>> getOrderList(@Header("Authorization") String accessToken, @Query("page") int page);

    @GET("customer/customer_detail")
    Call<UserFullDetail> getCustomerDetail(@Header("Authorization") String accessToken);

    @Multipart
    @PUT("customer/update_detail")
    Call<updateUserDetailResponse> editUser(@Header("Authorization") String authorization,
                                            @PartMap Map<String, RequestBody> params
    );

    @POST("order/create_order")
    Call<CreateOrderResponse> createOrder(@Header("Authorization") String accessToken, @Body() CreateOrderSendToServer bd);

    @Multipart
    @POST("customer/create_new_customer")
    Call<LoginResponse> createCustomer(@PartMap Map<String, RequestBody> params);

    @POST("auth/login_by_facebook")
    Call<LoginResponse> loginWithFacebook(@Body() SendAccessToken access_token);
}

