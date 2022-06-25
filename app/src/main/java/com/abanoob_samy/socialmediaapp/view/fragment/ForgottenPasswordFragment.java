package com.abanoob_samy.socialmediaapp.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.databinding.FragmentCreateAccountBinding;
import com.abanoob_samy.socialmediaapp.databinding.FragmentForgottenPasswordBinding;
import com.abanoob_samy.socialmediaapp.view.activity.FragmentReplacerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ForgottenPasswordFragment extends Fragment {

    private static final String TAG = "ForgottenPasswordFragment";

    private FragmentForgottenPasswordBinding binding;

    private FirebaseAuth mAuth;

    public ForgottenPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgottenPasswordBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding.tvBackToLogin.setOnClickListener(view1 -> {

            ((FragmentReplacerActivity) getActivity()).setUpFragment(new LoginFragment());
        });

        binding.btnRecover.setOnClickListener(view1 -> {

            String email = binding.etEmailR.getText().toString();

            if (email.isEmpty() || !email.matches(CreateAccountFragment.REGEX_EMAIL)) {
                binding.etEmailR.setError("Please input valid email!");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password Reset Email Send Successfully.",
                                        Toast.LENGTH_SHORT).show();
                                binding.etEmailR.setText("");
                            }
                            else {
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                            binding.progressBar.setVisibility(View.GONE);
                        }
                    });
        });
    }

}