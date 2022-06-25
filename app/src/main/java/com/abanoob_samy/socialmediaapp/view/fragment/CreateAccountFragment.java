package com.abanoob_samy.socialmediaapp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.databinding.FragmentCreateAccountBinding;
import com.abanoob_samy.socialmediaapp.utils.StringManipulation;
import com.abanoob_samy.socialmediaapp.view.activity.FragmentReplacerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateAccountFragment extends Fragment {

    private static final String TAG = "CreateAccountFragment";
    private FragmentCreateAccountBinding binding;

    private FirebaseAuth mAuth;

    public static final String REGEX_EMAIL = ".+@.+\\.[a-z]+(.+)";

    public CreateAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateAccountBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding.tvHaveAnAccount.setOnClickListener(view1 -> {

            ((FragmentReplacerActivity) getActivity()).setUpFragment(new LoginFragment());
        });

        binding.btnSignUp.setOnClickListener(view1 -> {

            String fullName = binding.etFullNameC.getText().toString();
            String email = binding.etEmailC.getText().toString();
            String password = binding.etPasswordC.getText().toString();
            String confirmPassword = binding.etConfirmPasswordC.getText().toString();

            if (fullName.isEmpty() || fullName.equals(" ")) {
                binding.etFullNameC.setError("Please input valid name!");
                return;
            }

            if (email.isEmpty() || !email.matches(REGEX_EMAIL)) {
                binding.etEmailC.setError("Please input valid email!");
                return;
            }

            if (password.isEmpty() || password.length() < 6) {
                binding.etPasswordC.setError("Please input valid password less than 6!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                binding.etConfirmPasswordC.setError("Password not match!");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            createAccount(fullName, email, password);
        });
    }

    private void createAccount(String fullName, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest.Builder reqBuilder =
                                    new UserProfileChangeRequest.Builder();

                            reqBuilder.setDisplayName(fullName);

                            user.updateProfile(reqBuilder.build());

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Email Verification Link Send.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            uploadUser(user, fullName, email, password);
                        }
                        else {

                            binding.progressBar.setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Error: " + task.getException()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadUser(FirebaseUser user, String name, String email, String password) {

        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();
        map.put("uid", user.getUid());
        map.put("fullName", name);
        map.put("username", StringManipulation.condenseUsername(name).toLowerCase(Locale.ROOT));
        map.put("email", email);
        map.put("password", password);
        map.put("profilePicture", " ");
        map.put("status", " ");
        map.put("followers", list);
        map.put("following", list2);

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            assert getActivity() != null;
                            binding.progressBar.setVisibility(View.GONE);
//                            startActivity(new Intent(getActivity().getApplicationContext(), HomeActivity.class));
                            ((FragmentReplacerActivity) getActivity()).setUpFragment(new LoginFragment());
                            getActivity().finish();
                        }
                        else {

                            binding.progressBar.setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Error: " + task.getException()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}