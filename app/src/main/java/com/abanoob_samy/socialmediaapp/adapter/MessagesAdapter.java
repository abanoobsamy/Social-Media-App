package com.abanoob_samy.socialmediaapp.adapter;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.MessagesItemBinding;
import com.abanoob_samy.socialmediaapp.listeners.OnStartMessagesListener;
import com.abanoob_samy.socialmediaapp.pojo.MessagesModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> {

    private Context context;
    private List<MessagesModel> messagesModels;

    private OnStartMessagesListener onStartMessagesListener;

    public MessagesAdapter(Context context, List<MessagesModel> messagesModels) {
        this.context = context;
        this.messagesModels = messagesModels;
    }

    public void setOnStartMessagesListener(OnStartMessagesListener onStartMessagesListener) {
        this.onStartMessagesListener = onStartMessagesListener;
    }

    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessagesHolder(LayoutInflater.from(context)
                .inflate(R.layout.messages_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {

        MessagesModel messagesModel = messagesModels.get(position);

        String dateInMin;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            long millis = messagesModel.getTimeStamp().toInstant().toEpochMilli();

            dateInMin = DateUtils.getRelativeTimeSpanString(millis,
                    System.currentTimeMillis(),
                    60000,
                    DateUtils.FORMAT_ABBREV_ALL).toString();
        }
        else {

            long millis = messagesModel.getTimeStamp().getTime();

            dateInMin = DateUtils.getRelativeTimeSpanString(millis,
                    System.currentTimeMillis(),
                    60000,
                    DateUtils.FORMAT_ABBREV_ALL).toString();

            dateInMin.replace("+", "");
        }

        System.out.println(dateInMin);

        holder.binding.tvTime.setText(dateInMin);

        holder.binding.tvMsg.setText(messagesModel.getLastMessage());

//        holder.binding.tvCountMsg.setText(chatModel);

        fetchDataUserID(messagesModel.getUid(), holder);

        holder.itemView.setOnClickListener(view -> onStartMessagesListener.onMessages(position, messagesModel));
    }

    private void fetchDataUserID(List<String> uid, MessagesHolder holder) {

        String oppositeUID;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //chat between two users
        if (!uid.get(0).equalsIgnoreCase(user.getUid())) {
            oppositeUID = uid.get(0);
        } else {
            oppositeUID = uid.get(1);
        }

        FirebaseFirestore.getInstance().collection("Users")
                .document(oppositeUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot snapshot = task.getResult();

                            Glide.with(context)
                                    .load(snapshot.getString("profilePicture"))
                                    .placeholder(R.drawable.ic_person)
                                    .timeout(6500)
                                    .into(holder.binding.profileImage);

                            holder.binding.tvFullName.setText(snapshot.getString("fullName"));
                        } else {
                            assert task.getException() != null;
                            Toast.makeText(context, "Error: " + task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {

        if (messagesModels != null) {
            return messagesModels.size();
        } else
            return 0;
    }

    public class MessagesHolder extends RecyclerView.ViewHolder {

        private MessagesItemBinding binding;

        public MessagesHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessagesItemBinding.bind(itemView);

            binding.tvCountMsg.setVisibility(View.GONE);
        }
    }

}
