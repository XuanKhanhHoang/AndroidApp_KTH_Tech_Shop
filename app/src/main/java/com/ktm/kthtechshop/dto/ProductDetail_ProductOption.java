package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class ProductDetail_ProductOption {
    public Integer originalPrice;
    @SerializedName("price")
    public Integer sellingPrice;
    public String name;
    public int id;
    public int amount;
    public double discount;
    public String image;

    public ProductDetail_ProductOption(int id, String name, Integer originalPrice, Integer sellingPrice, int amount, double discount, String image) {
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        this.name = name;
        this.id = id;
        this.amount = amount;
        this.discount = discount;
        this.image = image;
    }
}
