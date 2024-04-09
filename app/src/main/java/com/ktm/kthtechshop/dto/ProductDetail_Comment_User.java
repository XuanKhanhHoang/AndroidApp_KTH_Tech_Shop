package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class ProductDetail_Comment_User {
    @SerializedName("user_id")
    public int userId;
    public String avatar;
    @SerializedName("first_name")
    public String firstName;

    public ProductDetail_Comment_User(int userId, String avatar, String firstName) {
        this.userId = userId;
        this.avatar = avatar;
        this.firstName = firstName;
    }
}
