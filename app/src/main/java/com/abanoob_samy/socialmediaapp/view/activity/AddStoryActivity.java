package com.abanoob_samy.socialmediaapp.view.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.databinding.ActivityAddStoryBinding;
import com.abanoob_samy.socialmediaapp.utils.StringManipulation;
import com.abanoob_samy.socialmediaapp.view.fragment.ProfileFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.LogMessage;
import com.gowtham.library.utils.TrimType;
import com.gowtham.library.utils.TrimVideo;
import com.marsad.stylishdialogs.StylishAlertDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddStoryActivity extends AppCompatActivity {

    private static final int SELECT_VIDEO = 101;

    private ActivityAddStoryBinding binding;

    private FirebaseUser mUser;

    private StylishAlertDialog mStylishAlertDialog;

    private String profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        fetchDataUserID();

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/* video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, "image/* video/*");
        startActivityForResult(intent, SELECT_VIDEO);
    }

    private void uploadFileToStorage(Uri uri, String type) {

        mStylishAlertDialog = new StylishAlertDialog(this, StylishAlertDialog.PROGRESS);
        mStylishAlertDialog.setTitleText("Uploading...")
                .setCancelable(false);
        mStylishAlertDialog.show();

        String fileName;

        if (type.contains("image")) {

            fileName = System.currentTimeMillis() + ".png";
            uploadImageToStorage(fileName, uri, type);

        }
        else {

            fileName = System.currentTimeMillis() + ".mp4";
            uploadVideoToStorage(fileName, uri, type);
        }

    }

    private void uploadImageToStorage(String fileName, Uri uri, String type) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Stories")
                .child("stories_" + fileName);

        storageReference.putFile(uri).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                assert task.getResult() != null;
                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
                            uploadVideoDataToFirestore(String.valueOf(uri1), type);
                        }
                );

            }
            else {
                mStylishAlertDialog.dismissWithAnimation();
                assert task.getException() != null;
                String error = task.getException().getMessage();
                Toast.makeText(AddStoryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void fetchDataUserID() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot snapshot = task.getResult();

                            profilePicture = snapshot.getString("profilePicture");

                        }
                        else {
                            assert task.getException() != null;

                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void uploadVideoToStorage(String fileName, Uri uri, String type) {

        File file = new File(uri.getPath());

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("Stories")
                .child("stories_" + fileName);

        storageReference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {

                    assert task.getResult() != null;

                    task.getResult().getStorage()
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    uploadVideoDataToFirestore(String.valueOf(uri), type);
                                }
                            });
                }
                else {

                    assert task.getException() != null;

                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

                mStylishAlertDialog.dismissWithAnimation();
            }
        });

    }

    private void uploadVideoDataToFirestore(String url, String type) {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Stories");

        String id = reference.document().getId();

        Map<String, Object> map = new HashMap<>();

        map.put("url", url);
        map.put("id", id);
        map.put("uid", mUser.getUid());
        map.put("type", type);
        map.put("profilePicture", profilePicture);
        map.put("username", StringManipulation.condenseUsername(mUser.getDisplayName())
                .toLowerCase(Locale.ROOT));

        reference.document(id)
                .set(map);

//        mStylishAlertDialog.dismissWithAnimation();

        finish();

    }

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));

                    binding.videoView.setVideoURI(uri);
                    binding.videoView.start();

                    binding.ivSendBtn.setVisibility(View.VISIBLE);

                    binding.ivSendBtn.setOnClickListener(view -> {

                        binding.ivSendBtn.setVisibility(View.GONE);
                        binding.videoView.pause();
                        uploadFileToStorage(uri, "video");
                    });


                    Log.d("TAG", "Trimmed path:: " + uri);

                }
                else {
                    LogMessage.v("videoTrimResultLauncher data is null");

                    Toast.makeText(getApplicationContext(), "Data is null!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_VIDEO && resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();

            if (uri.toString().contains("image")) {

                //selected Image
                binding.videoView.setVisibility(View.GONE);
                binding.ivImageStory.setVisibility(View.VISIBLE);

                Glide.with(this)
                        .load(uri)
                        .into(binding.ivImageStory);


                binding.ivSendBtn.setVisibility(View.VISIBLE);

                binding.ivSendBtn.setOnClickListener(view -> {

                    binding.ivSendBtn.setVisibility(View.GONE);

                    uploadFileToStorage(uri, "image");
                });
            }
            else if (uri.toString().contains("video")) {

                TrimVideo.activity(String.valueOf(uri))
                        .setCompressOption(new CompressOption())
                        .setTrimType(TrimType.MIN_MAX_DURATION)
                        .setMinToMax(5, 30)
//                    .setFixedDuration(30)
                        .setHideSeekBar(true)
                        .start(this, startForResult);
            }

        }
    }
}