package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProductDetail {
    public String logo;
    public String name;
    public String description;
    public int productId;
    public String information;
    public float rating;
    @SerializedName("product_options")
    public ArrayList<ProductOption> productOptions;
    public Brand brand;
    public ArrayList<ProductDetail_ProductComment> comment;

    public ProductDetail(int productId, String name, String logo, String description, String information, float rating, ArrayList<ProductOption> productOptions) {
        this.logo = logo;
        this.name = name;
        this.description = description;
        this.productId = productId;
        this.information = information;
        this.rating = rating;
        this.productOptions = productOptions;
    }

    public ProductDetail(int productId, String name, String logo, String description, String information, float rating, ArrayList<ProductOption> productOptions, Brand brand, ArrayList<ProductDetail_ProductComment> comment) {
        this.logo = logo;
        this.name = name;
        this.description = description;
        this.productId = productId;
        this.information = information;
        this.rating = rating;
        this.productOptions = productOptions;
        this.brand = brand;
        this.comment = comment;
    }
}




