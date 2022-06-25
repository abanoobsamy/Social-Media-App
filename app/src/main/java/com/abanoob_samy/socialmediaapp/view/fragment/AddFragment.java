package com.abanoob_samy.socialmediaapp.view.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.adapter.GalleryAdapter;
import com.abanoob_samy.socialmediaapp.databinding.FragmentAddBinding;
import com.abanoob_samy.socialmediaapp.pojo.GalleryImagesModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;

    private GalleryAdapter adapter;
    private List<GalleryImagesModel> galleryImagesModels;

    private FirebaseUser mUser;

    private Uri mImageUri;

    private Dialog dialog;

    private String description, profilePicture;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAddBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        fetchDataUserID();

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                R.drawable.dialog_bg, null));
        dialog.setCancelable(false);

        binding.ivBackBtn.setOnClickListener(view1 -> {

//            getActivity().recreate();
        });

        setUpRecyclerPosts();

        adapter.sendImage(new GalleryAdapter.OnClickSendImage() {
            @Override
            public void onSend(Uri imageUri) {

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(4, 3)
                        .start(getContext(), AddFragment.this);

            }
        });

        binding.ivNextBtn.setOnClickListener(view1 -> {

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageReference = storage.getReference("Post Images").
                    child("post_images" + System.currentTimeMillis());

            description = binding.etAddPostDesc.getText().toString();

            if (description.isEmpty() && description.equals("")) {
                Toast.makeText(getContext(), "Please write a post!", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.show();

            storageReference.putFile(mImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        uploadData(uri.toString());
                                    }
                                });
                            }
                            else {

                                Toast.makeText(getContext(), "Failed to upload post!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
        });
    }

    private void uploadData(String imageUrl) {

        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(mUser.getUid())
                .collection("Post Images");

        String id = reference.document().getId();

        Map<String, Object> map = new HashMap<>();

        List<String> likes = new ArrayList<>();

        map.put("id", id);
        map.put("postImage", imageUrl);
        map.put("description", description);
        map.put("timeStamp", FieldValue.serverTimestamp());
        map.put("username", mUser.getDisplayName());
        map.put("profilePicture", profilePicture);
        map.put("likes", likes);
//        map.put("comments", "");
        map.put("uid", mUser.getUid());

        reference.document(id).set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            System.out.println();
                            Toast.makeText(getContext(), "Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Failed Uploaded! ", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
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

                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUpRecyclerPosts() {

        galleryImagesModels = new ArrayList<>();
        adapter = new GalleryAdapter(getContext(), galleryImagesModels);

        binding.recyclerViewAddPost.setHasFixedSize(true);
        binding.recyclerViewAddPost.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.recyclerViewAddPost.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Dexter.withContext(getContext())
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                    File file = new File(Environment.getExternalStorageDirectory()
                                            .toString() + "/Download");

                                    if (file.exists()) {

                                        File[] files = file.listFiles();
                                        assert files != null;

                                        galleryImagesModels.clear();

                                        for (File file1 : files) {

                                            if (file1.getAbsolutePath().endsWith(".jpg") ||
                                                    file1.getAbsolutePath().endsWith(".png")) {

                                                galleryImagesModels.add(new GalleryImagesModel(Uri.fromFile(file1)));
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        // There are no request codes
                        Intent data = result.getData();

                        CropImage.ActivityResult resultCode = CropImage.getActivityResult(data);

                        if (result.getResultCode() == RESULT_OK) {
                            // There are no request codes

                            assert result != null;

                            mImageUri = resultCode.getUri();

                            Glide.with(getContext())
                                    .load(mImageUri)
                                    .into(binding.ivAddPostImage);

                            binding.ivAddPostImage.setVisibility(View.VISIBLE);
                            binding.ivNextBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                assert result != null;
                mImageUri = result.getUri();

                Glide.with(getContext())
                        .load(mImageUri)
                        .into(binding.ivAddPostImage);

                binding.ivAddPostImage.setVisibility(View.VISIBLE);
                binding.ivNextBtn.setVisibility(View.VISIBLE);

            }

        }
    }

}