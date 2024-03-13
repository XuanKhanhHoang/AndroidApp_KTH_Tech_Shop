package com.ktm.kthtechshop.dto;

public class CartItemSaved {
    public int cartId;
    public int oId;
    public int amount;

    public CartItemSaved(int cartId, int oId, int amount) {
        this.cartId = cartId;
        this.oId = oId;
        this.amount = amount;
    }
}
