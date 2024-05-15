package com.example.unilink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.callback.UserPostsCallback;
import com.example.unilink.objects.UserPosts;
import com.example.unilink.recyclerAdapters.ViewPagerReelsAdapter;

import java.util.ArrayList;

public class ReelsFragment extends Fragment {



    public ReelsFragment() {
        // Required empty public constructor
    }

    private ViewPager2 viewPager2;
    private FirebaseController controller;
    private ViewPagerReelsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_reels, container, false);
        viewPager2 = inflate.findViewById(R.id.viewPager);

        controller = new FirebaseController();
        controller.getPosts(2, posts -> {
            adapter = new ViewPagerReelsAdapter(getContext(), posts);
            viewPager2.setAdapter(adapter);
            viewPager2.setClipToPadding(false);
            viewPager2.setClipChildren(false);
            viewPager2.setOffscreenPageLimit(5);
            viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        });

        return inflate;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.releasePlayers();
        }
    }
}