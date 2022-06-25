package com.abanoob_samy.socialmediaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.ChatsItemBinding;
import com.abanoob_samy.socialmediaapp.pojo.ChatsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsHolder> {

    private Context context;
    private List<ChatsModel> chatsModels;

    public ChatsAdapter(Context context, List<ChatsModel> chatsModels) {
        this.context = context;
        this.chatsModels = chatsModels;
    }

    @NonNull
    @Override
    public ChatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatsHolder(LayoutInflater.from(context)
                .inflate(R.layout.chats_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsHolder holder, int position) {

        ChatsModel chatsModel = chatsModels.get(position);

        holder.binding.tvRightChat.setText(chatsModel.getTimeStamp().toString());

//        holder.binding.tvCountMsg.setText(chatModel);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (chatsModel.getSenderId().equalsIgnoreCase(user.getUid())) {

            holder.binding.tvLeftChat.setVisibility(View.GONE);
            holder.binding.tvRightChat.setVisibility(View.VISIBLE);
            holder.binding.tvRightChat.setText(chatsModel.getMessage());
        }
        else {
            holder.binding.tvLeftChat.setVisibility(View.VISIBLE);
            holder.binding.tvRightChat.setVisibility(View.GONE);
            holder.binding.tvLeftChat.setText(chatsModel.getMessage());
        }

    }

    @Override
    public int getItemCount() {

        if (chatsModels != null) {
            return chatsModels.size();
        } else
            return 0;
    }

    public class ChatsHolder extends RecyclerView.ViewHolder {

        private ChatsItemBinding binding;

        public ChatsHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatsItemBinding.bind(itemView);
        }
    }

}
