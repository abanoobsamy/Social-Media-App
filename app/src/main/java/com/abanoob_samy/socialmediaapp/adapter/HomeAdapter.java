package com.abanoob_samy.socialmediaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.HomePostItemBinding;
import com.abanoob_samy.socialmediaapp.listeners.OnPressed;
import com.abanoob_samy.socialmediaapp.pojo.HomeModel;
import com.abanoob_samy.socialmediaapp.view.activity.FragmentReplacerActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    private Activity context;
    private List<HomeModel> homeModels;

    private OnPressed onPressed;

    private Boolean isAvailableLikedByCurrentUser = false;

    public HomeAdapter(Activity context, List<HomeModel> homeModels) {
        this.context = context;
        this.homeModels = homeModels;
    }

    public void setOnPressed(OnPressed onPressed) {
        this.onPressed = onPressed;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeHolder(LayoutInflater.from(context)
                .inflate(R.layout.home_post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {

        HomeModel homeModel = homeModels.get(position);

        //homeModel.getId() = id el post nafso.

        holder.binding.tvUserName.setText(homeModel.getUsername());
        holder.binding.tvHours.setText(homeModel.getTimeStamp() + "");
        holder.binding.tvDescriptionPost.setText(homeModel.getDescription());

        int count = homeModel.getLikes().size();
//        int firstDigit = Integer.parseInt(Integer.toString(count).substring(0, 1));
//        int firstSecondDigit = Integer.parseInt(Integer.toString(count).substring(0, 2));
//        int firstSecondThirdDigit = Integer.parseInt(Integer.toString(count).substring(0, 3));

//        if (count == 0) {
//            holder.binding.tvTextLikesNumber.setVisibility(View.GONE);
//        } else if (count >= 1 && count < 1000) {
//            holder.binding.tvTextLikesNumber.setText(count + " like");
//        } else if (count >= 1000 && count < 10000) {
//            holder.binding.tvTextLikesNumber.setText(firstDigit(count) + "k likes");
//        } else if (count >= 10000 && count < 100000) {
//            holder.binding.tvTextLikesNumber.setText(firstSecondDigit(count) + "k likes");
//        } else if (count >= 100000 && count < 1000000) {
//            holder.binding.tvTextLikesNumber.setText(firstSecondThirdDigit(count) + "k likes");
//        } else if (count >= 1000000 && count < 10000000) {
//            holder.binding.tvTextLikesNumber.setText(firstDigit(count) + "m likes");
//        } else if (count >= 10000000 && count < 100000000) {
//            holder.binding.tvTextLikesNumber.setText(firstSecondDigit(count) + "m likes");
//        } else {
//            holder.binding.tvTextLikesNumber.setText(firstSecondThirdDigit(count) + "m likes");
//        }

        holder.binding.tvTextLikesNumber.setText(count + " like");

        Random random = new Random();

        int color = Color.argb(255, random.nextInt(256),
                random.nextInt(256), random.nextInt(256));

        Glide.with(context)
                .load(homeModel.getProfilePicture())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.binding.profileImage);

        Glide.with(context)
                .load(homeModel.getPostImage())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.binding.ivPostImage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //check if already like.
        if (homeModel.getLikes().contains(user.getUid())) {

            homeModel.setLiked(true);
            holder.binding.ivLike.setImageResource(R.drawable.ic_heart_fill_like);
        }
        else {
            homeModel.setLiked(false);
            holder.binding.ivLike.setImageResource(R.drawable.ic_heart);
        }

        holder.binding.ivComment.setOnClickListener(view -> {

            Intent intent = new Intent(context, FragmentReplacerActivity.class);
            intent.putExtra("id", homeModel.getId());
            intent.putExtra("uid", homeModel.getUid());
            intent.putExtra("isComment", true);
            context.startActivity(intent);
        });

        holder.binding.ivLike.setOnClickListener(view -> {

            if (homeModel.isLiked()) {

                //not clicked
                homeModel.setLiked(false);
                holder.binding.ivLike.setImageResource(R.drawable.ic_heart);
            }
            else {

                //is clicked
                homeModel.setLiked(true);
                holder.binding.ivLike.setImageResource(R.drawable.ic_heart_fill_like);
            }

            // btw, by the way same thing will happen
            onPressed.onLiked(position,
                    homeModel.getId(),
                    homeModel.getUid(),
                    homeModel.getLikes(),
                    holder.binding.ivLike,
                    homeModel.isLiked());

        });

        holder.binding.ivShare.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, homeModel.getPostImage());
            context.startActivity(Intent.createChooser(intent, "Share link using..."));
        });

    }

    @Override
    public int getItemCount() {

        if (homeModels != null) {
            return homeModels.size();
        } else
            return 0;
    }

    public class HomeHolder extends RecyclerView.ViewHolder {

        private HomePostItemBinding binding;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            binding = HomePostItemBinding.bind(itemView);

            onPressed.setCommentCount(binding.tvViewAllComments);
        }
    }

    private int firstDigit(int count) {
        return Integer.parseInt(Integer.toString(count).substring(0, 1));
    }

    private int firstSecondDigit(int count) {
        return Integer.parseInt(Integer.toString(count).substring(0, 2));
    }

    private int firstSecondThirdDigit(int count) {
        return Integer.parseInt(Integer.toString(count).substring(0, 3));
    }

}
