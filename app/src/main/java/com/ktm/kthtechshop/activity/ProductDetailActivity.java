package com.ktm.kthtechshop.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
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
import com.ktm.kthtechshop.ProductDetail_ProductOption_ClickListener;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.Utils;
import com.ktm.kthtechshop.adapter.Adapter_ProductDetail_ProductOptionRclView;
import com.ktm.kthtechshop.dto.ProductDetail;
import com.ktm.kthtechshop.dto.ProductDetail_ProductOption;
import com.ktm.kthtechshop.localhostIp;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends NeededCallApiActivity implements ProductDetail_ProductOption_ClickListener {
    private TextView productName_txtView, ratingNumber_txtView, sellingPrice_txtView, originalPrice_txtView, totalCost_txtView;
    private RatingBar ratingBar;
    private ImageSlider slider;
    private Button increaseAmount_btn, decreaseAmount_btn, addToCart_btn, buyNow_btn;
    private RecyclerView option_rclView;
    private TextInputEditText amount_editTxtView;
    private Integer totalCost, curAmount;
    private static int curPositionOption;
    private boolean isFilterAmountInput = true;
    private ProductDetail product;
    private Integer productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_activity);
        initApiService();
        refChildComponents();
        curAmount = 1;
        curPositionOption = 0;
        productId = 1;
        apiServices.getProductDetail(productId).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetail> call, @NonNull Response<ProductDetail> response) {
                if (response.isSuccessful()) {
                    product = response.body();
                    ratingBar.setRating(product.rating);
                    ratingBar.setStepSize(0.1f);
                    ratingBar.setIsIndicator(true);
                    totalCost = product.productOptions.get(0).sellingPrice;
                    option_rclView.setAdapter(new Adapter_ProductDetail_ProductOptionRclView(ProductDetailActivity.this, product.productOptions, ProductDetailActivity.this));
                    ArrayList<SlideModel> sr = new ArrayList<>();
                    sr.add(new SlideModel(product.logo, ScaleTypes.CENTER_INSIDE));
                    for (ProductDetail_ProductOption option : product.productOptions
                    ) {
                        if (option.image != null && option.image.length() != 0) {
                            sr.add(new SlideModel(localhostIp.LOCALHOST_IP.getValue() + ":3000" + option.image, ScaleTypes.CENTER_INSIDE));
                        }
                    }
                    slider.setImageList(sr);
                    notifyCurrOptionChange();

                } else {
                    Toast.makeText(ProductDetailActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.ProductDetail_MainContent).setVisibility(View.GONE);
                    findViewById(R.id.ProductDetail_ErrorText).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ProductDetail> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
                findViewById(R.id.ProductDetail_MainContent).setVisibility(View.GONE);
                findViewById(R.id.ProductDetail_ErrorText).setVisibility(View.VISIBLE);
            }
        });


        limitTextInputEditText(amount_editTxtView);
        option_rclView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        option_rclView.setHasFixedSize(true);


        increaseAmount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curAmount < product.productOptions.get(curPositionOption).amount) {
                    curAmount++;
                    isFilterAmountInput = false;
                    amount_editTxtView.setText(String.valueOf(curAmount));
                    isFilterAmountInput = true;
                }
            }
        });
        decreaseAmount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curAmount > 1) {
                    curAmount--;
                    isFilterAmountInput = false;
                    amount_editTxtView.setText(String.valueOf(curAmount));
                    isFilterAmountInput = true;
                }
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
                    Integer input = Integer.parseInt(source.toString());
                    if (source.length() == 0) {
                        curAmount = input;
                        notifyTotalCostChange();
                        return null;
                    }
                    if (input >= 0) {
                        if (editText.getText().length() == 0 && input < upperBound && input > 0) {
                            curAmount = input;
                            notifyTotalCostChange();
                            return null;
                        }
                        try {
                            Integer nextVal = Integer.parseInt(editText.getText().toString() + input.toString());
                            if (nextVal > 0 && nextVal <= upperBound) {
                                curAmount = nextVal;
                                notifyTotalCostChange();
                                return null;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                } catch (NumberFormatException e) {
                }
                return "";
            }
        };
        editText.setFilters(new InputFilter[]{inputFilter});
    }

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