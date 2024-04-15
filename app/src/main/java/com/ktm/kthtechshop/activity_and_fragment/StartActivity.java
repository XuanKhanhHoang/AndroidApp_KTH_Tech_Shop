package com.ktm.kthtechshop.activity_and_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ktm.kthtechshop.R;

public class StartActivity extends AppCompatActivity {

    Animation appearAni;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        appearAni = AnimationUtils.loadAnimation(this, R.anim.appear_animation);
        logo = findViewById(R.id.logoImage);

        logo.setAnimation(appearAni);

        new Handler().postDelayed(() -> {
            Intent myIntent = new Intent(StartActivity.this, MainActivity.class);
            StartActivity.this.startActivity(myIntent);
            finish();
        }, 1000);

    }
}