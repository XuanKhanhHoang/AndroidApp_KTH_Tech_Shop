package com.ktm.kthtechshop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.activity.ProductDetailActivity;
import com.ktm.kthtechshop.model.ProductPreview;
import com.ktm.kthtechshop.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Adapter_ProductPreviewRclView extends RecyclerView.Adapter<ViewHolder_ProductPreviewRclView> {

    Context context;
    ArrayList<ProductPreview> newProductItemArrayList;

    public Adapter_ProductPreviewRclView(Context context, ArrayList<ProductPreview> newProductItemArrayList) {
        this.context = context;
        this.newProductItemArrayList = newProductItemArrayList;
    }


    @NonNull
    @Override
    public ViewHolder_ProductPreviewRclView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_ProductPreviewRclView(LayoutInflater.from(context).inflate(R.layout.product_preview_layout, parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ProductPreviewRclView holder, int position) {
        ProductPreview item = newProductItemArrayList.get(position);
        holder.productName.setText(Utils.truncateText(item.getName()));
        holder.sellingPrice.setText(Utils.formatPrice(item.getSellingPrice()));
        if (newProductItemArrayList.get(position).getDiscount() > 0) {
            holder.originalPrice.setText(Utils.formatPrice(item.getOriginal_price()));
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discount.setText("-" + item.getDiscount().toString() + "%");
        } else {
            holder.discount.setVisibility(View.INVISIBLE);
            holder.originalPrice.setVisibility(View.GONE);
        }
        holder.rootLayout.setOnClickListener(v -> {
            Integer productId = item.getProduct_id();
            Intent it = new Intent(v.getContext(), ProductDetailActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra("productId", productId);
            v.getContext().startActivity(it);
        });
        Bitmap logoBitmap = item.getBitmapLogo();
        if (logoBitmap == null) {
            holder.logo.setImageBitmap(null);
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                Bitmap bitmap = Utils.getBitmapFromURL(item.getLogo());
                handler.post(() -> {
                    if (bitmap != null) {
                        holder.logo.setImageBitmap(bitmap);
                        item.setBitmapLogo(bitmap);
                    } else {
                        holder.logo.setImageResource(R.drawable.img_image_broke);
                    }
                });
            });
        } else {
            holder.logo.setImageBitmap(logoBitmap);
        }
        holder.ratingBar.setRating(item.getRating());
    }

    @Override
    public int getItemCount() {
        return newProductItemArrayList.size();
    }
}

class ViewHolder_ProductPreviewRclView extends RecyclerView.ViewHolder {
    ImageView logo;
    TextView discount, productName, originalPrice, sellingPrice;
    RatingBar ratingBar;
    LinearLayout rootLayout;

    public ViewHolder_ProductPreviewRclView(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.logoImage);
        discount = itemView.findViewById(R.id.discount);
        productName = itemView.findViewById(R.id.productName);
        originalPrice = itemView.findViewById(R.id.productOriginalPrice);
        sellingPrice = itemView.findViewById(R.id.productSellingPrice);
        ratingBar = itemView.findViewById(R.id.ProductDetailRatingBar);
        rootLayout = itemView.findViewById(R.id.productPreviewLayout);
    }

}
