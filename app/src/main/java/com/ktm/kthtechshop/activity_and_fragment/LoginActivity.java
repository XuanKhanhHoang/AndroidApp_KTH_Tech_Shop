package com.ktm.kthtechshop.activity_and_fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.dto.Login_UserInfo;
import com.ktm.kthtechshop.utils.AppSharedPreferences;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends NeededCallApiActivity {

    private Button loginBtn, toRegisterBtn;
    private TextInputLayout userNameTxtInpLayout, passwordTxtInpLayout;
    private ImageButton backBtn;
    private boolean isLoading = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initApiService();
        refChildComponents();
        AppSharedPreferences appSharedPreferences = new AppSharedPreferences(this);
        appSharedPreferences.setIsAuth(false);
        loginBtn.setOnClickListener(v -> {
            if (isLoading) return;
            boolean isValid = true;
            if (Objects.requireNonNull(userNameTxtInpLayout.getEditText()).getText().length() == 0) {
                userNameTxtInpLayout.setError("Tên đăng nhập không được bỏ trống");
                isValid = false;
            } else userNameTxtInpLayout.setErrorEnabled(false);
            if (Objects.requireNonNull(passwordTxtInpLayout.getEditText()).getText().length() < 6) {
                passwordTxtInpLayout.setError("Mật khẩu không được nhỏ hơn 6 chữ số");
                isValid = false;
            } else passwordTxtInpLayout.setErrorEnabled(false);
            if (!isValid) return;
            isLoading = true;
            loginBtn.setText("Loading... ");
            apiServices.loginWithPassword(new Login_UserInfo(userNameTxtInpLayout.getEditText().getText().toString(), passwordTxtInpLayout.getEditText().getText().toString())).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        assert response.body() != null;
                        appSharedPreferences.setAllAttribute(response.body().value.userId, response.body().accessToken, response.body().value.firstName, response.body().value.avatar);
//                        Intent it = new Intent(v.getContext(), MainActivity.class);
//                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        v.getContext().startActivity(it);
                        finish();
                    } else if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoạc mật khẩu ", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        loginBtn.setText("Đăng nhập");

                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại ", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        loginBtn.setText("Đăng nhập");

                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Toast.makeText(LoginActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    loginBtn.setText("Đăng nhập");
                }
            });
        });
        toRegisterBtn.setOnClickListener(v -> {
            if (isLoading) {
                Toast.makeText(LoginActivity.this, "Vui lòng chờ trạng thái đăng nhập hoàn tất", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent it = new Intent(v.getContext(), RegisterActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(it);
        });
        userNameTxtInpLayout.setOnClickListener(v -> {
        });
        passwordTxtInpLayout.setOnClickListener(v -> passwordTxtInpLayout.setErrorEnabled(false));
        backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void refChildComponents() {
        loginBtn = findViewById(R.id.Login_Btn_Login);
        toRegisterBtn = findViewById(R.id.Login_Btn_ToRegister);
//        forgotPasswordBtn = findViewById(R.id.)
        backBtn = findViewById(R.id.backBtn);
        userNameTxtInpLayout = findViewById(R.id.Login_UserName);
        passwordTxtInpLayout = findViewById(R.id.Login_Password);
    }
}