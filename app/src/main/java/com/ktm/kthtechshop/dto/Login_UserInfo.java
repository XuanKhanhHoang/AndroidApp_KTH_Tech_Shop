package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class Login_UserInfo {
    @SerializedName("login_name")
    public String userName;
    public String password;

    public Login_UserInfo(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
