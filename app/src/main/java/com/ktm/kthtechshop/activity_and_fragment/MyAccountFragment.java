package com.ktm.kthtechshop.activity_and_fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.Utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

    @SuppressLint("StaticFieldLeak")
    static MyAccountFragment fragment;
    private boolean isAuth = false;

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

    @SuppressLint("SetTextI18n")
    private void handleUI() {
        isAuth = appSharedPreferences.getIsAuth();
        if (appSharedPreferences.getAccessToken().isEmpty() || !isAuth) {
            logoutTextView.setVisibility(View.GONE);
            customerNameTxtView.setText("Đăng nhập / Đăng kí");
            customerLogo.setImageResource(R.drawable.ic_user_solid);
        } else {
            String customerName = appSharedPreferences.getFirstName();
            if (!customerName.isEmpty()) customerNameTxtView.setText(customerName);
            logoutTextView.setVisibility(View.VISIBLE);
            executor.execute(() -> {
                Bitmap bitmap = Utils.getBitmapFromURL(appSharedPreferences.getAvatar());
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
            assert homePageFragment != null;
            homePageFragment.updateUI();
            assert myAccountFragment != null;
            fragmentTransaction.hide(myAccountFragment).show(homePageFragment);
            ((MainActivity) activity).active = homePageFragment;
            fragmentTransaction.commit();
        });
        view.findViewById(R.id.MyAccountFragment_AppInfo).setOnClickListener(v -> {
            Intent it = new Intent(activity, AppInfoActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(it);
        });
        view.findViewById(R.id.MyAccountFragment_ChPlay).setOnClickListener(v -> {
            final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
        customerLogo.setOnClickListener(v -> {
            if (!isAuth) {
                Intent it = new Intent(activity, LoginActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(it);
                return;
            }
            Intent it = new Intent(activity, UserActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(it);
        });
        view.findViewById(R.id.MyAccountFragment_MyOrders).setOnClickListener(v -> {
            if (!isAuth) {
                Intent it = new Intent(activity, LoginActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(it);
                return;
            }
            Intent it = new Intent(activity, UserActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(it);
        });
        customerNameTxtView.setOnClickListener(v -> {
            if (!isAuth) {
                Intent it = new Intent(activity, LoginActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(it);
            }
            Intent it = new Intent(activity, UserActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(it);
        });
        checkValidUserSession();
        handleUI();
        return view;
    }

    private void checkValidUserSession() {
        String accessToken = appSharedPreferences.getAccessToken();
        if (!accessToken.isEmpty() || appSharedPreferences.getIsAuth()) {
            callCheckingValidUserSession = apiServiceObject.apiServices.loginWithAccessToken("Bearer " + accessToken);
            callCheckingValidUserSession.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (call.isCanceled()) {
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        appSharedPreferences.setAllAttribute(response.body().value.userId,
                                response.body().accessToken, response.body().value.firstName,
                                response.body().value.avatar);
                        isAuth = true;
                        handleUI();
                    } else {
                        Toast.makeText(activity, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
                        appSharedPreferences.setIsAuth(false);
                        handleUI();

                    }

                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    if (call.isCanceled()) {
                        return;
                    }
                    Toast.makeText(activity, "Xác thực thất bại, vui lòng đăng nhập lại ", Toast.LENGTH_SHORT).show();
                    handleUI();

                }
            });
        }
    }

}