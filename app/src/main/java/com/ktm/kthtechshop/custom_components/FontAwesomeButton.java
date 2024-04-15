package com.ktm.kthtechshop.custom_components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FontAwesomeButton extends androidx.appcompat.widget.AppCompatButton {


    public FontAwesomeButton(@NonNull Context context) {
        super(context);
        init();
    }

    public FontAwesomeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontAwesomeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        //Font name should not contain "/".
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fa-solid-900.ttf");
        setTypeface(tf);
    }

}
