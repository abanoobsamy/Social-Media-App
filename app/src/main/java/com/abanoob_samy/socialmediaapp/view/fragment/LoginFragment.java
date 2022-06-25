package com.abanoob_samy.socialmediaapp.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.FragmentLoginBinding;
import com.abanoob_samy.socialmediaapp.view.activity.FragmentReplacerActivity;
import com.abanoob_samy.socialmediaapp.view.activity.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private FragmentLoginBinding binding;

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        binding.tvForgotPassword.setOnClickListener(view1 -> {

            ((FragmentReplacerActivity) getActivity()).setUpFragment(new ForgottenPasswordFragment());
        });

        binding.tvCreateAccount.setOnClickListener(view1 -> {

            ((FragmentReplacerActivity) getActivity()).setUpFragment(new CreateAccountFragment());
        });

        binding.googleLoginBtn.setOnClickListener(view1 -> {

            signInWithGoogle();
        });

        binding.btnLogin.setOnClickListener(view1 -> {

            String email = binding.etEmailL.getText().toString();
            String password = binding.etPasswordL.getText().toString();

            if (email.isEmpty() || !email.matches(CreateAccountFragment.REGEX_EMAIL)) {
                binding.etEmailL.setError("Input valid email!");
                return;
            }

            if (password.isEmpty() || password.length() < 6) {
                binding.etPasswordL.setError("Input 6 digit valid password!");
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            signAccount(email, password);
        });
    }

    private void signAccount(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (!user.isEmailVerified()) {
                                Toast.makeText(getContext(), "Please verify your email!",
                                        Toast.LENGTH_SHORT).show();
                            }

                            sendUserToActivity();
                        } else {

                            binding.progressBar.setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Error: " + task.getException()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToActivity() {

        if (getActivity() == null)
            return;

        binding.progressBar.setVisibility(View.GONE);
        startActivity(new Intent(getActivity().getApplicationContext(), HomeActivity.class));
        getActivity().finish();
    }

    private void signInWithGoogle() {
        binding.progressBar.setVisibility(View.VISIBLE);
        Intent intent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(intent);
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                            binding.progressBar.setVisibility(View.VISIBLE);
                            firebaseAuthWithGoogle(account.getIdToken());
                        }
                        catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Log.w(TAG, "Google sign in failed", e);

                        }
                    }
                }
            });

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        Map<String, Object> map = new HashMap<>();
        map.put("uid", user.getUid());
        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        map.put("profilePicture", String.valueOf(account.getPhotoUrl()));
        map.put("status", " ");
        map.put("followers", 0);
        map.put("following", 0);
        map.put("posts", 0);

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
                            sendUserToActivity();
                        } else {

                            binding.progressBar.setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Error: " + task.getException()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}