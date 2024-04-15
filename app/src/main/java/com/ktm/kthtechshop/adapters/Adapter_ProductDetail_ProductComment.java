package com.ktm.kthtechshop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ktm.kthtechshop.R;
import com.ktm.kthtechshop.dto.ProductDetail_ProductComment;
import com.ktm.kthtechshop.listeners.ProductDetail_Comment_ImageClickListener;
import com.ktm.kthtechshop.utils.Utils;

import java.util.ArrayList;

public class Adapter_ProductDetail_ProductComment extends RecyclerView.Adapter<Adapter_ProductDetail_ProductComment.ViewHolder> {

    Context context;
    ArrayList<ProductDetail_ProductComment> commentArrayList;
    int curPosCommentImageShow = -1;

    public Adapter_ProductDetail_ProductComment(Context context, ArrayList<ProductDetail_ProductComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.product_detail_comment_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetail_ProductComment item = commentArrayList.get(position);
        holder.userName.setText(item.user.firstName);
        holder.userCommentTime.setText(Utils.formatTime(item.createAt));
        holder.userCommentContent.setText(item.content);
//        holder.sellerReplyTime.setText(item.sellerReply);
        holder.sellerReplyContent.setText(item.sellerReply);
        if (item.image.length == 0)
            holder.userCommentImageRclView.setVisibility(View.GONE);
        else {
            holder.userCommentImageRclView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.userCommentImageRclView.setAdapter(new Adapter_ProductDetail_CommentItem_Image(item.image, new ProductDetail_Comment_ImageClickListener() {
                @Override
                public void onImageCLick(Bitmap bitmap) {
                    if (bitmap == null) holder.userCommentImageFocus.setVisibility(View.GONE);
                    else {
                        holder.userCommentImageFocus.setVisibility(View.VISIBLE);
                        holder.userCommentImageFocus.setImageBitmap(bitmap);
                    }
                }
            }));
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar, userCommentImageFocus;
        TextView userName, userCommentTime, userCommentContent, sellerReplyTime, sellerReplyContent;
        RecyclerView userCommentImageRclView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_UserAvatar);
            userCommentImageFocus = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_ImageIsZoom);
            userName = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_UserName);
            userCommentTime = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_UserCommentTime);
            userCommentContent = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_UserComment);
            sellerReplyTime = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_SellerReplyTime);
            sellerReplyContent = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_SellerReplyContent);
            userCommentImageRclView = itemView.findViewById(R.id.ProductDetail_CommentItemLayout_Image_RclView);
        }
    }
}

