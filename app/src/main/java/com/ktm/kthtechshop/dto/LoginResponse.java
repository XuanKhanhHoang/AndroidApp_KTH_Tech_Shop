package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    public String accessToken;
    public UserGeneral value;
}

