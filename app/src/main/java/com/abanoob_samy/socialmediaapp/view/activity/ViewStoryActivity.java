package com.abanoob_samy.socialmediaapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.abanoob_samy.socialmediaapp.databinding.ActivityViewStoryBinding;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

public class ViewStoryActivity extends AppCompatActivity {

    public static final String URL_KEY = "url";
    public static final String PATH_NAME_KEY = "pathName";
    public static final String FILE_TYPE = "fileType";
    public static final String POSITION = "position";

    private ActivityViewStoryBinding binding;
    private int progressStatus = 0;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String url = getIntent().getStringExtra(URL_KEY);
//        String videoName = getIntent().getStringExtra(PATH_NAME_KEY);
        String type = getIntent().getStringExtra(FILE_TYPE);

        if (url == null || url.isEmpty()) {
            finish();
        }

        if (type.contains("image")) {

            binding.imageView.setVisibility(View.VISIBLE);
            binding.videoView.setVisibility(View.GONE);

            Glide.with(this)
                    .load(url)
                    .into(binding.imageView);

            binding.ivCloseBtn.setOnClickListener(view -> finish());

            new Thread(new Runnable() {
                public void run() {
                    while (progressStatus < 100) {
                        progressStatus += 1;
                        // Update the progress bar and display the
                        //current value in the text view
                        handler.post(new Runnable() {
                            public void run() {
                                binding.progressBar2.setProgress(progressStatus);
                            }
                        });
                        try {
                            // Sleep for 200 milliseconds.
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    finish();
                }
            }, 10000);
        }
        else {
            //video selected
            binding.imageView.setVisibility(View.GONE);
            binding.videoView.setVisibility(View.VISIBLE);
            // Build the media item.
            MediaItem mediaItem = MediaItem.fromUri(url);

            SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
            player.setMediaItem(mediaItem);

            // Prepare the player.
            player.prepare();

            binding.videoView.setPlayer(player);
            player.play();

            binding.ivCloseBtn.setOnClickListener(view -> finish());

        }


    }

//    private void uploadVideoToStorage(String videoUrl, String videoName) {
//
//        StorageReference storageReference = FirebaseStorage.getInstance()
//                .getReference("Stories")
//                .child("stories_" + videoName);
//
//        try {
//
//            File localFile = File.createTempFile("test", ".mp4");
//
//            storageReference.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                            binding.videoView.setVideoPath(localFile.getPath());
//                            binding.videoView.start();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}