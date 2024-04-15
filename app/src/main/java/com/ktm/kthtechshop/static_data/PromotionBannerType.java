package com.ktm.kthtechshop.static_data;

public enum PromotionBannerType {
    CAROUSEL(1),
    STATIC(2);

    private final int value;

    PromotionBannerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
