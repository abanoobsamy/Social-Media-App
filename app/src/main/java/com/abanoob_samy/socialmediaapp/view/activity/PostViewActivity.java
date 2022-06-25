package com.abanoob_samy.socialmediaapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.ActivityPostViewBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.CountedCompleter;

public class PostViewActivity extends AppCompatActivity {

    private ActivityPostViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        String action = intent.getAction();

        Uri uri = intent.getData();

//        String scheme = uri.getScheme();
//        String host = uri.getHost();
//        String path = uri.getPath();
//        String query = uri.getQuery();

//            URL url = new URL(scheme + "://" + host + path.replace("Post Images", "Post%20Images") + "?" + query);

        FirebaseStorage.getInstance().getReference()
                .child(uri.getLastPathSegment())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(PostViewActivity.this)
                                .load(uri.toString())
                                .timeout(6500)
                                .into(binding.ivPostImage);

                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            startActivity(new Intent(PostViewActivity.this, HomeActivity.class));
        else
            startActivity(new Intent(PostViewActivity.this, FragmentReplacerActivity.class));

    }
}