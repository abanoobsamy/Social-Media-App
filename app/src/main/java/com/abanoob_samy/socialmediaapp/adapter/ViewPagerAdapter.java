package com.abanoob_samy.socialmediaapp.adapter;

import com.abanoob_samy.socialmediaapp.view.fragment.AddFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.HomeFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.NotificationFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.ProfileFragment;
import com.abanoob_samy.socialmediaapp.view.fragment.SearchFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    int noOfTabs;

    public ViewPagerAdapter(AppCompatActivity fa, int noOfTabs) {
        super(fa);
        this.noOfTabs = noOfTabs;
    }

    @Override
    public Fragment createFragment(int position) {

        switch (position) {

            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new AddFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ProfileFragment();
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return noOfTabs;
    }
}
