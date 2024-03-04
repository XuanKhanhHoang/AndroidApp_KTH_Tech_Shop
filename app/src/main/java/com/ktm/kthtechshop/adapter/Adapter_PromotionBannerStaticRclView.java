package com.ktm.kthtechshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.ImageLoadFromURL;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.PromotionBannerItem;

import java.util.ArrayList;


public class Adapter_PromotionBannerStaticRclView extends RecyclerView.Adapter<ViewHolder_PromotionBannerStaticRclView> {

    Context context;
    ArrayList<PromotionBannerItem> promotionBannerList;

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
        new ImageLoadFromURL(promotionBannerList.get(position).getImage(), holder.image).execute();
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

