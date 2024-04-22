package com.ktm.kthtechshop.activity_and_fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.ktm.kthtechshop.dto.LoginResponse;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private Button Register, ToLogin;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    private TextInputLayout email_TxtInpLayout, userName_TxtInpLayout, phoneNumber_TxtInpLayout, passWord_TxtInpLayout, RepassWord_TxtInpLayout, firstName_TxtInpLayout, lastName_TxtInputLayout, address_TxtInputLayout;
    private ImageView avatarImageView;
    private RadioGroup genderRadioGr;

    private Uri currentAvatarURI;
    private ApiServiceObject apiServiceObject;
    private boolean isLoading = false;
    private AppSharedPreferences appSharedPreferences;

    public void onBackPressed() {
        if (isLoading) {
            Toast.makeText(this, "Vui Lòng chờ", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Register = findViewById(R.id.Register_Btn_Register);
        ToLogin = findViewById(R.id.Register_Btn_ToLogin);
        email_TxtInpLayout = findViewById(R.id.RegisterActivity_Email);
        userName_TxtInpLayout = findViewById(R.id.RegisterActivity_UserName);
        phoneNumber_TxtInpLayout = findViewById(R.id.RegisterActivity_PhoneNumber);
        passWord_TxtInpLayout = findViewById(R.id.RegisterActivity_Password);
        RepassWord_TxtInpLayout = findViewById(R.id.RegisterActivity_RePassword);
        avatarImageView = findViewById(R.id.RegisterActivity_UserAvatar);
        firstName_TxtInpLayout = findViewById(R.id.RegisterActivity_FirstName);
        lastName_TxtInputLayout = findViewById(R.id.RegisterActivity_LastName);
        address_TxtInputLayout = findViewById(R.id.RegisterActivity_Address);
        avatarImageView = findViewById(R.id.RegisterActivity_UserAvatar);

        genderRadioGr = findViewById(R.id.RegisterActivity_GenderRadioGr);
        apiServiceObject = new ApiServiceObject();
        appSharedPreferences = new AppSharedPreferences(this);

        findViewById(R.id.Global_BackBtn).setOnClickListener(v -> finish());
        ToLogin.setOnClickListener(v -> {
            if (isLoading) {
                Toast.makeText(this, "Vui Lòng chờ", Toast.LENGTH_SHORT).show();
                return;
            }
            finish();
        });

        Register.setOnClickListener(v -> {
            Register.setText("Loading");
            if (!validateInput(email_TxtInpLayout, userName_TxtInpLayout, passWord_TxtInpLayout, passWord_TxtInpLayout, RepassWord_TxtInpLayout, firstName_TxtInpLayout, lastName_TxtInputLayout, address_TxtInputLayout)) {
                return;
            }
            String email = Objects.requireNonNull(email_TxtInpLayout.getEditText()).getText().toString();
            String phoneNumber = Objects.requireNonNull(phoneNumber_TxtInpLayout.getEditText()).getText().toString();
            String password = Objects.requireNonNull(passWord_TxtInpLayout.getEditText()).getText().toString();
            String rePassword = Objects.requireNonNull(RepassWord_TxtInpLayout.getEditText()).getText().toString();
            String userName = Objects.requireNonNull(RepassWord_TxtInpLayout.getEditText()).getText().toString();
            String firstName = Objects.requireNonNull(firstName_TxtInpLayout.getEditText()).getText().toString();
            String lastName = Objects.requireNonNull(lastName_TxtInputLayout.getEditText()).getText().toString();
            String address = Objects.requireNonNull(address_TxtInputLayout.getEditText()).getText().toString();

            RequestBody requestFile = null;
            File file = null;
            if (currentAvatarURI != null) {
                file = new File(Utils.getRealPathFromURI(RegisterActivity.this, currentAvatarURI));
                requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            }

            HashMap<String, RequestBody> data = new HashMap<>();
            data.put("first_name", RequestBody.create(MultipartBody.FORM, firstName));
            data.put("last_name", RequestBody.create(MediaType.parse("multipart/form-data"), lastName));
            data.put("user_name", RequestBody.create(MediaType.parse("multipart/form-data"), userName));
            data.put("gender", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(genderRadioGr.getCheckedRadioButtonId() != R.id.RegisterActivity_GenderMale)));
            data.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), email));
            data.put("phone_number", RequestBody.create(MediaType.parse("multipart/form-data"), phoneNumber));
            data.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), address));
            data.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), password));

            if (requestFile != null)
                data.put("avatar\"; filename=\"" + file.getName(), requestFile); // Thêm file vào HashMap
            apiServiceObject.apiServices.createCustomer(data).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    Register.setText("Đăng Kí");
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        appSharedPreferences.setAllAttribute(response.body().value.userId, response.body().accessToken, response.body().value.firstName, response.body().value.avatar);
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        finish();
                        return;
                    }
                    if (response.code() == 409) {
                        Toast.makeText(RegisterActivity.this, "Email hoặc Số điện thoại hoặc Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(RegisterActivity.this, "Tạo tài khoản thất bại", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    Register.setText("Đăng Kí");
                    Toast.makeText(RegisterActivity.this, "Tạo tài khoản thất bại", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }
            });

        });
        avatarImageView.setOnClickListener(v -> openImageChooser());
        genderRadioGr.check(R.id.RegisterActivity_GenderMale);
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
        String email = Objects.requireNonNull(email_TxtInpLayout.getEditText()).getText().toString();
        String phoneNumber = Objects.requireNonNull(phoneNumber_TxtInpLayout.getEditText()).getText().toString();
        String password = Objects.requireNonNull(passWord_TxtInpLayout.getEditText()).getText().toString();
        String rePassword = Objects.requireNonNull(RepassWord_TxtInpLayout.getEditText()).getText().toString();
        if (!password.equals(rePassword)) {
            RepassWord_TxtInpLayout.setError("Password không khớp");
            isValid = false;
        }
        if (!Utils.isValidEmail(email)) {
            email_TxtInpLayout.setError("Email không hợp lệ");
            isValid = false;
        }
        if (!Utils.isValidVietnamesePhoneNumber(phoneNumber)) {
            phoneNumber_TxtInpLayout.setError("Số điện thoại không hợp lệ");
            isValid = false;
        }
        return isValid;
    }

    /**
     * @noinspection deprecation
     */
    public void openImageChooser() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Quyền chưa được cấp, yêu cầu quyền tại đây
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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
            avatarImageView.setImageURI(imageUri);
            currentAvatarURI = imageUri;
        }
    }
}