package com.ktm.kthtechshop.model;

import android.graphics.Bitmap;

import com.ktm.kthtechshop.dto.ProductPreviewItem;

public class ProductPreview extends ProductPreviewItem {
    private Bitmap bitmapLogo;

    public ProductPreview(String logo, String name, Integer sellingPrice, Integer originalPrice, Integer discount, Integer productId, float rating) {
        super(logo, name, sellingPrice, originalPrice, discount, productId, rating);
    }

    public ProductPreview(String logo, String name, Integer sellingPrice, Integer originalPrice, Integer productId, float rating) {
        super(logo, name, sellingPrice, originalPrice, productId, rating);
    }

    public ProductPreview(ProductPreviewItem pr) {
        super(pr.getLogo(), pr.getName(), pr.getSellingPrice(), pr.getOriginalPrice(), pr.getDiscount(), pr.getProduct_id(), pr.getRating());
    }

    public void setBitmapLogo(Bitmap bitmapLogo) {
        this.bitmapLogo = bitmapLogo;
    }

    public Bitmap getBitmapLogo() {
        return this.bitmapLogo;
    }
}
