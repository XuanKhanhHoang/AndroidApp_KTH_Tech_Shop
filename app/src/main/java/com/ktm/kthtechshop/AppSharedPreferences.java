package com.ktm.kthtechshop;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AppSharedPreferences {
    private SharedPreferences sharedPreferences;
    public static String PREF_FILE_NAME = "user_info";

    public AppSharedPreferences(AppCompatActivity activity) {
        this.sharedPreferences = activity.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
    }

    public String getAccessToken() {
        return sharedPreferences.getString("accessToken", "");
    }

    public Integer getUserId() {
        return sharedPreferences.getInt("userId", -1);
    }

    public String getAvatar() {
        return sharedPreferences.getString("avatar", "");
    }

    public String getFirstName() {
        return sharedPreferences.getString("firstName", "");
    }

    public boolean getIsAuth() {
        return sharedPreferences.getBoolean("isAuth", false);
    }

    public void setAccessToken(String accessToken) {
        sharedPreferences.edit().putString("accessToken", accessToken).apply();
    }

    public void setAvatar(String avatar) {
        sharedPreferences.edit().putString("avatar", avatar).apply();

    }

    public void setUserId(Integer userId) {
        sharedPreferences.edit().putInt("userId", userId).apply();
    }

    public void setFirstName(String firstName) {
        sharedPreferences.edit().putString("firstName", firstName).apply();

    }

    public void setIsAuth(boolean isAuth) {
        sharedPreferences.edit().putBoolean("isAuth", isAuth).apply();


    }

    public void setAllAttribute(Integer userId, String accessToken, @Nullable String firstName, @Nullable String avatar) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.putInt("userId", userId);
        editor.putString("avatar", avatar != null ? avatar : "");
        editor.putString("firstName", firstName != null ? firstName : "");
        editor.putBoolean("isAuth", true);
        editor.apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().putBoolean("isAuth", false).apply();
    }

}
