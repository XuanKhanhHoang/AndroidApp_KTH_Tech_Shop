package com.ktm.kthtechshop.dto;

public class CategoryItem {
    private String name, icon, id;

    public CategoryItem(String name, String icon, String id) {
        this.name = name;
        this.icon = icon;
        this.id = id;
    }

    public CategoryItem(String icon, String id) {
        this.icon = icon;
        this.id = id;
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }
}
