package com.ktm.kthtechshop.dto;

import android.graphics.Bitmap;

public class CategoryItemViewModel extends CategoryItem {
    public Bitmap imageBitmap;

    public CategoryItemViewModel(String name, String icon, String id, Bitmap imageBitmap) {
        super(name, icon, id);
        this.imageBitmap = imageBitmap;
    }

}
