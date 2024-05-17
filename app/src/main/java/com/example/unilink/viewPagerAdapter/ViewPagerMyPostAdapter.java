package com.example.unilink.viewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.unilink.Fragments.MyPostFragment;
import com.example.unilink.Fragments.MyReelsFragment;
import com.example.unilink.Fragments.UserPostFragment;
import com.example.unilink.Fragments.UserReelsFragment;

public class ViewPagerMyPostAdapter extends FragmentPagerAdapter {
    String userID;
    public ViewPagerMyPostAdapter(@NonNull FragmentManager fm, String userID) {
        super(fm);
        this.userID = userID;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new MyPostFragment(userID);
        } else {
            return new MyReelsFragment(userID);
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
