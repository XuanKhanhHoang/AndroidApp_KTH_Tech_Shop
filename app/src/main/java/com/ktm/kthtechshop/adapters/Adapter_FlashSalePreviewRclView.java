package com.ktm.kthtechshop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.ProductPreviewItem;

import java.util.ArrayList;

public class Adapter_FlashSalePreviewRclView extends RecyclerView.Adapter<ViewHolder_ProductPreviewRclView> {

    Context context;
    ArrayList<ProductPreviewItem> newProductItemArrayList;

    public Adapter_FlashSalePreviewRclView(Context context, ArrayList<ProductPreviewItem> newProductItemArrayList) {
        this.context = context;
        this.newProductItemArrayList = newProductItemArrayList;
    }


    @NonNull
    @Override
    public ViewHolder_ProductPreviewRclView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_ProductPreviewRclView(LayoutInflater.from(context).inflate(R.layout.layout_product_preview_flashsale, parent, false));

    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder_ProductPreviewRclView holder, int position) {
        holder.productName.setText(newProductItemArrayList.get(position).getName());
        holder.sellingPrice.setText(String.format("%,d", newProductItemArrayList.get(position).getSellingPrice()).replace(',', '.') + "đ");
        if (newProductItemArrayList.get(position).getDiscount() > 0) {
            holder.originalPrice.setText(String.format("%,d", newProductItemArrayList.get(position).getOriginalPrice()).replace(',', '.') + "đ");
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discount.setText("-" + newProductItemArrayList.get(position).getDiscount().toString() + "%");
        } else {
            holder.discount.setVisibility(View.INVISIBLE);
            holder.originalPrice.setHeight(0);

            holder.originalPrice.setVisibility(View.INVISIBLE);
        }
        holder.logo.setImageResource(R.drawable.s23_xang);
    }

    @Override
    public int getItemCount() {
        return newProductItemArrayList.size();
    }
}

