package com.abanoob_samy.socialmediaapp.listeners;

import android.widget.ImageView;
import android.widget.TextView;

import com.abanoob_samy.socialmediaapp.pojo.HomeModel;

import java.util.List;

public interface OnPressed {
    void onLiked(int position, String id, String uid, List<String> likes, ImageView ivLike, boolean liked);
    void setCommentCount(TextView tvCommentCount);
}
