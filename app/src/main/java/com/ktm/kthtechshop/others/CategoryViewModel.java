package com.ktm.kthtechshop.others;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ktm.kthtechshop.api.ApiServices;
import com.ktm.kthtechshop.dto.CategoryItemViewModel;

import java.util.ArrayList;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<ArrayList<CategoryItemViewModel>> categories;
    ApiServices apiServices;

    public CategoryViewModel() {
        super();
        categories = new MutableLiveData<>();

    }


    public LiveData<ArrayList<CategoryItemViewModel>> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<CategoryItemViewModel> categories) {
        this.categories.setValue(categories);
    }
}
