package com.abanoob_samy.socialmediaapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.PostImageItemBinding;
import com.abanoob_samy.socialmediaapp.pojo.GalleryImagesModel;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryHolder> {

    private Context context;
    private List<GalleryImagesModel> galleryImagesModels;
    private OnClickSendImage onClickSendImage;

    public GalleryAdapter(Context context, List<GalleryImagesModel> galleryImagesModels) {
        this.context = context;
        this.galleryImagesModels = galleryImagesModels;
    }

    @NonNull
    @Override
    public GalleryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryHolder(LayoutInflater.from(context)
                .inflate(R.layout.post_image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryHolder holder, int position) {

        GalleryImagesModel galleryImagesModel = galleryImagesModels.get(position);

        Glide.with(context)
                .load(galleryImagesModel.getImageUri())
                .into(holder.binding.ivPostImage);


        holder.binding.ivPostImage.setOnClickListener(view -> {

            chooseImage(galleryImagesModel.getImageUri());
        });
    }

    private void chooseImage(Uri imageUri) {
        onClickSendImage.onSend(imageUri);
    }

    @Override
    public int getItemCount() {

        if (galleryImagesModels != null) {
            return galleryImagesModels.size();
        }
        else
            return 0;
    }

    public class GalleryHolder extends RecyclerView.ViewHolder {

        private PostImageItemBinding binding;

        public GalleryHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostImageItemBinding.bind(itemView);
        }
    }

    public interface OnClickSendImage {
        void onSend(Uri imageUri);
    }

    public void sendImage(OnClickSendImage onClickSendImage) {
        this.onClickSendImage = onClickSendImage;
    }

}
