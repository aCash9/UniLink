package com.example.unilink.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MyPostsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageButton backButton;
    private TextView username, name;
    private ImageView profileImage;

    private Button editProfile;
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
        username = findViewById(R.id.username);
        profileImage = findViewById(R.id.userImage);
        name = findViewById(R.id.name);
        editProfile = findViewById(R.id.editProfile);

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MyPostsActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        Uri uri = user.getPhotoUrl();
        if(uri != null) {
            Picasso.get().load(uri).into(profileImage);
        }

        controller.getUserData(userProfile -> {
            username.setText(userProfile.getUsername());
            name.setText(userProfile.getName());

        });

        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loading(true);
        controller.getMyPosts(user.getUid(), posts -> {
            RecyclerMyPostAdapter adapter = new RecyclerMyPostAdapter(this, posts);
            recyclerView.setAdapter(adapter);
            loading(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            controller.getMyPosts(user.getUid(), posts -> {
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

