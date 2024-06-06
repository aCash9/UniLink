package com.example.unilink.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.unilink.RoomDatabase.DatabaseHelper;
import com.example.unilink.RoomDatabase.dao.UserDao;
import com.example.unilink.RoomDatabase.objects.UserInfo;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.viewPagerAdapter.ViewPagerMyPostAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class MyAccountActivity extends AppCompatActivity {
    private TextView username, name;

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

        FirebaseController controller = new FirebaseController();
        ImageButton backButton = findViewById(R.id.back_btn);
        username = findViewById(R.id.username);
        ImageView profileImage = findViewById(R.id.userImage);
        name = findViewById(R.id.name);
        Button editProfile = findViewById(R.id.editProfile);

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccountActivity.this, EditAccountActivity.class);
            startActivity(intent);
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();


        DatabaseHelper databaseHelper = DatabaseHelper.getDB(this);

        UserDao userDao = databaseHelper.userDao();
        List<UserInfo> userData = userDao.getAll();
//        userDao.deleteAll();
        if(userData.isEmpty()) {
            Uri uri = user.getPhotoUrl();
            if (uri != null) {
                Picasso.get().load(uri).into(profileImage);
            }
            controller.getUserData(userProfile -> {
                username.setText(userProfile.getUsername());
                name.setText(userProfile.getName());
            });
        } else {
            UserInfo userInfo = userData.get(0);
            username.setText(userInfo.getUsername());
            name.setText(userInfo.getName());
            loadImageIntoImageView(userInfo.getUserImageUri() , profileImage);
        }

        backButton.setOnClickListener(v -> finish());
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerMyPostAdapter adapter = new ViewPagerMyPostAdapter(getSupportFragmentManager(), user.getUid());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadImageIntoImageView(String filePath, ImageView imageView) {
        Glide.with(this)
                .load(filePath)
                .into(imageView);
    }
}

