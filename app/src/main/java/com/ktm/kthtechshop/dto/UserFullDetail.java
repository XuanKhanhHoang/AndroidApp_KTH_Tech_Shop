package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class UserFullDetail {
    @SerializedName("user_id")
    public int userId;
    @SerializedName("first_name")
    public String firstName;
    @SerializedName("last_name")
    public String lastName;
    public String address;
    public String email;
    public boolean gender;
    @SerializedName("avartar")
    public String avatar;
    @SerializedName("login_name")
    public String loginName;
    @SerializedName("phone_number")
    public String phoneNumber;
}
