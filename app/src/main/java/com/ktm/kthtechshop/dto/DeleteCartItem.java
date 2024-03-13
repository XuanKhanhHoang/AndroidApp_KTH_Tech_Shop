package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class DeleteCartItem {
    @SerializedName("cart_id")
    public int cartId;

    public DeleteCartItem(int cartId) {
        this.cartId = cartId;
    }
}
