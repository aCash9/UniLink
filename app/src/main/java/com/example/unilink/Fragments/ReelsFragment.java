package com.example.unilink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.objects.ExoPlayerItem;
import com.example.unilink.recyclerAdapters.VideoAdapter;

import java.util.ArrayList;

public class ReelsFragment extends Fragment {



    public ReelsFragment() {
        // Required empty public constructor
    }

    private ViewPager2 viewPager2;
    private VideoAdapter adapter;

    private FirebaseController controller;
    private ArrayList<ExoPlayerItem> exoPlayerItems = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_reels, container, false);
        viewPager2 = inflate.findViewById(R.id.viewPager2);

        controller = new FirebaseController();

        controller.getPosts(2, posts -> {
            adapter = new VideoAdapter(getContext(), posts, exoPlayerItem -> {
                exoPlayerItems.add(exoPlayerItem);
            });
            setAdapterAndViewPagerCallback();
        });



        return inflate;
    }

    private void setAdapterAndViewPagerCallback() {
        viewPager2.setAdapter(adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int previousIndex = -1;
                for (int i = 0; i < exoPlayerItems.size(); i++) {
                    if (exoPlayerItems.get(i).getPlayer().isPlaying()) {
                        previousIndex = i;
                        break;
                    }
                }
                if (previousIndex != -1) {
                    ExoPlayerItem exoPlayerItem = exoPlayerItems.get(previousIndex);
                    exoPlayerItem.getPlayer().pause();
                    exoPlayerItem.getPlayer().setPlayWhenReady(false);
                }
                int newIndex = -1;
                for (int i = 0; i < exoPlayerItems.size(); i++) {
                    if (exoPlayerItems.get(i).getPosition() == position) {
                        newIndex = i;
                        break;
                    }
                }
                if (newIndex != -1) {
                    ExoPlayerItem exoPlayerItem = exoPlayerItems.get(newIndex);
                    exoPlayerItem.getPlayer().setPlayWhenReady(true);
                    exoPlayerItem.getPlayer().play();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        int index = -1;
        for (int i = 0; i < exoPlayerItems.size(); i++) {
            if (exoPlayerItems.get(i).getPosition() == viewPager2.getCurrentItem()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            ExoPlayerItem exoPlayerItem = exoPlayerItems.get(index);
            exoPlayerItem.getPlayer().pause();
            exoPlayerItem.getPlayer().setPlayWhenReady(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int index = -1;
        for (int i = 0; i < exoPlayerItems.size(); i++) {
            if (exoPlayerItems.get(i).getPosition() == viewPager2.getCurrentItem()) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            ExoPlayerItem exoPlayerItem = exoPlayerItems.get(index);
            exoPlayerItem.getPlayer().setPlayWhenReady(true);
            exoPlayerItem.getPlayer().play();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!exoPlayerItems.isEmpty()) {
            for (ExoPlayerItem item : exoPlayerItems) {
                item.getPlayer().stop();
                item.getPlayer().clearMediaItems();
            }
        }
    }
}