package com.ktm.kthtechshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ktm.kthtechshop.R;

public class MainActivity extends AppCompatActivity {

    Animation appearAni;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        appearAni = AnimationUtils.loadAnimation(this, R.anim.appear_animation);
        logo = findViewById(R.id.logoImage);

        logo.setAnimation(appearAni);

        new Handler().postDelayed(() -> {
            Intent myIntent = new Intent(MainActivity.this, HomePageActivity.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        }, 1000);

    }
}