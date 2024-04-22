package com.ktm.kthtechshop.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ktm.kthtechshop.dto.CategoryItem;

import java.util.ArrayList;

public class Adapter_Spinner_FilterCategoryItem extends ArrayAdapter<CategoryItem> {

    private ArrayList<CategoryItem> categories;
    private Context context;

    public Adapter_Spinner_FilterCategoryItem(Context context, int textViewResourceId,
                                              ArrayList<CategoryItem> categories) {
        super(context, textViewResourceId, categories);
        this.context = context;
        this.categories = categories;

    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public CategoryItem getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(categories.get(position).getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(categories.get(position).getName());

        return label;
    }

}

