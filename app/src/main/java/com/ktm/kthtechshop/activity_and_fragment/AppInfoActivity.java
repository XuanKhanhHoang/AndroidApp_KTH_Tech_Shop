package com.ktm.kthtechshop.activity_and_fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ktm.kthtechshop.R;

public class AppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }
}