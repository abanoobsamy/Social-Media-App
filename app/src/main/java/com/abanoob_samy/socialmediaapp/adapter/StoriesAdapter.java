package com.abanoob_samy.socialmediaapp.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.CommentItemBinding;
import com.abanoob_samy.socialmediaapp.databinding.StoriesItemBinding;
import com.abanoob_samy.socialmediaapp.pojo.CommentsModel;
import com.abanoob_samy.socialmediaapp.pojo.StoriesModel;
import com.abanoob_samy.socialmediaapp.view.activity.AddStoryActivity;
import com.abanoob_samy.socialmediaapp.view.activity.ViewStoryActivity;
import com.abanoob_samy.socialmediaapp.view.fragment.ProfileFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoriesHolder> {

    private Activity context;
    private List<StoriesModel> storiesModels;

    public StoriesAdapter(Activity context, List<StoriesModel> storiesModels) {
        this.context = context;
        this.storiesModels = storiesModels;
    }

    @NonNull
    @Override
    public StoriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StoriesHolder(LayoutInflater.from(context)
                .inflate(R.layout.stories_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesHolder holder, int position) {

        StoriesModel storiesModel = storiesModels.get(position);

        if (position == 0) {

            fetchDataUserID(holder);

//            Glide.with(context)
//                    .load(ProfileFragment.PROFILE_PICTURE)
//                    .placeholder(context.getDrawable(R.drawable.ic_person))
//                    .into(holder.binding.profileImage);

            holder.binding.containerAddStory.setOnClickListener(view -> {

                context.startActivity(new Intent(context, AddStoryActivity.class));
            });

            holder.binding.tvUserName.setText("Your Story");

            holder.binding.ivAddStory.setVisibility(View.VISIBLE);
        }
        else {

            Glide.with(context)
                    .load(storiesModel.getUrl())
                    .placeholder(context.getDrawable(R.drawable.ic_person))
                    .timeout(6500)
                    .into(holder.binding.profileImage);

            holder.binding.ivAddStory.setVisibility(View.GONE);

            holder.binding.tvUserName.setText(storiesModel.getUsername());

            holder.binding.containerAddStory.setOnClickListener(view -> {

                if (holder.getAbsoluteAdapterPosition() == 0) {
                    //new story
                    Dexter.withContext(context)
                            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                                        context.startActivity(new Intent(context, AddStoryActivity.class));
                                    } else {
                                        Toast.makeText(context, "Please allow permission from settings.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();
                }
                else {
                    //open story
                    Intent intent = new Intent(context, ViewStoryActivity.class);
                    intent.putExtra(ViewStoryActivity.URL_KEY, storiesModel.getUrl());
                    intent.putExtra(ViewStoryActivity.FILE_TYPE, storiesModel.getType());
                    intent.putExtra(ViewStoryActivity.POSITION, position);
                    context.startActivity(intent);
                }
            });
        }
    }

    private void fetchDataUserID(StoriesHolder holder) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot snapshot = task.getResult();

                            String profilePicture = snapshot.getString("profilePicture");

                            Glide.with(context)
                                    .load(profilePicture)
                                    .placeholder(context.getDrawable(R.drawable.ic_person))
                                    .into(holder.binding.profileImage);
                        }
                        else {
                            assert task.getException() != null;
                            Toast.makeText(context, "Error: " + task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {

        if (storiesModels != null)
            return storiesModels.size();
        else
            return 0;
    }

    public class StoriesHolder extends RecyclerView.ViewHolder {

        private StoriesItemBinding binding;

        public StoriesHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoriesItemBinding.bind(itemView);
        }
    }



}
