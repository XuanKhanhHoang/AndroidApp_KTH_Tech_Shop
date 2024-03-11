package com.ktm.kthtechshop.utils;

public class Utils {
    public static String formatPrice(Integer str) {
        return String.format("%,d", str).replace(',', '.') + "đ";
    }
}
