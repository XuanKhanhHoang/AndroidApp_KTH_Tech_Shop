package com.ktm.kthtechshop.activity_and_fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.api.ApiServiceObject;
import com.ktm.kthtechshop.dto.UserFullDetail;
import com.ktm.kthtechshop.dto.updateUserDetailResponse;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserEditInfoActivity extends AppCompatActivity {
    private TextInputLayout userNameTxtILayout, lastNameTxtILayout, firstNameTxtILayout, emailTxtILayout, addressTxtILayout, phoneNumberTxtILayout;

    private RadioGroup genderRadioGr;
    private Button confirmBtn;
    private ImageView avatar;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    private Uri currentAvatarURI;
    private ApiServiceObject apiServiceObject;
    private boolean isLoading = false;

    private AppSharedPreferences appSharedPreferences;

    @Override
    public void onBackPressed() {
        if (isLoading) {
            Toast.makeText(this, "Vui Lòng chờ", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_info);
        refChildComponents();
        apiServiceObject = new ApiServiceObject();
        appSharedPreferences = new AppSharedPreferences(this);
        findViewById(R.id.Global_BackBtn).setOnClickListener(v -> finish());
        String accessToken = appSharedPreferences.getBearerAccessToken();
        if (accessToken.isEmpty()) {
            Intent it = new Intent(UserEditInfoActivity.this, LoginActivity.class);
            this.startActivity(it);
            finish();
        }
        apiServiceObject.apiServices.getCustomerDetail(accessToken).enqueue(new Callback<UserFullDetail>() {
            @Override
            public void onResponse(@NonNull Call<UserFullDetail> call, @NonNull Response<UserFullDetail> response) {
                if (response.isSuccessful()) {
                    UserFullDetail bd = response.body();
                    if (bd == null) return;
                    Objects.requireNonNull(firstNameTxtILayout.getEditText()).setText(bd.firstName);
                    Objects.requireNonNull(lastNameTxtILayout.getEditText()).setText(bd.lastName);
                    Objects.requireNonNull(userNameTxtILayout.getEditText()).setText(bd.loginName);
                    genderRadioGr.check(bd.gender ? R.id.UserEditInfoActivity_GenderMale : R.id.UserEditInfoActivity_GenderFemale);
                    Objects.requireNonNull(emailTxtILayout.getEditText()).setText(bd.email);
                    Objects.requireNonNull(phoneNumberTxtILayout.getEditText()).setText(bd.phoneNumber);
                    Objects.requireNonNull(addressTxtILayout.getEditText()).setText(bd.address);
                    Executor executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());
                    executor.execute(() -> {
                        Bitmap bitmap = Utils.getBitmapFromURL(bd.avatar);
                        handler.post(() -> {
                            if (bitmap != null) {
                                avatar.setImageBitmap(bitmap);
                            }
                        });
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserFullDetail> call, @NonNull Throwable t) {

            }
        });

        confirmBtn.setOnClickListener(v -> {
            if (isLoading) return;
            isLoading = true;
            if (!validateInput(userNameTxtILayout, lastNameTxtILayout, firstNameTxtILayout, emailTxtILayout, addressTxtILayout, phoneNumberTxtILayout)) {
                return;
            }

            RequestBody requestFile = null;
            File file = null;
            if (currentAvatarURI != null) {
                file = new File(Utils.getRealPathFromURI(UserEditInfoActivity.this, currentAvatarURI));
                requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            }

            String userName = Objects.requireNonNull(userNameTxtILayout.getEditText()).getText().toString();
            String firstName = Objects.requireNonNull(firstNameTxtILayout.getEditText()).getText().toString();
            String lastName = Objects.requireNonNull(lastNameTxtILayout.getEditText()).getText().toString();
            String email = Objects.requireNonNull(emailTxtILayout.getEditText()).getText().toString();
            String phoneNumber = Objects.requireNonNull(phoneNumberTxtILayout.getEditText()).getText().toString();
            String address = Objects.requireNonNull(addressTxtILayout.getEditText()).getText().toString();
            HashMap<String, RequestBody> data = new HashMap<>();
            data.put("first_name", RequestBody.create(MultipartBody.FORM, firstName));
            data.put("last_name", RequestBody.create(MediaType.parse("multipart/form-data"), lastName));
            data.put("user_name", RequestBody.create(MediaType.parse("multipart/form-data"), userName));
            data.put("gender", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(genderRadioGr.getCheckedRadioButtonId() != R.id.UserEditInfoActivity_GenderMale)));
            data.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), email));
            data.put("phoneNumber", RequestBody.create(MediaType.parse("multipart/form-data"), phoneNumber));
            data.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), address));
            if (requestFile != null)
                data.put("avatar\"; filename=\"" + file.getName(), requestFile); // Thêm file vào HashMap
            new ApiServiceObject().apiServices.editUser(accessToken, data).enqueue(new Callback<updateUserDetailResponse>() {
                @Override
                public void onResponse(@NonNull Call<updateUserDetailResponse> call, @NonNull Response<updateUserDetailResponse> response) {
                    if (response.isSuccessful()) {


                        Intent intent = new Intent(UserEditInfoActivity.this, UserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(UserEditInfoActivity.this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        finish();
                        return;
                    }
                    isLoading = false;
                    Toast.makeText(UserEditInfoActivity.this, "Thay đổi thông tin thất bại", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(@NonNull Call<updateUserDetailResponse> call, @NonNull Throwable t) {
                    Toast.makeText(UserEditInfoActivity.this, "Thay đổi thông tin thất bại", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }
            });

        });
        findViewById(R.id.UserEditInfoActivity_CancelBtn).setOnClickListener(v -> {
            if (isLoading) {
                Toast.makeText(this, "Vui Lòng chờ", Toast.LENGTH_SHORT).show();
                return;
            }
            finish();
        });
        avatar.setOnClickListener(v -> openImageChooser());
        genderRadioGr.check(R.id.UserEditInfoActivity_GenderMale);

    }

    /**
     * @noinspection deprecation
     */
    public void openImageChooser() {
        if (ContextCompat.checkSelfPermission(UserEditInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Quyền chưa được cấp, yêu cầu quyền tại đây
            ActivityCompat.requestPermissions(UserEditInfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bạn cần cấp quyền", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            avatar.setImageURI(imageUri);
            currentAvatarURI = imageUri;
        }
    }

    private boolean validateInput(TextInputLayout... layouts) {
        boolean isValid = true;
        for (TextInputLayout layout : layouts) {
            if (Objects.requireNonNull(layout.getEditText()).getText().toString().trim().isEmpty()) {
                layout.setError("Trường này không được để trống");
                isValid = false;
            } else {
                layout.setError(null); // Xóa lỗi nếu đã nhập
            }
        }
        String email = Objects.requireNonNull(emailTxtILayout.getEditText()).getText().toString();
        String phoneNumber = Objects.requireNonNull(phoneNumberTxtILayout.getEditText()).getText().toString();
        if (!Utils.isValidEmail(email)) {
            emailTxtILayout.setError("Email không hợp lệ");
            isValid = false;
        }
        if (!Utils.isValidVietnamesePhoneNumber(phoneNumber)) {
            phoneNumberTxtILayout.setError("Số điện thoại không hợp lệ");
            isValid = false;
        }
        return isValid;
    }

    void refChildComponents() {
        userNameTxtILayout = findViewById(R.id.UserEditInfoActivity_UserNameTxtILayout);
        lastNameTxtILayout = findViewById(R.id.UserEditInfoActivity_LastNameTxtILayout);
        firstNameTxtILayout = findViewById(R.id.UserEditInfoActivity_FirstNameTxtILayout);
        emailTxtILayout = findViewById(R.id.UserEditInfoActivity_EmailTxtILayout);
        phoneNumberTxtILayout = findViewById(R.id.UserEditInfoActivity_PhoneNumberTxtILayout);
        addressTxtILayout = findViewById(R.id.UserEditInfoActivity_AddressTxtILayout);
        confirmBtn = findViewById(R.id.UserEditInfoActivity_ConfirmBtn);
        avatar = findViewById(R.id.UserEditInfoActivity_UserAvatar);
        genderRadioGr = findViewById(R.id.UserEditInfoActivity_GenderRadioGr);
    }
}