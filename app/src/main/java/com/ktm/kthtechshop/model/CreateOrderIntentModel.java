package com.ktm.kthtechshop.model;

import com.google.gson.annotations.SerializedName;
import com.ktm.kthtechshop.dto.CartItemSaved;

import java.io.Serializable;

public class CreateOrderIntentModel implements Serializable {
    @SerializedName("option_id")
    public int productOptionId;
    public int amount;

    public CreateOrderIntentModel(CartItemSaved cartItemSaved) {
        this.productOptionId = cartItemSaved.oId;
        this.amount = cartItemSaved.amount;
    }

    public CreateOrderIntentModel(int productOptionId, int amount) {
        this.productOptionId = productOptionId;
        this.amount = amount;
    }
}
