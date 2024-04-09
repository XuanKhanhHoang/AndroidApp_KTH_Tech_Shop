package com.ktm.kthtechshop.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String formatPrice(Integer str) {
        return String.format("%,d", str).replace(',', '.') + "đ";
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public static boolean isValidVietnamesePhoneNumber(String phoneNumber) {
        String phoneRegex = "^(0?)(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])[0-9]{7}$";
        return phoneNumber.matches(phoneRegex);
    }

    // Kiểm tra mật khẩu hợp lệ (>6 ký tự, có cả chữ hoa, chữ thường)
    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        return password.matches(passwordRegex);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String truncateText(String text) {
        if (text.length() <= 40) return text;
        return text.substring(0, 37) + "...";
    }

    public static String formatTime(String dateString) {
        // Phân tích chuỗi ngày tháng ISO 8601 thành một đối tượng ZonedDateTime
        ZonedDateTime zonedDateTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            zonedDateTime = ZonedDateTime.parse(dateString);
        }

        // Định dạng lại đối tượng ZonedDateTime thành chuỗi ngày tháng theo ý muốn
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return zonedDateTime.format(formatter);
        }
        return "";
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
