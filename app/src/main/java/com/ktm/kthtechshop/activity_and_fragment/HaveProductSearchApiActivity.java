package com.ktm.kthtechshop.activity_and_fragment;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.ktm.kthtechshop.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HaveProductSearchApiActivity extends NeededCallApiActivity {
    protected EditText searchEditText;


    protected void RefSearchEditText(Integer id) {
        try {
            searchEditText = findViewById(id);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void RefSearchEditText() {
        try {
            searchEditText = findViewById(R.id.Global_ProductSearch_EditText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setUpSearchFunction() {
        if (searchEditText == null) {
            RefSearchEditText();
            if (searchEditText == null) return;
        }
        searchEditText.setSingleLine();
        searchEditText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                Intent it = new Intent(v.getContext(), ProductListActivity.class);
                Map<String, String> mp = new HashMap<String, String>();
                String searchTerm = searchEditText.getText().toString();
                if (!searchTerm.isEmpty()) mp.put("keyword", searchTerm);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra("queryParams", (Serializable) mp);
                v.getContext().startActivity(it);
                finish();
                return true;
            }
            return false;
        });
    }
}
