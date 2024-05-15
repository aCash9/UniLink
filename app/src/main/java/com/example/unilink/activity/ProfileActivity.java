package com.example.unilink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.callback.userProfileCallback;
import com.example.unilink.objects.UserProfile;
import com.example.unilink.recyclerAdapters.RecyclerMyPostAdapter;
import com.example.unilink.recyclerAdapters.RecyclerSearchedUserPostAdapter;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageButton backButton;
    private TextView username, name;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
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
        username = findViewById(R.id.username);
        profileImage = findViewById(R.id.userImage);
        name = findViewById(R.id.name);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        String userUID = intent.getStringExtra("user");
        backButton.setOnClickListener(v -> finish());
        controller.getUserDataUsingUID(userUID, userProfile -> {
            username.setText(userProfile.getUsername());
            name.setText(userProfile.getName());

            if(userProfile.getUserImageURI() != null) {
                Picasso.get().load(userProfile.getUserImageURI()).into(profileImage);
            }

            loading(true);
            controller.getMyPosts(userProfile.getUserUID(), posts -> {
                RecyclerSearchedUserPostAdapter adapter = new RecyclerSearchedUserPostAdapter(this, posts);
                recyclerView.setAdapter(adapter);
                loading(false);
            });

            swipeRefreshLayout.setOnRefreshListener(() -> {
                controller.getMyPosts(userProfile.getUserUID(), posts -> {
                    RecyclerSearchedUserPostAdapter adapter = new RecyclerSearchedUserPostAdapter(this, posts);
                    recyclerView.setAdapter(adapter);
                    loading(false);
                    swipeRefreshLayout.setRefreshing(false);
                });
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