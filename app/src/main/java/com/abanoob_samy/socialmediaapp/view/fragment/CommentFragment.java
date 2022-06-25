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
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.adapter.CommentAdapter;
import com.abanoob_samy.socialmediaapp.adapter.HomeAdapter;
import com.abanoob_samy.socialmediaapp.adapter.StoriesAdapter;
import com.abanoob_samy.socialmediaapp.databinding.FragmentCommentBinding;
import com.abanoob_samy.socialmediaapp.databinding.FragmentHomeBinding;
import com.abanoob_samy.socialmediaapp.pojo.CommentsModel;
import com.abanoob_samy.socialmediaapp.pojo.HomeModel;
import com.abanoob_samy.socialmediaapp.utils.StringManipulation;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentFragment extends Fragment {

    private FragmentCommentBinding binding;

    private CommentAdapter adapter;
    private List<CommentsModel> commentsModels;
    private FirebaseUser mUser;

    private String id, uid;

    private CollectionReference reference;

    private String profilePicture;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCommentBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        fetchDataUserID();

        if (getArguments() == null)
            return;

        id = getArguments().getString("id");
        uid = getArguments().getString("uid");

        reference = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .collection("Post Images")
                .document(id)
                .collection("Comments");

        setUpRecyclerHome();

        sendComment();
    }

    private void sendComment() {

        binding.ivSendComment.setOnClickListener(view1 -> {

            String comment = binding.etWriteComment.getText().toString();

            if (comment.isEmpty() || comment.equals(" ")) {
                Toast.makeText(getContext(), "Can not send empty comment!", Toast.LENGTH_SHORT).show();
                return;
            }

            String commentId = reference.document().getId();

            Map<String, Object> map = new HashMap<>();

            map.put("uid", mUser.getUid());
            map.put("commentId", commentId);
            map.put("postId", id);
            map.put("comment", comment);
            map.put("username", StringManipulation.condenseUsername(mUser.getDisplayName())
                    .toLowerCase(Locale.ROOT));
            map.put("profilePicture", profilePicture);
            map.put("timeStamp", FieldValue.serverTimestamp());

            reference.document(commentId)
                    .set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                binding.etWriteComment.setText("");
                                Toast.makeText(getContext(),
                                        "Comment is Successfully."
                                        , Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(),
                                        "Failed to comment: " + task.getException().getMessage()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });
    }

    private void fetchDataUserID() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot snapshot = task.getResult();

                            profilePicture = snapshot.getString("profilePicture");

                        }
                        else {
                            assert task.getException() != null;

                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUpRecyclerHome() {

        commentsModels = new ArrayList<>();

        binding.recyclerViewComments.setHasFixedSize(true);
        binding.recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentAdapter(getContext(), commentsModels);
        binding.recyclerViewComments.setAdapter(adapter);

        loadCommentData();
    }

    private void loadCommentData() {

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.e("Error: ", error.getMessage());
                    return;
                }

                if (value == null)
                    return;

                commentsModels.clear();

                for (QueryDocumentSnapshot snapshot : value) {

                    if (snapshot.exists()) {

                        CommentsModel commentsModel = snapshot.toObject(CommentsModel.class);

                        commentsModels.add(commentsModel);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

}