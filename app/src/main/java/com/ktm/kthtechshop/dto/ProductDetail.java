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
    public ArrayList<ProductDetail_ProductOption> productOptions;
    public ProductDetail_Brand brand;
    public ArrayList<ProductDetail_Comment> comment;

    public ProductDetail(int productId, String name, String logo, String description, String information, float rating, ArrayList<ProductDetail_ProductOption> productOptions) {
        this.logo = logo;
        this.name = name;
        this.description = description;
        this.productId = productId;
        this.information = information;
        this.rating = rating;
        this.productOptions = productOptions;
    }

    public ProductDetail(int productId, String name, String logo, String description, String information, float rating, ArrayList<ProductDetail_ProductOption> productOptions, ProductDetail_Brand brand, ArrayList<ProductDetail_Comment> comment) {
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

class ProductDetail_Comment {
    public int id;
    public String content;
    public double rating;
    public ProductDetail_User user;
    public String sellerReply;
    public String createAt;
    public ProductDetail_Image[] image;

    public ProductDetail_Comment(int id, String content, double rating, ProductDetail_User user, String sellerReply, String createAt, ProductDetail_Image[] image) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.sellerReply = sellerReply;
        this.createAt = createAt;
        this.image = image;
    }
}

class ProductDetail_Brand {
    public int id;
    public String name;

    public ProductDetail_Brand(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

class ProductDetail_User {
    public int userId;
    public String avatar;
    public String firstName;

    public ProductDetail_User(int userId, String avatar, String firstName) {
        this.userId = userId;
        this.avatar = avatar;
        this.firstName = firstName;
    }
}

class ProductDetail_Image {
    public String image;

    public ProductDetail_Image(String image) {
        this.image = image;
    }

}

