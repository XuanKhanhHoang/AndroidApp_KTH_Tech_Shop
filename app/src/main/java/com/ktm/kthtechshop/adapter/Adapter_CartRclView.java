package com.ktm.kthtechshop.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ktm.kthtechshop.AppSharedPreferences;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.CartItem;
import com.ktm.kthtechshop.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Adapter_CartRclView extends RecyclerView.Adapter<Adapter_CartRclView.ViewHolder_CartRclView> {

    Context context;
    ArrayList<CartItem> cartArrayList;
    boolean isFilterAmountInput = false;

    public Adapter_CartRclView(Context context, ArrayList<CartItem> cartArrayList) {
        this.context = context;
        this.cartArrayList = cartArrayList;
    }


    @NonNull
    @Override
    public ViewHolder_CartRclView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder_CartRclView(LayoutInflater.from(context).inflate(R.layout.cart_cart_item_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_CartRclView holder, int position) {
        CartItem item = cartArrayList.get(position);
        holder.productName.setText(item.option.products.name);
        holder.optionName.setText(item.option.name);
        holder.image.setImageResource(R.drawable.s23_xang);
//        new ImageLoadFromURL(localhostIp.LOCALHOST_IP.getValue() + ":3000" + item.option.image, holder.image).execute();
        holder.amountEdiTxt.setText(String.valueOf(item.amount));
        holder.originalPrice.setText(Utils.formatPrice(item.option.originalPrice));
        holder.sellingPrice.setText(Utils.formatPrice(item.option.sellingPrice));
        holder.originalPrice.setPaintFlags(holder.amountEdiTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (position == cartArrayList.size()) isFilterAmountInput = true;

    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    class ViewHolder_CartRclView extends RecyclerView.ViewHolder {
        ImageView image;
        TextView productName, optionName, sellingPrice, originalPrice;
        CheckBox checkBox;
        EditText amountEdiTxt;
        Button decreaseAmountBtn, increaseAmountBtn;
        AppSharedPreferences appSharedPreferences;
        Gson gson;

        public ViewHolder_CartRclView(@NonNull View itemView) {
            super(itemView);
            appSharedPreferences = new AppSharedPreferences((AppCompatActivity) itemView.getContext());
            image = itemView.findViewById(R.id.CartItemLayout_Image);
            productName = itemView.findViewById(R.id.CartItemLayout_ProductName);
            optionName = itemView.findViewById(R.id.CartItemLayout_ProductOptionName);
            sellingPrice = itemView.findViewById(R.id.CartItemLayout_SellingPrice);
            originalPrice = itemView.findViewById(R.id.CartItemLayout_OriginalPrice);
            checkBox = itemView.findViewById(R.id.CartItemLayout_CheckBox);
            amountEdiTxt = itemView.findViewById(R.id.CartItemLayout_AmountInp);
            decreaseAmountBtn = itemView.findViewById(R.id.CartItemLayout_DecreaseAmount);
            increaseAmountBtn = itemView.findViewById(R.id.CartItemLayout_IncreaseAmount);
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

            decreaseAmountBtn.setOnClickListener(v -> {
                int selectedItem = getAdapterPosition();
                CartItem item = cartArrayList.get(selectedItem);
                if (item.amount <= 1) return;
                item.amount--;
                cartArrayList.set(selectedItem, item);
                String cartJson = appSharedPreferences.getCart();
                if (cartJson != null && !cartJson.isEmpty()) {
                    Type type = new TypeToken<ArrayList<CartItem>>() {
                    }.getType();
                    ArrayList<CartItem> cart = gson.fromJson(cartJson, type);

                    for (int i = 0; i < cart.size(); i++) {

                        if (item.cartId == cart.get(i).cartId) {
                            cart.set(i, item);
                            appSharedPreferences.setCart(gson.toJson(cart));
                            amountEdiTxt.setText(String.valueOf(item.amount));
                            break;
                        }
                    }
                }
            });
            increaseAmountBtn.setOnClickListener(v -> {
                int selectedItem = getAdapterPosition();
                CartItem item = cartArrayList.get(selectedItem);
                if (item.amount >= item.option.amount) return;
                item.amount++;
                cartArrayList.set(selectedItem, item);
                String cartJson = appSharedPreferences.getCart();
                if (cartJson != null && !cartJson.isEmpty()) {
                    Type type = new TypeToken<ArrayList<CartItem>>() {
                    }.getType();
                    ArrayList<CartItem> cart = gson.fromJson(cartJson, type);

                    for (int i = 0; i < cart.size(); i++) {

                        if (item.cartId == cart.get(i).cartId) {
                            cart.set(i, item);
                            appSharedPreferences.setCart(gson.toJson(cart));
                            amountEdiTxt.setText(String.valueOf(item.amount));

                            break;
                        }
                    }
                }
            });
            InputFilter inputFilter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                    if (!isFilterAmountInput) {
                        //curAmount has just update before this
//                        notifyTotalCostChange();
                        return null;
                    }
                    try {
                        int selectedItem = getAdapterPosition();
                        int upperBound = cartArrayList.get(selectedItem).option.amount;
                        Integer input = Integer.parseInt(source.toString());
                        if (source.length() == 0) {
//                            curAmount = input;
//                            notifyTotalCostChange();
                            return null;
                        }
                        if (input >= 0) {
                            if (amountEdiTxt.getText().length() == 0 && input < upperBound && input > 0) {
//                                curAmount = input;
//                                notifyTotalCostChange();
                                return null;
                            }
                            try {
                                Integer nextVal = Integer.parseInt(amountEdiTxt.getText().toString() + input.toString());
                                if (nextVal > 0 && nextVal <= upperBound) {
//                                    curAmount = nextVal;
//                                    notifyTotalCostChange();
                                    return null;
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    } catch (NumberFormatException e) {
                    }
                    return "";
                }
            };
            amountEdiTxt.setFilters(new InputFilter[]{inputFilter});
        }

    }
}

