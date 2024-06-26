package com.ktm.kthtechshop.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.api.ApiServices;
import com.ktm.kthtechshop.dto.CartItem;
import com.ktm.kthtechshop.dto.CartItemSaved;
import com.ktm.kthtechshop.dto.DeleteCartItem;
import com.ktm.kthtechshop.listeners.Cart_CartItem_Checked_Listener;
import com.ktm.kthtechshop.localhostIp;
import com.ktm.kthtechshop.utils.AppSharedPreferences;
import com.ktm.kthtechshop.utils.ImageLoadFromURL;
import com.ktm.kthtechshop.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Adapter_CartRclView extends RecyclerView.Adapter<Adapter_CartRclView.ViewHolder_CartRclView> {

    Context context;
    ArrayList<CartItem> cartArrayList;
    boolean isFilterAmountInput = false;
    private Cart_CartItem_Checked_Listener listener;

    public Adapter_CartRclView(Context context, ArrayList<CartItem> cartArrayList, Cart_CartItem_Checked_Listener listener) {
        this.context = context;
        this.cartArrayList = cartArrayList;
        this.listener = listener;
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
        new ImageLoadFromURL(item.option.image, holder.image).execute();
        holder.amountEdiTxt.setText(String.valueOf(item.amount));
        if (item.option.discount != 0) {
            holder.originalPrice.setText(Utils.formatPrice(item.option.originalPrice));
            holder.originalPrice.setPaintFlags(holder.amountEdiTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.originalPrice.setVisibility(View.INVISIBLE);
        }
        holder.sellingPrice.setText(Utils.formatPrice(item.option.sellingPrice));

        if (position == cartArrayList.size()) isFilterAmountInput = true;

    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    class ViewHolder_CartRclView extends RecyclerView.ViewHolder {
        ImageView image;
        TextView productName, optionName, sellingPrice, originalPrice, deleteItem;
        CheckBox checkBox;
        EditText amountEdiTxt;
        Button decreaseAmountBtn, increaseAmountBtn;
        AppSharedPreferences appSharedPreferences;
        Gson gson;
        ApiServices apiServices;

        protected void initApiService() {
            gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(localhostIp.LOCALHOST_IP.getValue() + ":8081/api/v1/") //for ld
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            apiServices = retrofit.create(ApiServices.class);
        }

        public ViewHolder_CartRclView(@NonNull View itemView) {
            super(itemView);
            initApiService();
            appSharedPreferences = new AppSharedPreferences((AppCompatActivity) itemView.getContext());
            image = itemView.findViewById(R.id.CartItemLayout_Image);
            deleteItem = itemView.findViewById(R.id.CartItemLayout_DeleteItem);
            productName = itemView.findViewById(R.id.CartItemLayout_ProductName);
            optionName = itemView.findViewById(R.id.CartItemLayout_ProductOptionName);
            sellingPrice = itemView.findViewById(R.id.CartItemLayout_SellingPrice);
            originalPrice = itemView.findViewById(R.id.CartItemLayout_OriginalPrice);
            checkBox = itemView.findViewById(R.id.CartItemLayout_CheckBox);
            amountEdiTxt = itemView.findViewById(R.id.CartItemLayout_AmountInp);
            decreaseAmountBtn = itemView.findViewById(R.id.CartItemLayout_DecreaseAmount);
            increaseAmountBtn = itemView.findViewById(R.id.CartItemLayout_IncreaseAmount);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> listener.OnCartItemClick(getAdapterPosition()));

            decreaseAmountBtn.setOnClickListener(v -> {
                int selectedItem = getAdapterPosition();
                CartItem item = cartArrayList.get(selectedItem);
                if (item.amount <= 1) return;
                item.amount--;
                cartArrayList.set(selectedItem, item);
                String cartJson = appSharedPreferences.getCart();
                if (cartJson != null && !cartJson.isEmpty()) {
                    Type type = new TypeToken<ArrayList<CartItemSaved>>() {
                    }.getType();
                    ArrayList<CartItemSaved> cart = gson.fromJson(cartJson, type);

                    for (int i = 0; i < cart.size(); i++) {

                        if (item.cartId == cart.get(i).cartId) {
                            cart.set(i, new CartItemSaved(item.cartId, item.option.id, item.amount));
                            appSharedPreferences.setCart(gson.toJson(cart));
                            amountEdiTxt.setText(String.valueOf(item.amount));
                            listener.OnCartItemAmountChange(selectedItem, item.amount);
                            return;
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
                    Type type = new TypeToken<ArrayList<CartItemSaved>>() {
                    }.getType();
                    ArrayList<CartItemSaved> cart = gson.fromJson(cartJson, type);

                    for (int i = 0; i < cart.size(); i++) {

                        if (item.cartId == cart.get(i).cartId) {
                            cart.set(i, new CartItemSaved(item.cartId, item.option.id, item.amount));
                            appSharedPreferences.setCart(gson.toJson(cart));
                            amountEdiTxt.setText(String.valueOf(item.amount));
                            listener.OnCartItemAmountChange(selectedItem, item.amount);
                            return;
                        }
                    }
                }
            });
            InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {

                if (!isFilterAmountInput) {
                    //curAmount has just update before this
                    return null;
                }
                try {
                    int selectedItem = getAdapterPosition();
                    int upperBound = cartArrayList.get(selectedItem).option.amount;
                    Integer input = Integer.parseInt(source.toString());
                    if (source.length() == 0) {
                        return null;
                    }
                    if (input >= 0) {
                        if (amountEdiTxt.getText().length() == 0 && input < upperBound && input > 0) {
                            listener.OnCartItemAmountChange(selectedItem, input);
                            return null;
                        }
                        try {
                            Integer nextVal = Integer.parseInt(amountEdiTxt.getText().toString() + input.toString());
                            if (nextVal > 0 && nextVal <= upperBound) {
                                listener.OnCartItemAmountChange(selectedItem, nextVal);
                                return null;
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                } catch (NumberFormatException e) {
                }
                return "";
            };
            amountEdiTxt.setFilters(new InputFilter[]{inputFilter});

            deleteItem.setOnClickListener(v -> {
                int itemPos = getAdapterPosition();
                CartItem selectedItem = cartArrayList.get(itemPos);
                int cartId = selectedItem.cartId;
                apiServices.deleteOptionInCart(appSharedPreferences.getBearerAccessToken(), new DeleteCartItem(cartId)).enqueue(new Callback<DeleteCartItem>() {

                    @Override
                    public void onResponse(Call<DeleteCartItem> call, Response<DeleteCartItem> response) {
                        if (response.isSuccessful()) {
                            String cartJson = appSharedPreferences.getCart();
                            if (cartJson != null && !cartJson.isEmpty()) {
                                Type type = new TypeToken<ArrayList<CartItemSaved>>() {
                                }.getType();
                                ArrayList<CartItemSaved> cart = gson.fromJson(cartJson, type);

                                for (int i = 0; i < cart.size(); i++) {

                                    if (cartId == cart.get(i).cartId) {
                                        cart.remove(i);
                                        appSharedPreferences.setCart(gson.toJson(cart));
                                        cartArrayList.remove(itemPos);
                                        listener.OnCartItemDelete(cartId, selectedItem.option.sellingPrice);
                                        notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteCartItem> call, Throwable t) {
                        Toast.makeText(itemView.getContext(), "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

            });
        }

    }
}

