package com.abanoob_samy.socialmediaapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.adapter.HomeAdapter;
import com.abanoob_samy.socialmediaapp.adapter.StoriesAdapter;
import com.abanoob_samy.socialmediaapp.databinding.FragmentHomeBinding;
import com.abanoob_samy.socialmediaapp.listeners.OnPressed;
import com.abanoob_samy.socialmediaapp.pojo.CommentsModel;
import com.abanoob_samy.socialmediaapp.pojo.HomeModel;
import com.abanoob_samy.socialmediaapp.pojo.StoriesModel;
import com.abanoob_samy.socialmediaapp.view.activity.MessagesContactActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private HomeAdapter adapter;
    private List<HomeModel> homeModels;

    private StoriesAdapter storiesAdapter;
    private List<StoriesModel> storiesModels;
    private List<CommentsModel> commentsList = new ArrayList<>();

//    private int commentCount = 0;

    String profilePicture;

    public static MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    private int counterComment = 0;

//    public static int LAST_SIZE = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        binding.btnSendMessage.setOnClickListener(view1 ->
                startActivity(new Intent(getContext(), MessagesContactActivity.class)));

        setUpRecyclerHome();

        loadDataFromFireStore();

        adapter.setOnPressed(new OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likes,
                                ImageView ivLike, boolean liked) {

                DocumentReference reference = FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likes.contains(mUser.getUid()) || liked) {

                    likes.remove(mUser.getUid()); //unlike
                    ivLike.setImageResource(R.drawable.ic_heart);
                }
                else {
                    likes.add(mUser.getUid()); //like
                    ivLike.setImageResource(R.drawable.ic_heart_fill_like);
                }

                Map<String, Object> map = new HashMap<>();
                map.put("likes", likes);

                reference.update(map);

            }

            @Override
            public void setCommentCount(TextView tvCommentCount) {

                if (counterComment == 0) {

                    tvCommentCount.setVisibility(View.GONE);
                }
                else {
                    tvCommentCount.setVisibility(View.VISIBLE);
                }

//                        tvCommentCount.setText("See All " + commentCount.getValue() + " Comments");
                tvCommentCount.setText("See All " + counterComment + " Comments");

//                commentCount.observe(getActivity(), new Observer<Integer>() {
//                    @Override
//                    public void onChanged(Integer integer) {
//
//                        if (counterComment == 0) {
//
//                            tvCommentCount.setVisibility(View.GONE);
//                        }
//                        else {
//                            tvCommentCount.setVisibility(View.VISIBLE);
//                        }
//
////                        tvCommentCount.setText("See All " + commentCount.getValue() + " Comments");
//                        tvCommentCount.setText("See All " + counterComment + " Comments");
//                    }
//                });

            }
        });

//        adapter.notifyDataSetChanged();
    }

    private void setUpRecyclerHome() {

        homeModels = new ArrayList<>();

        binding.recyclerViewHome.setHasFixedSize(true);
        binding.recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HomeAdapter(getActivity(), homeModels);
        binding.recyclerViewHome.setAdapter(adapter);

        storiesModels = new ArrayList<>();

        binding.recyclerViewHomeStories.setHasFixedSize(true);
        binding.recyclerViewHomeStories.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        storiesModels.add(new StoriesModel("","", "", "", "", ""));
        storiesAdapter = new StoriesAdapter(getActivity(), storiesModels);
        binding.recyclerViewHomeStories.setAdapter(storiesAdapter);
    }

    private void loadDataFromFireStore() {

        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(mUser.getUid());

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Users");

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.e("Error: ", error.getMessage());
                    return;
                }

                if (value == null)
                    return;

                List<String> uidList = (List<String>) value.get("following");

                if (uidList == null || uidList.isEmpty())
                    return;

                collectionReference.whereIn("uid", uidList)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if (error != null) {
                                    Log.e("Error: ", error.getMessage());
                                    return;
                                }

                                if (value == null)
                                    return;

                                for (QueryDocumentSnapshot snapshot : value) {

                                    snapshot.getReference().collection("Post Images")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                    if (error != null) {
                                                        Log.e("Error: ", error.getMessage());
                                                        return;
                                                    }

                                                    if (value == null)
                                                        return;

                                                    homeModels.clear();

                                                    for (QueryDocumentSnapshot snapshot : value) {

                                                        if (snapshot.exists()) {

                                                            HomeModel homeModel = snapshot.toObject(HomeModel.class);

                                                            homeModels.add(new HomeModel(homeModel.getId(),
                                                                    homeModel.getUsername(),
                                                                    homeModel.getTimeStamp(),
                                                                    homeModel.getProfilePicture(),
                                                                    homeModel.getPostImage(),
                                                                    homeModel.getUid(),
                                                                    homeModel.getDescription(),
                                                                    homeModel.getLikes(),
                                                                    homeModel.getCommentsModelList(),
                                                                    homeModel.isLiked()));

                                                            snapshot.getReference()
                                                                    .collection("Comments")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            if (task.isSuccessful()) {

//                                                                                int count = 0;

                                                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                                                                    count++;

                                                                                    CommentsModel commentsModel = documentSnapshot.toObject(CommentsModel.class);
                                                                                    commentsList.add(commentsModel);

                                                                                }
                                                                                counterComment = commentsList.size();

//                                                                                commentCount.setValue(count);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }

                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                }

                            }
                        });

                //todo: fetch stories
                loadStories(uidList);
            }
        });

    }

    private void loadStories(List<String> uidList) {

        Query query = FirebaseFirestore.getInstance().collection("Stories");

        query.whereIn("uid", uidList)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("Error: ", error.getMessage());
                            return;
                        }

                        if (value == null)
                            return;

                        //here became error
                        storiesModels.clear();

                        for (QueryDocumentSnapshot snapshot : value) {

                            if (!value.isEmpty()) {
                                StoriesModel storiesModel = snapshot.toObject(StoriesModel.class);
                                storiesModels.add(storiesModel);
                            }
                        }

                        storiesAdapter.notifyDataSetChanged();
                    }
                });
    }
}