package com.example.unilink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.viewPagerAdapter.ViewPagerPostAdapter;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
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

        FirebaseController controller = new FirebaseController();
        ImageButton backButton = findViewById(R.id.back_btn);
        username = findViewById(R.id.username);
        profileImage = findViewById(R.id.userImage);
        name = findViewById(R.id.name);
        Intent intent = getIntent();
        String userUID = intent.getStringExtra("user");
        backButton.setOnClickListener(v -> finish());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerPostAdapter adapter = new ViewPagerPostAdapter(getSupportFragmentManager(), userUID);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        controller.getUserDataUsingUID(userUID, userProfile -> {
            username.setText(userProfile.getUsername());
            name.setText(userProfile.getName());

            if(userProfile.getUserImageURI() != null) {
                Picasso.get().load(userProfile.getUserImageURI()).into(profileImage);
            }

        });
    }
}