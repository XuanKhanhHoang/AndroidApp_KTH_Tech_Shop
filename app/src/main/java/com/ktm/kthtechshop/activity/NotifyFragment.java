package com.ktm.kthtechshop.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ktm.kthtechshop.AppSharedPreferences;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotifyFragment extends Fragment {
    private AppSharedPreferences appSharedPreferences;
    private Context context;
    private Activity activity;
    private View view;
    private ApiServiceObject apiServiceObject;

    static NotifyFragment fragment;
    private boolean isAuth = false;
    private Call<LoginResponse> callCheckingValidUserSession;

    private NotifyFragment() {
    }

    public static NotifyFragment getInstance() {
        if (fragment == null) fragment = new NotifyFragment();
        return new NotifyFragment();
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
        if (appSharedPreferences.getAccessToken().isEmpty() || !isAuth) {

        } else {
            String customerName = appSharedPreferences.getFirstName();

        }
    }

    public void updateUI() {
        handleUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_account, container, false);

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