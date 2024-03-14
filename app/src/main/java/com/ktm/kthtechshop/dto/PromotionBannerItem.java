package com.ktm.kthtechshop.dto;

import android.graphics.Bitmap;

public class PromotionBannerItem {
    private Integer id;
    private String image;
    private Integer type;
    private String name;
    private Bitmap bitmap;

    public Integer getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public PromotionBannerItem(Integer id, String image, Integer type, String name) {
        this.id = id;
        this.image = image;
        this.type = type;
        this.name = name;
    }

    public PromotionBannerItem(Integer id, String image, Integer type) {
        this.id = id;
        this.image = image;
        this.type = type;
        this.name = "";
    }
}
