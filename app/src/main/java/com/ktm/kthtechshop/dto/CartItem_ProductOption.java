package com.ktm.kthtechshop.dto;

public class CartItem_ProductOption extends ProductOption {
    public class CartItem_ProductNameClass {
        public String name;

        public CartItem_ProductNameClass(String name) {
            this.name = name;
        }
    }

    public CartItem_ProductNameClass products;

    public CartItem_ProductOption(int id,
                                  String name,
                                  Integer originalPrice,
                                  Integer sellingPrice,
                                  int amount,
                                  double discount,
                                  String image, String productName) {
        super(id, name, originalPrice, sellingPrice, amount, discount, image);
        this.products = new CartItem_ProductNameClass(productName);
    }
}
