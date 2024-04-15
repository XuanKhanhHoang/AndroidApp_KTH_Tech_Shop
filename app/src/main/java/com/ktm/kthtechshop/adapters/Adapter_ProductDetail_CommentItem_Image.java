package com.ktm.kthtechshop.adapters;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.ProductDetail_Comment_Image;
import com.ktm.kthtechshop.listeners.ProductDetail_Comment_ImageClickListener;
import com.ktm.kthtechshop.utils.Utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Adapter_ProductDetail_CommentItem_Image extends RecyclerView.Adapter<Adapter_ProductDetail_CommentItem_Image.ViewHolder> {

    private ProductDetail_Comment_Image[] images; // Array of image resource IDs
    private ProductDetail_Comment_ImageClickListener listener;
    private int curPos = -1;

    public Adapter_ProductDetail_CommentItem_Image(ProductDetail_Comment_Image[] images, ProductDetail_Comment_ImageClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Adjust the scaling if needed
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_detail_comment_item_image_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetail_Comment_Image item = images[position];
        if (item.bitmap != null) {
            holder.imageView.setImageBitmap(item.bitmap);
            return;
        }
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Bitmap bitmap = Utils.getBitmapFromURL(item.image);
            handler.post(() -> {
                if (bitmap != null) {
                    images[position].bitmap = bitmap;
                    holder.imageView.setImageBitmap(item.bitmap);
                } else {
                    holder.imageView.setImageResource(R.drawable.img_image_broke);
                }

            });

        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_ImageItemLayout_ImgView);
            imageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                boolean isUsing = curPos == position;
                if (isUsing) {
                    curPos = -1;
                    listener.onImageCLick(null);
                } else {
                    curPos = position;
                    listener.onImageCLick(images[position].bitmap);

                }
            });
        }
    }
}

