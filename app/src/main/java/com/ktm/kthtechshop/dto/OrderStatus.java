package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class OrderStatus {
    public int id;
    @SerializedName("status_name")
    public String statusName;
}
