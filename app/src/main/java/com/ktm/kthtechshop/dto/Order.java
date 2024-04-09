package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Order {
    public String createAt;
    public OrderStatus status;
    public int id;
    @SerializedName("order_list")
    public ArrayList<OrderListItem> orderList;
    public int price;
    public String name;

    // Getters and Setters
}
