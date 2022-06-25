package com.abanoob_samy.socialmediaapp.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.abanoob_samy.socialmediaapp.R;
import com.abanoob_samy.socialmediaapp.adapter.ViewPagerAdapter;
import com.abanoob_samy.socialmediaapp.databinding.ActivityHomeBinding;
import com.abanoob_samy.socialmediaapp.listeners.OnDataPass;
import com.abanoob_samy.socialmediaapp.pojo.UsersModel;
import com.abanoob_samy.socialmediaapp.utils.Constants;
import com.abanoob_samy.socialmediaapp.view.fragment.AddFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.HomeFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.NotificationFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.ProfileFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements OnDataPass {

    private ActivityHomeBinding binding;

    public static String USER_ID;
    public static boolean IS_SEARCHED_USER = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpTabsViewPager();
    }

    private void setUpTabsViewPager() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_home_fill));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_search));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_add));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_heart));

        SharedPreferences preferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        String directory = preferences.getString(Constants.PREF_DIRECTORY, "");

        Bitmap bitmap = loadProfileImage(directory);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(drawable));

        binding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(HomeActivity.this, binding.tabLayout.getTabCount());
        binding.viewpager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewpager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                switch (position) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home_fill);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_add);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.ic_heart);
                        break;

                    case 4:
                        tab.setIcon(drawable);
                        break;
                }
            }
        }).attach();

        pagerAdapter.notifyDataSetChanged();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                binding.viewpager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home_fill);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search_fill);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_add_fill);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.ic_heart_fill);
                        break;

                    case 4:
                        tab.setIcon(drawable);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_add);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.ic_heart);
                        break;

                    case 4:
                        tab.setIcon(drawable);
                        break;
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {

                    case 0:
                        tab.setIcon(R.drawable.ic_home_fill);
                        break;

                    case 1:
                        tab.setIcon(R.drawable.ic_search_fill);
                        break;

                    case 2:
                        tab.setIcon(R.drawable.ic_add_fill);
                        break;

                    case 3:
                        tab.setIcon(R.drawable.ic_heart_fill);
                        break;

                    case 4:
                        tab.setIcon(R.drawable.ic_profile_fill);
                        break;
                }
            }
        });

        binding.viewpager.setCurrentItem(0);
    }

    private Bitmap loadProfileImage(String directory) {

        try {
            File file = new File(directory, "profile.png");

            return BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {

        if (binding.viewpager.getCurrentItem() == 4) {
            binding.viewpager.setCurrentItem(0);
            IS_SEARCHED_USER = false;
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onChange(UsersModel usersModel, int position) {

        USER_ID = usersModel.getUid();
        IS_SEARCHED_USER = true;

        binding.viewpager.setCurrentItem(4);
    }

    public static void updateStatus(boolean status) {

        Map<String, Object> map = new HashMap<>();
        map.put("online", status);

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatus(false);
    }
}