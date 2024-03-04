package com.ktm.kthtechshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.ImageLoadFromURL;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.activity.ProductListActivity;
import com.ktm.kthtechshop.dto.CategoryItem;
import com.ktm.kthtechshop.localhostIp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_CategoryRclView extends RecyclerView.Adapter<ViewHolder_CategoryRclView> {

    Context context;
    ArrayList<CategoryItem> categoryItemArrayList;

    public Adapter_CategoryRclView(Context context, ArrayList<CategoryItem> categoryItemArrayList) {
        this.context = context;
        this.categoryItemArrayList = categoryItemArrayList;
    }


    @NonNull
    @Override
    public ViewHolder_CategoryRclView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_CategoryRclView(LayoutInflater.from(context).inflate(R.layout.hompage_category_recycleitem, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_CategoryRclView holder, int position) {
        CategoryItem ct = categoryItemArrayList.get(position);
        holder.categoryName.setText(ct.getName());
        new ImageLoadFromURL(localhostIp.LOCALHOST_IP.getValue() + ":3000" + ct.getIcon(), holder.categoryLogo).execute();
        holder.categoryLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(v.getContext(), ProductListActivity.class);
                Map<String, String> mp = new HashMap<String, String>();
                mp.put("category_id", ct.getId());
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("queryParams", (Serializable) mp);
                v.getContext().startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItemArrayList.size();
    }

}

class ViewHolder_CategoryRclView extends RecyclerView.ViewHolder {
    ImageView categoryLogo;
    TextView categoryName;

    public ViewHolder_CategoryRclView(@NonNull View itemView) {
        super(itemView);
        categoryLogo = itemView.findViewById(R.id.categoryLogo);
        categoryName = itemView.findViewById(R.id.categoryName);
    }
}