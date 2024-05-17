package com.example.unilink.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.recyclerAdapters.RecyclerMyPostAdapter;
import com.example.unilink.recyclerAdapters.RecyclerSearchedUserPostAdapter;

public class UserPostFragment extends Fragment {
    public UserPostFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    private FirebaseController controller;

    private SwipeRefreshLayout swipeRefreshLayout;

    String userUID;
    public UserPostFragment(String userID) {
        this.userUID = userID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_user_post, container, false);
        recyclerView = inflate.findViewById(R.id.recyclerView);
        swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
        controller = new FirebaseController();

        recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        controller.getMyPosts(userUID, posts -> {
            RecyclerSearchedUserPostAdapter adapter = new RecyclerSearchedUserPostAdapter(getContext(), posts);
            recyclerView.setAdapter(adapter);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            controller.getMyPosts(userUID, posts -> {
                RecyclerMyPostAdapter adapter = new RecyclerMyPostAdapter(getContext(), posts);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            });
        });
        return inflate;
    }
}