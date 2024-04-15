package com.ktm.kthtechshop.adapters;

import android.content.Context;
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
import com.ktm.kthtechshop.dto.PromotionBannerItem;
import com.ktm.kthtechshop.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Adapter_PromotionBannerStaticRclView extends RecyclerView.Adapter<ViewHolder_PromotionBannerStaticRclView> {

    Context context;
    static ArrayList<PromotionBannerItem> promotionBannerList;

    public Adapter_PromotionBannerStaticRclView(Context context, ArrayList<PromotionBannerItem> promotionBannerList) {
        this.context = context;
        this.promotionBannerList = promotionBannerList;
    }


    @NonNull
    @Override
    public ViewHolder_PromotionBannerStaticRclView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_PromotionBannerStaticRclView(LayoutInflater.from(context).inflate(R.layout.hompage_promotionbanner_rcl_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_PromotionBannerStaticRclView holder, int position) {
        PromotionBannerItem item = promotionBannerList.get(position);
        if (item.getBitmap() == null) {
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            executor.execute(() -> {
                Bitmap bitmap = Utils.getBitmapFromURL(item.getImage());
                handler.post(() -> {
                    item.setBitmap(bitmap);
                    holder.image.setImageBitmap(bitmap);
                    promotionBannerList.set(position, item);
                });
            });
        } else {
            holder.image.setImageBitmap(item.getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return promotionBannerList.size();
    }
}

class ViewHolder_PromotionBannerStaticRclView extends RecyclerView.ViewHolder {
    ImageView image;

    public ViewHolder_PromotionBannerStaticRclView(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.bannerItem);
    }
}

