package com.ktm.kthtechshop.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AppGson {
    private static Gson gson;

    public static Gson getGson() {
        if (gson == null)
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson;
    }

}
