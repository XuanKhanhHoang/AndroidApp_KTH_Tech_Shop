package com.ktm.kthtechshop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.dto.Login_UserInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends NeededCallApiActivity {

    private Button loginBtn, toRegisterBtn, forgotPasswordBtn;
    private TextInputLayout userNameTxtInpLayout, passwordTxtInpLayout;
    private SharedPreferences sharedPreferences;
    private ImageButton backBtn;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initApiService();
        refChildComponents();
        sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isAuth", false);
        sharedPreferences.edit().commit();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) return;
                boolean isValid = true;
                if (userNameTxtInpLayout.getEditText().getText().length() == 0) {
                    userNameTxtInpLayout.setError("Tên đăng nhập không được bỏ trống");
                    isValid = false;
                } else userNameTxtInpLayout.setErrorEnabled(false);
                if (passwordTxtInpLayout.getEditText().getText().length() < 6) {
                    passwordTxtInpLayout.setError("Mật khẩu không được nhỏ hơn 6 chữ số");
                    isValid = false;
                } else passwordTxtInpLayout.setErrorEnabled(false);
                if (!isValid) return;
                isLoading = true;
                loginBtn.setText("Loading... ");
                apiServices.loginWithPassword(new Login_UserInfo(userNameTxtInpLayout.getEditText().getText().toString(), passwordTxtInpLayout.getEditText().getText().toString())).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accessToken", response.body().accessToken);
                            editor.putInt("userId", response.body().value.userId);
                            editor.putString("avatar", response.body().value.avatar);
                            editor.putString("firstName", response.body().value.firstName);
                            editor.putBoolean("isAuth", false);
                            editor.apply();
                            Intent it = new Intent(v.getContext(), MainActivity.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            v.getContext().startActivity(it);
                        } else if (response.code() == 401) {
                            Toast.makeText(LoginActivity.this, "Sai tài khoản hoạc mật khẩu ", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                        } else {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại ", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Call api failed", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        loginBtn.setText("Đăng nhập");
                    }
                });
            }
        });
        toRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) {
                    Toast.makeText(LoginActivity.this, "Vui lòng chờ trạng thái đăng nhập hoàn tất", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent it = new Intent(v.getContext(), RegisterActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(it);
            }
        });
        userNameTxtInpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        passwordTxtInpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTxtInpLayout.setErrorEnabled(false);
            }
        });
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