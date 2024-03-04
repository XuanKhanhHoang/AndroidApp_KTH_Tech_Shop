package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class ProductPreviewItem {
    private final String logo, name;


    private final Integer original_price, discount, product_id;
    private final float rating;

    public Integer getOriginal_price() {
        return original_price;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public float getRating() {
        return rating;
    }

    public Integer getSelling_price() {
        return selling_price;
    }

    @SerializedName("price_sell")
    private final Integer selling_price;

    public ProductPreviewItem(String logo, String name, Integer sellingPrice, Integer originalPrice, Integer discount, Integer productId, float rating) {
        this.logo = logo;
        this.name = name;
        this.selling_price = sellingPrice;
        this.original_price = originalPrice;
        this.discount = discount;
        product_id = productId;
        this.rating = rating;
    }

    public ProductPreviewItem(String logo, String name, Integer sellingPrice, Integer originalPrice, Integer productId, float rating) {
        this.logo = logo;
        this.name = name;
        this.selling_price = sellingPrice;
        this.original_price = originalPrice;
        product_id = productId;
        this.rating = rating;
        this.discount = 0;
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public Integer getSellingPrice() {
        return selling_price;
    }

    public Integer getOriginalPrice() {
        return original_price;
    }

    public Integer getDiscount() {
        return discount;
    }
}
