package com.example.unilink.viewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.unilink.Fragments.UserPostFragment;
import com.example.unilink.Fragments.UserReelsFragment;

import java.util.ArrayList;

public class ViewPagerPostAdapter extends FragmentPagerAdapter {
    String userID;
    public ViewPagerPostAdapter(@NonNull FragmentManager fm, String userID) {
        super(fm);
        this.userID = userID;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new UserPostFragment(userID);
        } else {
            return new UserReelsFragment(userID);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0) {
            return "Posts";
        } else {
            return "Reels";
        }
    }
}
