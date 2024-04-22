package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class SendAccessToken {
    @SerializedName("access_token")
    public String access_token;

    public SendAccessToken(String access_token) {
        this.access_token = access_token;
    }
}
