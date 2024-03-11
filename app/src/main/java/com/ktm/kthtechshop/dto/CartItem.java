package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    public CartItem_ProductOption option;
    @SerializedName("id")
    public int cartId;
    public int amount = 1;

    public CartItem(CartItem_ProductOption option, int cartId, int amount) {
        this.option = option;
        this.cartId = cartId;
        this.amount = amount;
    }

    public CartItem(CartItem_ProductOption option, int cartId) {
        this.option = option;
        this.cartId = cartId;
    }
}

