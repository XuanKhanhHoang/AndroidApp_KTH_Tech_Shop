package com.ktm.kthtechshop.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.ImageLoadFromURL;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.Utils;
import com.ktm.kthtechshop.dto.ProductPreviewItem;
import com.ktm.kthtechshop.localhostIp;

import java.util.ArrayList;

public class Adapter_ProductPreviewRclView extends RecyclerView.Adapter<ViewHolder_ProductPreviewRclView> {

    Context context;
    ArrayList<ProductPreviewItem> newProductItemArrayList;

    public Adapter_ProductPreviewRclView(Context context, ArrayList<ProductPreviewItem> newProductItemArrayList) {
        this.context = context;
        this.newProductItemArrayList = newProductItemArrayList;
    }

    public Adapter_ProductPreviewRclView() {

    }

    @NonNull
    @Override
    public ViewHolder_ProductPreviewRclView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_ProductPreviewRclView(LayoutInflater.from(context).inflate(R.layout.product_preview_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ProductPreviewRclView holder, int position) {
        ProductPreviewItem item = newProductItemArrayList.get(position);
        holder.productName.setText(item.getName());
        holder.sellingPrice.setText(Utils.formatPrice(item.getSellingPrice()));
        if (newProductItemArrayList.get(position).getDiscount() > 0) {
            holder.originalPrice.setText(Utils.formatPrice(item.getOriginal_price()));
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discount.setText("-" + item.getDiscount().toString() + "%");
        } else {
            holder.discount.setVisibility(View.INVISIBLE);
            holder.originalPrice.setHeight(0);

            holder.originalPrice.setVisibility(View.INVISIBLE);
        }
//        holder.logo.setImageResource(R.drawable.s23_xang);
        new ImageLoadFromURL(localhostIp.LOCALHOST_IP.getValue() + ":3000" + item.getLogo(), holder.logo).execute();
        holder.ratingBar.setRating(item.getRating());
    }

    @Override
    public int getItemCount() {
        return Math.min(newProductItemArrayList.size(), 6);
    }
}

class ViewHolder_ProductPreviewRclView extends RecyclerView.ViewHolder {
    ImageView logo;
    TextView discount, productName, originalPrice, sellingPrice;
    RatingBar ratingBar;

    public ViewHolder_ProductPreviewRclView(@NonNull View itemView) {
        super(itemView);
        logo = itemView.findViewById(R.id.logoImage);
        discount = itemView.findViewById(R.id.discount);
        productName = itemView.findViewById(R.id.productName);
        originalPrice = itemView.findViewById(R.id.productOriginalPrice);
        sellingPrice = itemView.findViewById(R.id.productSellingPrice);
        ratingBar = itemView.findViewById(R.id.ProductDetailRatingBar);

    }
}