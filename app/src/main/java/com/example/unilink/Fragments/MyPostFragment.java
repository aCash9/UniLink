package com.example.unilink.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.recyclerAdapters.RecyclerMyPostAdapter;
import com.example.unilink.recyclerAdapters.RecyclerSearchedUserPostAdapter;

public class MyPostFragment extends Fragment {
    public MyPostFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    private FirebaseController controller;

    private SwipeRefreshLayout swipeRefreshLayout;

    String userUID;
    public MyPostFragment(String userID) {
        this.userUID = userID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_my_post, container, false);
        recyclerView = inflate.findViewById(R.id.recyclerView);
        swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
        controller = new FirebaseController();

        recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        controller.getMyPosts(userUID, posts -> {
            RecyclerMyPostAdapter adapter = new RecyclerMyPostAdapter(getContext(), posts);
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