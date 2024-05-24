package com.example.unilink.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.viewPagerAdapter.ViewPagerMyPostAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MyAccountActivity extends AppCompatActivity {
    private FirebaseController controller;
    private ImageButton backButton;
    private TextView username, name;
    private ImageView profileImage;

    private FirebaseUser user;
    private Button editProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        controller = new FirebaseController();
        backButton = findViewById(R.id.back_btn);
        username = findViewById(R.id.username);
        profileImage = findViewById(R.id.userImage);
        name = findViewById(R.id.name);
        editProfile = findViewById(R.id.editProfile);

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccountActivity.this, EditAccountActivity.class);
            startActivity(intent);
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Uri uri = user.getPhotoUrl();
        if (uri != null) {
            Log.d("MyAccountActivity", "Profile image URI: " + uri.toString());
            Picasso.get().load(uri).into(profileImage);
        } else {
            Log.d("MyAccountActivity", "No profile image URI found.");
        }


        controller.getUserData(userProfile -> {
            username.setText(userProfile.getUsername());
            name.setText(userProfile.getName());
        });

        backButton.setOnClickListener(v -> finish());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerMyPostAdapter adapter = new ViewPagerMyPostAdapter(getSupportFragmentManager(), user.getUid());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        controller.getUserDataUsingUID(user.getUid(), userProfile -> {
            username.setText(userProfile.getUsername());
            name.setText(userProfile.getName());
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        controller.getUserDataUsingUID(user.getUid(), userProfile -> {
            username.setText(userProfile.getUsername());
            name.setText(userProfile.getName());

            if(userProfile.getUserImageURI() != null && !Objects.equals(userProfile.getUserImageURI(), "")) {
                Picasso.get().load(userProfile.getUserImageURI()).into(profileImage);
            }

        });
    }
}

