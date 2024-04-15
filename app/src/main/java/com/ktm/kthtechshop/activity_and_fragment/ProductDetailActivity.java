package com.ktm.kthtechshop.activity_and_fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.adapters.Adapter_ProductDetail_ProductComment;
import com.ktm.kthtechshop.adapters.Adapter_ProductDetail_ProductOptionRclView;
import com.ktm.kthtechshop.dto.AddCartSendToServer;
import com.ktm.kthtechshop.dto.AddOptionToCartResponse;
import com.ktm.kthtechshop.dto.CartItemSaved;
import com.ktm.kthtechshop.dto.ProductDetail;
import com.ktm.kthtechshop.dto.ProductOption;
import com.ktm.kthtechshop.listeners.ProductDetail_ProductOption_ClickListener;
import com.ktm.kthtechshop.model.CreateOrderIntentModel;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends NeededCallApiActivity implements ProductDetail_ProductOption_ClickListener {
    private TextView productName_txtView, ratingNumber_txtView, sellingPrice_txtView, originalPrice_txtView, totalCost_txtView, noComment_txtView;
    private RatingBar ratingBar;
    private ImageSlider slider;
    private Button increaseAmount_btn, decreaseAmount_btn, addToCart_btn, buyNow_btn;
    private RecyclerView option_rclView;
    private TextInputEditText amount_editTxtView;
    private Integer totalCost, curAmount;
    private int curPositionOption;
    private boolean isFilterAmountInput = true;
    private ProductDetail product;
    private Integer productId;
    private ImageButton backBtn, toCartBtn;
    private AppSharedPreferences appSharedPreferences;
    private WebView specWebView, descriptionWebView;
    private RecyclerView commentRclView;
    private ImageView noComment_imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_activity);
        initApiService();
        refChildComponents();
        curAmount = 1;
        curPositionOption = 0;
        appSharedPreferences = new AppSharedPreferences(this);
        Intent it = getIntent();
        productId = -1;
        if (it != null) {
            productId = it.getIntExtra("productId", -1);
        }
        backBtn.setOnClickListener(v -> {
            finish();
        });
        buyNow_btn.setOnClickListener(v -> {
            if (!appSharedPreferences.getIsAuth()) {
                Toast.makeText(this, "Vui lòng đăng nhập ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                ProductDetailActivity.this.startActivity(intent);
                return;
            }
            Intent intent = new Intent(ProductDetailActivity.this, CreateOrderActivity.class);
            ArrayList<CreateOrderIntentModel> products = new ArrayList<>();
            products.add(new CreateOrderIntentModel(product.productOptions.get(curPositionOption).id, curAmount));
            Bundle bundle = new Bundle();
            bundle.putSerializable(CreateOrderActivity.productsIdAndAmountIntentKeyName, products);
            intent.putExtras(bundle);
            ProductDetailActivity.this.startActivity(intent);
        });
        toCartBtn.setOnClickListener(v -> {
            //to cart act
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            ProductDetailActivity.this.startActivity(intent);
        });
        addToCart_btn.setOnClickListener(v -> {
            if (!appSharedPreferences.getIsAuth()) {
                Toast.makeText(this, "Vui lòng đăng nhập ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                ProductDetailActivity.this.startActivity(intent);
                return;
            }
            String cartJson = appSharedPreferences.getCart();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            int optionId = product.productOptions.get(curPositionOption).id;
            ArrayList<CartItemSaved> cart;
            if (cartJson != null && !cartJson.isEmpty()) {
                Type type = new TypeToken<ArrayList<CartItemSaved>>() {
                }.getType();
                cart = gson.fromJson(cartJson, type);
                for (CartItemSaved item : cart) {
                    if (item.oId == optionId) {
                        Toast.makeText(this, "Sản phẩm đã có trong giỏ hàng rồi !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            } else cart = new ArrayList<>();
            apiServices.addOptionToCart(appSharedPreferences.getBearerAccessToken(), new AddCartSendToServer(optionId)).enqueue(new Callback<AddOptionToCartResponse>() {
                @Override
                public void onResponse(@NonNull Call<AddOptionToCartResponse> call, @NonNull Response<AddOptionToCartResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        cart.add(new CartItemSaved(response.body().cartId, optionId, curAmount));
                        appSharedPreferences.setCart(gson.toJson(cart));
                        Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng thành công !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng Thất bại !", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<AddOptionToCartResponse> call, @NonNull Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Thêm vào giỏ hàng Thất bại !", Toast.LENGTH_SHORT).show();
                }
            });
        });
        if (productId != -1)
            apiServices.getProductDetail(productId).enqueue(new Callback<ProductDetail>() {
                @Override
                public void onResponse(@NonNull Call<ProductDetail> call, @NonNull Response<ProductDetail> response) {
                    if (response.isSuccessful()) {
                        product = response.body();
                        assert product != null;
                        ratingBar.setRating(product.rating);
                        ratingBar.setStepSize(0.1f);
                        ratingBar.setIsIndicator(true);
                        totalCost = product.productOptions.get(0).sellingPrice;
                        option_rclView.setAdapter(new Adapter_ProductDetail_ProductOptionRclView(ProductDetailActivity.this, product.productOptions, ProductDetailActivity.this));
                        ArrayList<SlideModel> sr = new ArrayList<>();
                        sr.add(new SlideModel(product.logo, ScaleTypes.CENTER_INSIDE));
                        for (ProductOption option : product.productOptions
                        ) {
                            if (option.image != null && option.image.length() != 0) {
                                sr.add(new SlideModel(option.image, ScaleTypes.CENTER_INSIDE));
                            }
                        }
                        slider.setImageList(sr);
                        if (product.comment.size() > 0) {
                            noComment_imgView.setVisibility(View.GONE);
                            noComment_txtView.setVisibility(View.GONE);
                            commentRclView.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                            commentRclView.setAdapter(new Adapter_ProductDetail_ProductComment(ProductDetailActivity.this, product.comment));

                        }
                        String desHtml = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"htmlCss.css\"></head><body>" + product.description + "</body></html>";
                        String specHtml = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"htmlCss.css\"></head><body>" + product.information + "</body></html>";
                        specWebView.loadDataWithBaseURL("file:///android_asset/", specHtml, "text/html", "UTF-8", null);
                        descriptionWebView.loadDataWithBaseURL(null, desHtml, "text/html", "UTF-8", null);
                        notifyCurrOptionChange();

                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.ProductDetail_MainContent).setVisibility(View.GONE);
                        findViewById(R.id.ProductDetail_ErrorText).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductDetail> call, @NonNull Throwable t) {
                    Toast.makeText(ProductDetailActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.ProductDetail_MainContent).setVisibility(View.GONE);
                    findViewById(R.id.ProductDetail_ErrorText).setVisibility(View.VISIBLE);
                }
            });
        else {
            Toast.makeText(ProductDetailActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
            findViewById(R.id.ProductDetail_MainContent).setVisibility(View.GONE);
            findViewById(R.id.ProductDetail_ErrorText).setVisibility(View.VISIBLE);
            return;
        }


        limitTextInputEditText(amount_editTxtView);
        option_rclView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        option_rclView.setHasFixedSize(true);


        increaseAmount_btn.setOnClickListener(v -> {
            if (curAmount < product.productOptions.get(curPositionOption).amount) {
                curAmount++;
                isFilterAmountInput = false;
                amount_editTxtView.setText(String.valueOf(curAmount));
                isFilterAmountInput = true;
            }
        });
        decreaseAmount_btn.setOnClickListener(v -> {
            if (curAmount > 1) {
                curAmount--;
                isFilterAmountInput = false;
                amount_editTxtView.setText(String.valueOf(curAmount));
                isFilterAmountInput = true;
            }
        });

    }

    private void refChildComponents() {
        productName_txtView = findViewById(R.id.ProductDetailName);
        ratingNumber_txtView = findViewById(R.id.ProductDetailRatingNumber);
        sellingPrice_txtView = findViewById(R.id.ProductDetail_selling_price);
        originalPrice_txtView = findViewById(R.id.ProductDetail_original_price);
        totalCost_txtView = findViewById(R.id.ProductDetail_total_cost);
        ratingBar = findViewById(R.id.ProductDetailRatingBar);
        increaseAmount_btn = findViewById(R.id.increaseAmount);
        decreaseAmount_btn = findViewById(R.id.decreaseAmount);
        addToCart_btn = findViewById(R.id.ProductDetail_AddToCart);
        buyNow_btn = findViewById(R.id.ProductDetail_BuyNow);
        slider = findViewById(R.id.ProductDetail_Slider);
        option_rclView = findViewById(R.id.ProductDetail_OptionList_rclView);
        amount_editTxtView = findViewById(R.id.ProductDetail_amountInp);
        backBtn = findViewById(R.id.backBtn);
        toCartBtn = findViewById(R.id.ToCart);
        specWebView = findViewById(R.id.ProductDetail_Spec_WebView);
        descriptionWebView = findViewById(R.id.ProductDetail_Description_WebView);
        commentRclView = findViewById(R.id.ProductDetail_Comment_RclView);
        noComment_txtView = findViewById(R.id.ProductDetail_NoComment_TxtView);
        noComment_imgView = findViewById(R.id.ProductDetail_NoCommentImg);
    }

    private void limitTextInputEditText(TextInputEditText editText) {
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (!isFilterAmountInput) {
                    //curAmount has just update before this
                    notifyTotalCostChange();
                    return null;
                }
                try {
                    int upperBound = product.productOptions.get(curPositionOption).amount;
                    int input = Integer.parseInt(source.toString());
                    if (source.length() == 0) {
                        curAmount = input;
                        notifyTotalCostChange();
                        return null;
                    }
                    if (input >= 0) {
                        if (Objects.requireNonNull(editText.getText()).length() == 0 && input < upperBound && input > 0) {
                            curAmount = input;
                            notifyTotalCostChange();
                            return null;
                        }
                        try {
                            int nextVal = Integer.parseInt(editText.getText().toString() + Integer.toString(input));
                            if (nextVal > 0 && nextVal <= upperBound) {
                                curAmount = nextVal;
                                notifyTotalCostChange();
                                return null;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
                return "";
            }
        };
        editText.setFilters(new InputFilter[]{inputFilter});
    }

    @SuppressLint("SetTextI18n")
    private void notifyCurrOptionChange() {
        productName_txtView.setText(product.name);
        ratingNumber_txtView.setText(Float.toString(product.rating));
        sellingPrice_txtView.setText(Utils.formatPrice(product.productOptions.get(curPositionOption).sellingPrice));
        if (product.productOptions.get(curPositionOption).discount > 0) {
            originalPrice_txtView.setVisibility(View.VISIBLE);
            originalPrice_txtView.setText(Utils.formatPrice(product.productOptions.get(curPositionOption).originalPrice));
            originalPrice_txtView.setPaintFlags(originalPrice_txtView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            originalPrice_txtView.setVisibility(View.GONE);
        }
        isFilterAmountInput = false;
        amount_editTxtView.setText(String.valueOf(curAmount));
        isFilterAmountInput = true;
        notifyTotalCostChange();
    }

    private void notifyTotalCostChange() {
        totalCost = product.productOptions.get(curPositionOption).sellingPrice * curAmount;
        totalCost_txtView.setText(Utils.formatPrice(totalCost));
    }

    @Override
    public void onClick(int position) {
        this.curPositionOption = position;
        notifyCurrOptionChange();
    }
}