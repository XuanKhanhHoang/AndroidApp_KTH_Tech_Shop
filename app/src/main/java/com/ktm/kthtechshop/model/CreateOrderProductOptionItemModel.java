package com.ktm.kthtechshop.model;

import com.ktm.kthtechshop.dto.ProductOptionWithProductName;

public class CreateOrderProductOptionItemModel {

    public int amount = 1;
    public ProductOptionWithProductName option;

    public CreateOrderProductOptionItemModel(ProductOptionWithProductName productOption, int amount) {
        this.amount = amount;
        this.option = productOption;
    }
}

