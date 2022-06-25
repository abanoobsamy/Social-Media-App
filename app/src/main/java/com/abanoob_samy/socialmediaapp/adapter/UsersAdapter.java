package com.abanoob_samy.socialmediaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.UsersItemBinding;
import com.abanoob_samy.socialmediaapp.listeners.OnUsersClickListener;
import com.abanoob_samy.socialmediaapp.pojo.UsersModel;
import com.abanoob_samy.socialmediaapp.utils.StringManipulation;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder> {

    private Context context;
    private List<UsersModel> usersModels;

    private OnUsersClickListener mUsersClickListener;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public UsersAdapter(Context context, List<UsersModel> usersModels) {
        this.context = context;
        this.usersModels = usersModels;
    }

    public void setUsersClickListener(OnUsersClickListener mUsersClickListener) {
        this.mUsersClickListener = mUsersClickListener;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersHolder(LayoutInflater.from(context)
                .inflate(R.layout.users_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {

        UsersModel usersModel = usersModels.get(position);

        //which means if it's my account as current user is in recycler list, then hide containerLayout.
        if (usersModel.getUid().equals(user.getUid())) {
            holder.binding.container.setVisibility(View.GONE);
            holder.binding.container.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
        else {
            holder.binding.container.setVisibility(View.VISIBLE);
        }

        holder.binding.tvUserName.setText(StringManipulation.condenseUsername(usersModel.getUsername())
                .toLowerCase(Locale.ROOT));
        holder.binding.tvStatus.setText(usersModel.getStatus());

        Glide.with(context)
                .load(usersModel.getProfilePicture())
                .placeholder(R.drawable.ic_person)
                .timeout(6500)
                .into(holder.binding.profileImage);

        holder.itemView.setOnClickListener(view -> mUsersClickListener.onUsersClick(usersModel, position));
    }

    @Override
    public int getItemCount() {
        return usersModels.size();
    }

    public class UsersHolder extends RecyclerView.ViewHolder {

        private UsersItemBinding binding;

        public UsersHolder(@NonNull View itemView) {
            super(itemView);
            binding = UsersItemBinding.bind(itemView);
        }
    }

}
