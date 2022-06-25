package com.abanoob_samy.socialmediaapp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.adapter.MessagesAdapter;
import com.abanoob_samy.socialmediaapp.adapter.NotificationAdapter;
import com.abanoob_samy.socialmediaapp.databinding.FragmentNotificationBinding;
import com.abanoob_samy.socialmediaapp.pojo.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding binding;

    private List<NotificationModel> notificationModels;
    private NotificationAdapter adapter;

    private FirebaseUser mUser;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        setUpRecyclerHome();

        loadNotification();
    }

    private void setUpRecyclerHome() {

        notificationModels = new ArrayList<>();

        binding.recyclerViewNotification.setHasFixedSize(true);
        binding.recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NotificationAdapter(getActivity(), notificationModels);
        binding.recyclerViewNotification.setAdapter(adapter);
    }

    private void loadNotification() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Notifications");

        reference//.whereEqualTo("uid", mUser.getUid())
                .orderBy("timeStamp", Query.Direction.ASCENDING)
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

                        notificationModels.clear();

                        for (QueryDocumentSnapshot snapshot: value) {

                            if (snapshot.exists()) {

                                NotificationModel notificationModel = snapshot.toObject(NotificationModel.class);
                                notificationModels.add(notificationModel);

                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

}