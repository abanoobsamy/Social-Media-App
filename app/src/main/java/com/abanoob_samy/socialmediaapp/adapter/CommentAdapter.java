package com.abanoob_samy.socialmediaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.CommentItemBinding;
import com.abanoob_samy.socialmediaapp.pojo.CommentsModel;
import com.bumptech.glide.Glide;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {

    private Context context;
    private List<CommentsModel> commentsModels;

    public CommentAdapter(Context context, List<CommentsModel> commentsModels) {
        this.context = context;
        this.commentsModels = commentsModels;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context)
                .inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        CommentsModel commentsModel = commentsModels.get(position);

        holder.binding.tvUserName.setText(commentsModel.getUsername());
        holder.binding.tvHours.setText(commentsModel.getTimeStamp() + "");
        holder.binding.tvComment.setText(commentsModel.getComment());

        Glide.with(context)
                .load(commentsModel.getProfilePicture())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.binding.profileImage);
    }

    @Override
    public int getItemCount() {

        if (commentsModels != null) {
            return commentsModels.size();
        } else
            return 0;
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        private CommentItemBinding binding;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentItemBinding.bind(itemView);
        }
    }

}
