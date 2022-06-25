package com.abanoob_samy.socialmediaapp.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.adapter.ChatsAdapter;
import com.abanoob_samy.socialmediaapp.databinding.ActivityViewChatBinding;
import com.abanoob_samy.socialmediaapp.pojo.ChatsModel;
import com.abanoob_samy.socialmediaapp.view.fragment.ForgottenPasswordFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.ProfileFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewChatActivity extends AppCompatActivity {

    private ActivityViewChatBinding binding;

    private FirebaseUser mUser;

    private List<ChatsModel> chatsModels;
    private ChatsAdapter adapter;

    private String oppositeUID, messagesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setUpRecyclerChat();

        loadUserData();

        loadMessagesChat();

        makeAChatBetweenTwoUser();
    }

    private void makeAChatBetweenTwoUser() {

        binding.ivSendMsg.setOnClickListener(view -> {

            String message = binding.etWriteMessage.getText().toString();

            if (message.trim().isEmpty()) {
                return;
            }

            binding.ivSendMsg.setVisibility(View.VISIBLE);

            CollectionReference messagesReference = FirebaseFirestore.getInstance()
                    .collection("Messages");

            //here we didn't need this lines because the process(chat) has already started. ;'
//            List<String> listUid = new ArrayList<>();
//
//            listUid.add(0, mUser.getUid());
//            listUid.add(1, uid);

//            String messageId = messagesReference.document().getId();

            Map<String, Object> messagesMap = new HashMap<>();
//            messagesMap.put("id", messageId);
//            messagesMap.put("uid", listUid);
            messagesMap.put("lastMessage", message);
            messagesMap.put("timeStamp", FieldValue.serverTimestamp());

            //update because this will modify everytime to get the last thing.
            messagesReference.document(messagesId)
                    .update(messagesMap);

//            CollectionReference chatsReference = FirebaseFirestore.getInstance()
//                    .collection("Messages")
//                    .document(messagesId)
//                    .collection("Chats");

            String chatId = messagesReference.document(messagesId)
                    .collection("Chats")
                    .document()
                    .getId();

            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("id", chatId);
            chatMap.put("senderId", mUser.getUid());
            chatMap.put("message", message);
            chatMap.put("timeStamp", FieldValue.serverTimestamp());

            //set because this will send everytime a new message or chat in chat.
            messagesReference.document(messagesId)
                    .collection("Chats")
                    .document(chatId)
                    .set(chatMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                binding.etWriteMessage.setText("");
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Something went wrong!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });
    }

    private void setUpRecyclerChat() {

        chatsModels = new ArrayList<>();

//        binding.recyclerViewChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //because to see last message
        linearLayoutManager.setStackFromEnd(true);

        binding.recyclerViewChat.setLayoutManager(linearLayoutManager);
        adapter = new ChatsAdapter(this, chatsModels);
        binding.recyclerViewChat.setAdapter(adapter);
    }

    private void loadUserData() {

        //come from profileFragment and MessagesContactActivity
        oppositeUID = getIntent().getStringExtra("uid");

        FirebaseFirestore.getInstance().collection("Users")
                .document(oppositeUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("Error: ", error.getMessage());
                            return;
                        }

                        assert value != null;

                        if (!value.exists())
                            return;

                        boolean isOnline = value.getBoolean("online");

                        //this line mean if(isOnline) which mean true btw, return "Online"
                        // else which mean isOnline == false , return "Offline"
                        // in one line
                        binding.tvStatus.setText(isOnline ? "Online" : "Offline");

                        Glide.with(getApplicationContext())
                                .load(value.getString("profilePicture"))
                                .into(binding.profileImageUser);

                        binding.tvFullName.setText(value.getString("fullName"));
                    }
                });
    }

    private void loadMessagesChat() {

        //come from profileFragment and MessagesContactActivity
        messagesId = getIntent().getStringExtra("id");

        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("Messages")
                .document(messagesId)
                .collection("Chats");

        reference.orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("Error: ", error.getMessage());
                            return;
                        }

//                        assert value != null;
                        //this in up or this in down
                        if (value == null || value.isEmpty())
                            return;

                        chatsModels.clear();

                        for (QueryDocumentSnapshot snapshot: value) {

                            if (snapshot.exists()) {
                                ChatsModel chatsModel = snapshot.toObject(ChatsModel.class);
                                chatsModels.add(chatsModel);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.updateStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HomeActivity.updateStatus(false);
    }

}