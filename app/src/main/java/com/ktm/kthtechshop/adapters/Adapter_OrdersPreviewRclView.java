package com.ktm.kthtechshop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.Order;
import com.ktm.kthtechshop.dto.OrderListItem;
import com.ktm.kthtechshop.utils.Utils;

import java.util.ArrayList;

public class Adapter_OrdersPreviewRclView extends RecyclerView.Adapter<Adapter_OrdersPreviewRclView.ViewHolder> {

    Context context;
    ArrayList<Order> orders;

    public Adapter_OrdersPreviewRclView(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.order_preview_item_layout, parent, false));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = "";
        ArrayList<OrderListItem> orderListItemList = orders.get(position).orderList;
        for (OrderListItem item : orderListItemList
        ) {
            name += item.option.products.name + " " + item.option.name + ", ";
        }
        holder.orderName.setText(Utils.truncateText(name));
        holder.orderStatus.setText(orders.get(position).status.statusName);
        holder.orderTime.setText(Utils.formatTime(orders.get(position).createAt));
        holder.price.setText(Utils.formatPrice(orders.get(position).price));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderStatus, orderTime, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.OrderPreviewItemLayout_Name);
            orderStatus = itemView.findViewById(R.id.OrderPreviewItemLayout_Status);
            orderTime = itemView.findViewById(R.id.OrderPreviewItemLayout_Time);
            price = itemView.findViewById(R.id.OrderPreviewItemLayout_Price);
        }
    }
}
