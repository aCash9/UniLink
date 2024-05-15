package com.example.unilink.Fragments;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.activity.AddNewPostActivity;
import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.activity.AddReelActivity;
import com.example.unilink.recyclerAdapters.RecyclerClubPostAdapter;
import com.example.unilink.recyclerAdapters.RecyclerPostAdapter;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean flag = true;

    private SimpleExoPlayer player;

    private boolean animFlag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = inflate.findViewById(R.id.toolbar);
        ImageButton switchbtn = inflate.findViewById(R.id.switchBtn);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        controller = new FirebaseController();
        swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
        lottieAnimationView = inflate.findViewById(R.id.animationView);

        final FloatingActionButton addPost = inflate.findViewById(R.id.addPost);
        final FloatingActionButton addImage = inflate.findViewById(R.id.addImage);
        final FloatingActionButton addReel = inflate.findViewById(R.id.addReel);


        Animation open = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim);
        Animation close = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim);

        Animation from = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom_anim);
        Animation to = AnimationUtils.loadAnimation(getContext(), R.anim.to_bottom_anim);

        addPost.setOnClickListener(v -> {
           if(animFlag) {
               addPost.startAnimation(close);
               animFlag = false;
               addImage.startAnimation(to);
               addReel.startAnimation(to);
           } else {
               addPost.startAnimation(open);
               animFlag = true;
               addImage.startAnimation(from);
               addReel.startAnimation(from);
           }
        });


        recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        player = new SimpleExoPlayer.Builder(getContext()).build();
        loading(true);

        addImage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddNewPostActivity.class);
            startActivity(intent);
        });

        addReel.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddReelActivity.class);
            startActivity(intent);
        });

        controller.getPosts(0, posts -> {
            RecyclerPostAdapter adapter = new RecyclerPostAdapter(getActivity(), posts);
            recyclerView.setAdapter(adapter);
            loading(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(!flag) {
                controller.getPosts(0, posts -> {
                    RecyclerClubPostAdapter adapter = new RecyclerClubPostAdapter(getActivity(), posts);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    loading(false);
                });
            } else {
                controller.getPosts(1, posts -> {
                    RecyclerPostAdapter adapter = new RecyclerPostAdapter(getActivity(), posts);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    loading(false);
                });
            }
        });

        switchbtn.setOnClickListener(v -> {
          if(flag) {
              controller.getPosts(0, posts -> {
                  RecyclerClubPostAdapter adapter = new RecyclerClubPostAdapter(getActivity(), posts);
                  recyclerView.setAdapter(adapter);
                  loading(false);
              });
          } else {
              controller.getPosts(1, posts -> {
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