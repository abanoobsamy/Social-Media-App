package com.abanoob_samy.socialmediaapp.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.FragmentProfileBinding;
import com.abanoob_samy.socialmediaapp.databinding.PostImageItemBinding;
import com.abanoob_samy.socialmediaapp.pojo.PostImageModel;
import com.abanoob_samy.socialmediaapp.utils.Constants;
import com.abanoob_samy.socialmediaapp.utils.StringManipulation;
import com.abanoob_samy.socialmediaapp.view.activity.HomeActivity;
import com.abanoob_samy.socialmediaapp.view.activity.ViewChatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marsad.stylishdialogs.StylishAlertDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirestoreRecyclerAdapter mFirestoreRecyclerAdapter;
    private DocumentReference mDocumentReference, myRef;

    private boolean isMyProfile = true;

    private String uid;

    private List<Object> followersList, followingList, followingList_2;

    private boolean isFollowing;
    private int count;

    public static String PROFILE_PICTURE;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getActivity() != null;

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        myRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(mUser.getUid());

        if (HomeActivity.IS_SEARCHED_USER) {

            //if user from search
            isMyProfile = false;
            uid = HomeActivity.USER_ID;

            loadData();
        } else {
            //if current user
            isMyProfile = true;
            uid = mUser.getUid();
        }

        if (isMyProfile) {
            binding.btnFollow.setVisibility(View.GONE);
            binding.btnStartChat.setVisibility(View.GONE);
            binding.ivEditBtn.setVisibility(View.VISIBLE);
            binding.containerFollowers.setVisibility(View.VISIBLE);
        } else {
            binding.btnFollow.setVisibility(View.VISIBLE);
            binding.btnStartChat.setVisibility(View.VISIBLE);
            binding.ivEditBtn.setVisibility(View.GONE);
//            binding.containerFollowers.setVisibility(View.GONE);
        }

        mDocumentReference = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid);

        loadBasicData();

        binding.ivEditBtn.setOnClickListener(view1 -> {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), ProfileFragment.this);

        });

        setUpFollow();

        binding.btnStartChat.setOnClickListener(view1 -> {

            queryChat();
        });
    }

    private void queryChat() {

        StylishAlertDialog stylishAlertDialog = new StylishAlertDialog(getContext(),
                StylishAlertDialog.PROGRESS);
        stylishAlertDialog.setTitleText("Starting Chat...")
                .setCancelable(false);
        stylishAlertDialog.show();

//        List<String> list = new ArrayList<>();
//        list.add(mUser.getUid());
//        list.add(uid);

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        reference.whereArrayContains("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            QuerySnapshot snapshot = task.getResult();

                            if (snapshot.isEmpty()) {

                                startNewChat(stylishAlertDialog);
                            } else {

                                //get chatId and pass
                                for (DocumentSnapshot snapshotChat : snapshot) {

                                    //Because we don't need to send to the same user with a new ID
                                    Intent intent = new Intent(getActivity(), ViewChatActivity.class);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("id", snapshotChat.getId());
                                    startActivity(intent);
                                }
                            }
                        } else {
                            stylishAlertDialog.dismissWithAnimation();
                        }
                    }
                });
    }


    private void startNewChat(StylishAlertDialog stylishAlertDialog) {

        CollectionReference messagesReference = FirebaseFirestore.getInstance()
                .collection("Messages");

        List<String> listUid = new ArrayList<>();

        listUid.add(0, mUser.getUid());
        listUid.add(1, uid);

        String messageId = messagesReference.document().getId();

        Map<String, Object> messagesMap = new HashMap<>();
        messagesMap.put("id", messageId);
        messagesMap.put("uid", listUid);
        messagesMap.put("lastMessage", "Hi");
        messagesMap.put("timeStamp", FieldValue.serverTimestamp());

        //update because this will modify everytime to get the last thing if he has already send it,
        // if else which mean first message == set.
        messagesReference.document(messageId)
                .update(messagesMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                        } else {
                            messagesReference.document(messageId)
                                    .set(messagesMap);
                        }
                    }
                });

        CollectionReference chatsReference = FirebaseFirestore.getInstance()
                .collection("Messages")
                .document(messageId)
                .collection("Chats");

        String chatId = chatsReference.document().getId();

        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put("id", chatId);
        chatMap.put("senderId", mUser.getUid());
        chatMap.put("message", "Hi");
        chatMap.put("timeStamp", FieldValue.serverTimestamp());

        //set because this will send everytime a new message or chat in chat.
        chatsReference.document(chatId)
                .set(chatMap);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                stylishAlertDialog.dismissWithAnimation();

                Intent intent = new Intent(getActivity(), ViewChatActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("id", messageId);
                startActivity(intent);
            }
        }, 3000);
    }

    private void setUpFollow() {

        binding.btnFollow.setOnClickListener(view1 -> {

            if (isFollowing) {

                followersList.remove(mUser.getUid());

                followingList_2.remove(uid);

                Map<String, Object> map2 = new HashMap<>();
                map2.put("following", followingList_2);

                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);

                mDocumentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            binding.btnFollow.setText("Follow");

                            createNotification(" has unfollowed you.");

                            myRef.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Unfollow Successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("TAG", "onComplete: " + task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.e("TAG", "onComplete: " + task.getException().getMessage());
                        }
                    }
                });

            }
            else {

                followersList.add(mUser.getUid());

                followingList_2.add(uid);

                Map<String, Object> map2 = new HashMap<>();
                map2.put("following", followingList_2);

                Map<String, Object> map = new HashMap<>();
                map.put("followers", followersList);

                mDocumentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            binding.btnFollow.setText("Unfollow");

                            createNotification(" follow you.");

                            myRef.update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Follow Successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("TAG", "onComplete: " + task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.e("TAG", "onComplete: " + task.getException().getMessage());
                        }
                    }
                });

            }
        });
    }

    private void loadData() {

        myRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.e("TAG", "onEvent: " + error.getMessage());
                    return;
                }

                if (value == null || !value.exists()) {
                    return;
                }

                followingList_2 = (List<Object>) value.get("following");

            }
        });
    }

    private void loadBasicData() {

        //to retrieve data from firestore.

        mDocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null)
                    return;

                assert value != null;

                if (value.exists()) {

                    String fullName = value.getString("fullName");
                    String username = value.getString("username");
                    String status = value.getString("status");
                    PROFILE_PICTURE = value.getString("profilePicture");

//                    int followers = value.getLong("followers").intValue();
//                    int following = value.getLong("following").intValue();
//
                    followersList = (List<Object>) value.get("followers");
                    followingList = (List<Object>) value.get("following");

                    binding.tvFullName.setText(fullName);
                    binding.tvUserName.setText(StringManipulation.condenseUsername(username)
                            .toLowerCase(Locale.ROOT));
                    binding.tvStatus.setText(status);

                    binding.tvFollowersNumberProfile.setText(String.valueOf(followersList.size()));
                    binding.tvFollowingNumberProfile.setText(String.valueOf(followingList.size()));

                    if (followersList.contains(mUser.getUid())) {

                        binding.btnFollow.setText("Unfollow");
                        isFollowing = true;
                        binding.btnStartChat.setVisibility(View.VISIBLE);

                    } else {
                        binding.btnFollow.setText("Follow");
                        isFollowing = false;
                        binding.btnStartChat.setVisibility(View.GONE);
                    }

                    try {

                        Glide.with(getContext())
                                .load(PROFILE_PICTURE)
                                .circleCrop()
                                .placeholder(R.drawable.ic_person)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                        Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                                        storeProfileImage(bitmap, PROFILE_PICTURE);
                                        return false;
                                    }
                                })
                                .timeout(6500)
                                .into(binding.profileImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        loadPostImage();
    }

    private void storeProfileImage(Bitmap bitmap, String url) {

        SharedPreferences preferences = getActivity()
                .getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        boolean isStored = preferences.getBoolean(Constants.PREF_STORED, false);
        String urlString = preferences.getString(Constants.PREF_URL, "");

        SharedPreferences.Editor editor = preferences.edit();

        if (isStored && urlString.equals(url))
            return;

        if (HomeActivity.IS_SEARCHED_USER)
            return;

        ContextWrapper contextWrapper = new ContextWrapper(getContext().getApplicationContext());

        File directory = contextWrapper.getDir("image_data", Context.MODE_PRIVATE);

        if (!directory.exists())
            directory.mkdirs();

        File path = new File(directory, "profile.png");

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(path);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        editor.putBoolean(Constants.PREF_STORED, true);
        editor.putString(Constants.PREF_URL, url);
        editor.putString(Constants.PREF_DIRECTORY, directory.getAbsolutePath());
        editor.apply();

    }

    private void loadPostImage() {

        //to add data to firestore
        DocumentReference mReference = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid);

        Query query = mReference.collection("Post Images");

        FirestoreRecyclerOptions<PostImageModel> options = new FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel.class)
                .build();

        mFirestoreRecyclerAdapter = new FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            @NonNull
            @Override
            public PostImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PostImageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_image_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull PostImageHolder holder, int position, @NonNull PostImageModel model) {

                Random random = new Random();

                int color = Color.argb(255, random.nextInt(256),
                        random.nextInt(256), random.nextInt(256));

                Glide.with(getContext())
                        .load(model.getPostImage())
                        .placeholder(new ColorDrawable(color))
                        .timeout(6500)
                        .into(holder.binding.ivPostImage);

                count = getItemCount();

                binding.tvPostsNumberProfile.setText("" + count);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };

        binding.recyclerViewProfile.setHasFixedSize(true);
        binding.recyclerViewProfile.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.recyclerViewProfile.setAdapter(mFirestoreRecyclerAdapter);
        mFirestoreRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirestoreRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirestoreRecyclerAdapter.stopListening();
    }

    private class PostImageHolder extends RecyclerView.ViewHolder {

        private PostImageItemBinding binding;

        public PostImageHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostImageItemBinding.bind(itemView);
        }

    }

    private void uploadImage(Uri uri) {

        StorageReference reference = FirebaseStorage.getInstance().getReference("Profile Images")
                .child("profile_images" + System.currentTimeMillis());

        reference.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String imageUrl = uri.toString();

                                            UserProfileChangeRequest.Builder reqBuilder =
                                                    new UserProfileChangeRequest.Builder();

                                            reqBuilder.setPhotoUri(uri);
                                            mUser.updateProfile(reqBuilder.build());

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("profilePicture", imageUrl);

                                            FirebaseFirestore.getInstance()
                                                    .collection("Users")
                                                    .document(mUser.getUid())
                                                    .update(map)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                Toast.makeText(getContext(),
                                                                        "Updated Successfully.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(getContext(),
                                                                        "Failed updated!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNotification(String isFollow) {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");

        String id = reference.document().getId();

        Map<String, Object> map = new HashMap<>();

        map.put("id", id);
        map.put("uid", uid);
        map.put("timeStamp", FieldValue.serverTimestamp());
        map.put("notification", mUser.getDisplayName() + isFollow);

        reference.document(id).set(map);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            // There are no request codes

            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);

            Uri uri = activityResult.getUri();

            uploadImage(uri);
        }
    }
}