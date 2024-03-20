package com.ktm.kthtechshop.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ktm.kthtechshop.AppSharedPreferences;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.localhostIp;
import com.ktm.kthtechshop.utils.Utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;


public class MyAccountFragment extends Fragment {
    private AppSharedPreferences appSharedPreferences;
    private Context context;
    private Activity activity;
    private View view;
    private ApiServiceObject apiServiceObject;
    private Call<LoginResponse> callCheckingValidUserSession;
    private ImageView customerLogo;
    private TextView logoutTextView, customerNameTxtView;
    Executor executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    static MyAccountFragment fragment;

    private MyAccountFragment() {
    }

    public static MyAccountFragment getInstance() {
        if (fragment == null) fragment = new MyAccountFragment();
        return new MyAccountFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = getActivity();
        appSharedPreferences = new AppSharedPreferences((AppCompatActivity) context);
        apiServiceObject = new ApiServiceObject();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callCheckingValidUserSession == null) return;
        callCheckingValidUserSession.cancel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void handleUI() {
        if (appSharedPreferences.getAccessToken().isEmpty()) {
            logoutTextView.setVisibility(View.GONE);
            customerNameTxtView.setText("Đăng nhập / Đăng kí");
            customerLogo.setImageResource(R.drawable.ic_user_solid);
        } else {
            String customerName = appSharedPreferences.getFirstName();
            if (!customerName.isEmpty()) customerNameTxtView.setText(customerName);
            executor.execute(() -> {
                Bitmap bitmap = Utils.getBitmapFromURL(localhostIp.LOCALHOST_IP.getValue() + ":3000" + appSharedPreferences.getAvatar());
                handler.post(() -> {
                    if (bitmap != null) customerLogo.setImageBitmap(bitmap);
                });
            });
        }
    }

    public void updateUI() {
        handleUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        customerLogo = view.findViewById(R.id.MyAccountFragment_UserLogo);
        customerNameTxtView = view.findViewById(R.id.MyAccountFragment_UserName);

        logoutTextView = view.findViewById(R.id.MyAccountFragment_Logout);
        logoutTextView.setOnClickListener(v -> {
            appSharedPreferences.setIsAuth(false);
            appSharedPreferences.setAccessToken("");
            BottomNavigationView navigationView = getActivity().findViewById(R.id.MainActivity_BottomNavigation);
            navigationView.getMenu().getItem(0).setChecked(true);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            HomePageFragment homePageFragment = (HomePageFragment) fragmentManager.findFragmentByTag("1");
            MyAccountFragment myAccountFragment = (MyAccountFragment) fragmentManager.findFragmentByTag("2");
            homePageFragment.updateUI();
            fragmentTransaction.hide(myAccountFragment).show(homePageFragment);
            ((MainActivity) activity).active = homePageFragment;
            fragmentTransaction.commit();
        });
        handleUI();
        return view;
    }
}