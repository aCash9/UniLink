package com.example.unilink.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

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
    }
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;

    private SwipeRefreshLayout swipeRefreshLayout;
    private int flag = 0;
    Dialog dialog;

    private SimpleExoPlayer player;

    private boolean animFlag = false;
    private TextView title, text;

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

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_intro_dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        title = dialog.findViewById(R.id.title);
        text = dialog.findViewById(R.id.text);

        SharedPreferences preferences = getContext().getSharedPreferences("updateChecked", MODE_PRIVATE);
        boolean checked = preferences.getBoolean("updateChecked", false);

        if(!checked) {
            controller.checkUpdates(update -> {
                if(!(update == null)) {
                    if(!update.isCancellable()) {
                        dialog.setCancelable(false);
                    }
                    text.setText(update.getText());
                    title.setText(update.getTitle());
                    dialog.show();
                    preferences.edit().putBoolean("updateChecked", true).apply();
                }
            });
        }


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
            if(flag == 0) {
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
          if(flag == 0) {
              controller.getPosts(0, posts -> {
                  RecyclerClubPostAdapter adapter = new RecyclerClubPostAdapter(getActivity(), posts);
                  recyclerView.setAdapter(adapter);
                  loading(false);
              });
              flag = 1;
          } else {
              controller.getPosts(1, posts -> {
                  RecyclerPostAdapter adapter = new RecyclerPostAdapter(getActivity(), posts);
                  recyclerView.setAdapter(adapter);
                  loading(false);
              });
              flag = 0;
          }
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