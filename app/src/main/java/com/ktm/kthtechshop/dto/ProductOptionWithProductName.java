package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class ProductOptionWithProductName extends ProductOption {
    @SerializedName("products")
    public ProductNameClass product;

    public class ProductNameClass {
        public String name;

        public ProductNameClass(String name) {
            this.name = name;
        }
    }

    public ProductOptionWithProductName(int id, String name, Integer originalPrice, Integer sellingPrice, int amount, double discount, String image) {
        super(id, name, originalPrice, sellingPrice, amount, discount, image);
    }
}
