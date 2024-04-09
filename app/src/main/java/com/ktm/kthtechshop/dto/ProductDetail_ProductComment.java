package com.ktm.kthtechshop.dto;

import com.google.gson.annotations.SerializedName;

public class ProductDetail_ProductComment {
    public int id;
    public String content;
    public double rating;
    public ProductDetail_Comment_User user;
    @SerializedName("seller_reply")
    public String sellerReply;
    public String createAt;
    public ProductDetail_Comment_Image[] image;

    public ProductDetail_ProductComment(int id, String content, double rating, ProductDetail_Comment_User user, String sellerReply, String createAt, ProductDetail_Comment_Image[] image) {
        this.id = id;
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.sellerReply = sellerReply;
        this.createAt = createAt;
        this.image = image;
    }
}
