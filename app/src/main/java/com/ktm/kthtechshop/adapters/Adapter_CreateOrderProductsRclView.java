package com.ktm.kthtechshop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.ProductOptionWithProductName;
import com.ktm.kthtechshop.model.CreateOrderProductOptionItemModel;
import com.ktm.kthtechshop.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Adapter_CreateOrderProductsRclView extends RecyclerView.Adapter<Adapter_CreateOrderProductsRclView.ViewHolder> {

    Context context;
    ArrayList<CreateOrderProductOptionItemModel> products;

    public Adapter_CreateOrderProductsRclView(Context context, ArrayList<CreateOrderProductOptionItemModel> products) {
        this.context = context;
        this.products = products;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.create_order_product_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductOptionWithProductName option = products.get(position).option;
        holder.productName.setText(Utils.truncateText(option.product.name));
        holder.sellingPrice.setText(Utils.formatPrice(option.sellingPrice));
        if (option.discount != 0) {
            holder.originalPrice.setText(Utils.formatPrice(option.originalPrice));
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.originalPrice.setVisibility(View.INVISIBLE);
        }
        holder.amount.setText(String.valueOf(products.get(position).amount));
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Bitmap bitmap = Utils.getBitmapFromURL(option.image);
            handler.post(() -> {
                if (bitmap != null) {
                    holder.image.setImageBitmap(bitmap);
                } else {
                    holder.image.setImageResource(R.drawable.img_image_broke);
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView productName, optionName, sellingPrice, originalPrice, amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.CreateOrderProductItemLayout_ProductImage);
            productName = itemView.findViewById(R.id.CreateOrderProductItemLayout_ProductName);
            optionName = itemView.findViewById(R.id.CreateOrderProductItemLayout_ProductOptionName);
            sellingPrice = itemView.findViewById(R.id.CreateOrderProductItemLayout_SellingPrice);
            originalPrice = itemView.findViewById(R.id.CreateOrderProductItemLayout_OriginalPrice);
            amount = itemView.findViewById(R.id.CreateOrderProductItemLayout_Amount);
        }

    }
}

