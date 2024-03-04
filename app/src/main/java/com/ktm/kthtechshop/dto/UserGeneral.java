package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class UserGeneral {
    @SerializedName("user_id")
    public Integer userId;
    @SerializedName("first_name")
    public String firstName;
    public String avatar;
}
