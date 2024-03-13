package com.ktm.kthtechshop.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoadFromURL extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;

    private int IdDrawableImageError;

    public ImageLoadFromURL(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
        IdDrawableImageError = -1;
    }

    public ImageLoadFromURL(String url, ImageView imageView, int IdDrawableImageError) {
        this.url = url;
        this.imageView = imageView;
        this.IdDrawableImageError = IdDrawableImageError;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) imageView.setImageBitmap(result);
        else if (IdDrawableImageError != -1) imageView.setImageResource(IdDrawableImageError);
    }

}