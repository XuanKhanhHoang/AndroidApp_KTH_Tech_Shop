package com.ktm.kthtechshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.ProductOption;
import com.ktm.kthtechshop.listeners.ProductDetail_ProductOption_ClickListener;

import java.util.ArrayList;

public class Adapter_ProductDetail_ProductOptionRclView extends RecyclerView.Adapter<Adapter_ProductDetail_ProductOptionRclView.ViewHolder> {
    Integer selectedItem;
    Context context;
    ArrayList<ProductOption> optionArrayList;
    private ProductDetail_ProductOption_ClickListener listener;

    public Adapter_ProductDetail_ProductOptionRclView(Context context, ArrayList<ProductOption> optionArrayList, ProductDetail_ProductOption_ClickListener listener) {
        this.context = context;
        this.optionArrayList = optionArrayList;
        selectedItem = 0;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.product_detail_option_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductOption ct = optionArrayList.get(position);
        holder.checkBox.setText(ct.name);
        holder.checkBox.setChecked(position == selectedItem);
    }


    @Override
    public int getItemCount() {
        return optionArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.ProductDetail_AnOption);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, optionArrayList.size());
                    listener.onClick(getAdapterPosition());
                }
            });
            ;
        }
    }
}

