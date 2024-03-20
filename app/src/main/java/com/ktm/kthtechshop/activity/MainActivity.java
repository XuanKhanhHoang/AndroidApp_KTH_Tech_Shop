package com.ktm.kthtechshop.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    BottomNavigationView navigationView;
    private HomePageFragment homePageFragment;
    private MyAccountFragment myAccountFragment;
    public Fragment active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Sử dụng View Binding
        navigationView = binding.MainActivityBottomNavigation;
        homePageFragment = HomePageFragment.getInstance();
        myAccountFragment = MyAccountFragment.getInstance();
        active = homePageFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.MainActivity_FrameContainer, myAccountFragment, "2").hide(myAccountFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.MainActivity_FrameContainer, homePageFragment, "1").commit();
        // Lazy initialization của Fragment
        navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.MainBottomMenu_ToHomePage) {
                homePageFragment.updateUI();
                getSupportFragmentManager().beginTransaction().hide(active).show(homePageFragment).commit();
                active = homePageFragment;
            } else if (item.getItemId() == R.id.MainBottomMenu_ToAccountPage) {
                myAccountFragment.updateUI();
                getSupportFragmentManager().beginTransaction().hide(active).show(myAccountFragment).commit();
                active = myAccountFragment;
            }
            return true;
        });
        navigationView.setOnItemReselectedListener(item -> {

        });
    }
}