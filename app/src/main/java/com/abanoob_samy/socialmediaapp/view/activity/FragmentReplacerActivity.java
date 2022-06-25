package com.abanoob_samy.socialmediaapp.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.databinding.ActivityFragmentReplacerBinding;
import com.abanoob_samy.socialmediaapp.view.fragment.CommentFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.CreateAccountFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.LoginFragment;

public class FragmentReplacerActivity extends AppCompatActivity {

    private ActivityFragmentReplacerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentReplacerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean isComment = getIntent().getBooleanExtra("isComment", false);

        //for transfer to fragment
        if (isComment) {
            setUpFragment(new CommentFragment());
        }
        else
            setUpFragment(new LoginFragment());
    }

    public void setUpFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (fragment instanceof CreateAccountFragment) {
            transaction.addToBackStack(null);
        }

        if (fragment instanceof CommentFragment) {

            String id = getIntent().getStringExtra("id");
            String uid = getIntent().getStringExtra("uid");

            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("uid", uid);
            fragment.setArguments(bundle);
        }

        transaction.replace(binding.frameLayout.getId(), fragment);
        transaction.commit();
    }
}