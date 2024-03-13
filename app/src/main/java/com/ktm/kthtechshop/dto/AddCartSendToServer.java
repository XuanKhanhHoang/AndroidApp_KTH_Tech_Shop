package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class AddCartSendToServer {
    @SerializedName("option_id")
    private int optionId;

    public AddCartSendToServer(int optionId) {
        this.optionId = optionId;
    }
}
