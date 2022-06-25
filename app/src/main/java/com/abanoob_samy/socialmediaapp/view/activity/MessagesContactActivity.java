package com.abanoob_samy.socialmediaapp.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.abanoob_samy.socialmediaapp.adapter.MessagesAdapter;
import com.abanoob_samy.socialmediaapp.databinding.ActivityMessagesContactBinding;
import com.abanoob_samy.socialmediaapp.listeners.OnStartMessagesListener;
import com.abanoob_samy.socialmediaapp.pojo.MessagesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesContactActivity extends AppCompatActivity {

    private ActivityMessagesContactBinding binding;

    private MessagesAdapter adapter;
    private List<MessagesModel> messagesModels;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setUpRecyclerHome();

        fetchUserData();

        adapter.setOnStartMessagesListener(new OnStartMessagesListener() {
            @Override
            public void onMessages(int position, MessagesModel messagesModel) {

                String oppositeUID;

                if (!messagesModel.getUid().get(0).equalsIgnoreCase(mUser.getUid())) {
                    oppositeUID = messagesModel.getUid().get(0);
                }
                else {
                    oppositeUID = messagesModel.getUid().get(1);
                }

                Intent intent = new Intent(getApplicationContext(), ViewChatActivity.class);
                intent.putExtra("uid", oppositeUID);
                intent.putExtra("id", messagesModel.getId());
                startActivity(intent);

            }
        });
    }

    private void setUpRecyclerHome() {

        messagesModels = new ArrayList<>();

        binding.recyclerViewChat.setHasFixedSize(true);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessagesAdapter(this, messagesModels);
        binding.recyclerViewChat.setAdapter(adapter);
    }

    private void fetchUserData() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        reference.whereArrayContains("uid", mUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("Error: ", error.getMessage());
                            return;
                        }

                        if (value == null || value.isEmpty())
                            return;

                        messagesModels.clear();

                        for (QueryDocumentSnapshot snapshot: value) {

                            if (snapshot.exists()) {
                                MessagesModel messagesModel = snapshot.toObject(MessagesModel.class);
                                messagesModels.add(messagesModel);
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