package com.example.unilink;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class MyPosts extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_posts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        controller = new FirebaseController();
        lottieAnimationView = findViewById(R.id.animationView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        backButton = findViewById(R.id.back_btn);

        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loading(true);
        controller.getMyPosts(posts -> {
            RecyclerMyPostAdapter adapter = new RecyclerMyPostAdapter(this, posts);
            recyclerView.setAdapter(adapter);
            loading(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            controller.getMyPosts(posts -> {
                RecyclerMyPostAdapter adapter = new RecyclerMyPostAdapter(this, posts);
                recyclerView.setAdapter(adapter);
                loading(false);
                swipeRefreshLayout.setRefreshing(false);
            });
        });

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

