package com.abanoob_samy.socialmediaapp.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abanoob_samy.socialmediaapp.adapter.UsersAdapter;
import com.abanoob_samy.socialmediaapp.databinding.FragmentSearchBinding;
import com.abanoob_samy.socialmediaapp.listeners.OnDataPass;
import com.abanoob_samy.socialmediaapp.listeners.OnUsersClickListener;
import com.abanoob_samy.socialmediaapp.pojo.UsersModel;
import com.abanoob_samy.socialmediaapp.utils.StringManipulation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment implements OnUsersClickListener {

    private FragmentSearchBinding binding;

    private UsersAdapter adapter;
    private List<UsersModel> usersModels;

    private CollectionReference mReference;

    private OnDataPass onDataPass;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mReference = FirebaseFirestore.getInstance().collection("Users");

        setUpRecyclerHome();

        loadUserData();

        binding.searchViewBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mReference.orderBy("username").startAt(query).endAt(query + "\uf8ff")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {

                                    usersModels.clear();

                                    for (DocumentSnapshot snapshot : task.getResult()) {

                                        if (!snapshot.exists())
                                            return;

                                        UsersModel usersModel = snapshot.toObject(UsersModel.class);
                                        usersModels.add(usersModel);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.equals("")) {
                    usersModels.clear();
                }
                return false;
            }
        });
    }

    private void setUpRecyclerHome() {

        usersModels = new ArrayList<>();

        binding.recyclerViewSearch.setHasFixedSize(true);
        binding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsersAdapter(getContext(), usersModels);
        binding.recyclerViewSearch.setAdapter(adapter);
        adapter.setUsersClickListener(this);

//        loadUserData();
    }

    private void loadUserData() {

        mReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.e("Error: ", error.getMessage());
                    return;
                }

                if (value == null)
                    return;

                usersModels.clear();

                for (DocumentSnapshot snapshot : value) {

                    if (!snapshot.exists())
                        return;

                    UsersModel usersModel = snapshot.toObject(UsersModel.class);
                    usersModels.add(usersModel);
                }

                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onUsersClick(UsersModel usersModel, int position) {

        onDataPass.onChange(usersModel, position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onDataPass = (OnDataPass) context;
    }
}