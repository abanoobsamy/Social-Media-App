package com.abanoob_samy.socialmediaapp.pojo;

import android.net.Uri;

public class GalleryImagesModel {

    public Uri imageUri;

    public GalleryImagesModel() {
    }

    public GalleryImagesModel(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
