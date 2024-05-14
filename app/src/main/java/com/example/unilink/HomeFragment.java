package com.example.unilink;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean flag = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = inflate.findViewById(R.id.toolbar);
        ImageButton switchbtn = inflate.findViewById(R.id.switchBtn);
        ImageButton addPost = inflate.findViewById(R.id.addPost);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        controller = new FirebaseController();
        swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
        lottieAnimationView = inflate.findViewById(R.id.animationView);

        recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loading(true);
        addPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddNewPost.class);
            startActivity(intent);
        });


        controller.getUserPosts(posts -> {
            RecyclerPostAdapter adapter = new RecyclerPostAdapter(getActivity(), posts);
            recyclerView.setAdapter(adapter);
            loading(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(!flag) {
                controller.getClubPosts(posts -> {
                    RecyclerClubPostAdapter adapter = new RecyclerClubPostAdapter(getActivity(), posts);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    loading(false);
                });
            } else {
                controller.getUserPosts(posts -> {
                    RecyclerPostAdapter adapter = new RecyclerPostAdapter(getActivity(), posts);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    loading(false);
                });
            }
            flag = !flag;
        });

        switchbtn.setOnClickListener(v -> {
          if(flag) {
              controller.getClubPosts(posts -> {
                  RecyclerClubPostAdapter adapter = new RecyclerClubPostAdapter(getActivity(), posts);
                  recyclerView.setAdapter(adapter);
                  loading(false);
              });
          } else {
              controller.getUserPosts(posts -> {
                  RecyclerPostAdapter adapter = new RecyclerPostAdapter(getActivity(), posts);
                  recyclerView.setAdapter(adapter);
                  loading(false);
              });
          }
          flag = !flag;
        });

        return inflate;
    }
    private void loading(boolean loading){
        if(loading) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
        } else {
            lottieAnimationView.pauseAnimation();
            lottieAnimationView.setVisibility(View.GONE);
        }
    }
}