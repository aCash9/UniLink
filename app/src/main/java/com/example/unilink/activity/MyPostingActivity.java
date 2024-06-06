package com.example.unilink.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.R;
import com.example.unilink.callback.ProductListCallback;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.Product;
import com.example.unilink.recyclerAdapters.RecyclerMarketProductAdapter;
import com.example.unilink.recyclerAdapters.RecyclerUserProductAdapter;

import java.util.ArrayList;

public class MyPostingActivity extends AppCompatActivity {
    private ImageButton backbtn;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;
    private View darkBackground;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_posting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backbtn = findViewById(R.id.back_btn);
        lottieAnimationView = findViewById(R.id.animationView);
        darkBackground = findViewById(R.id.darkBackground);
        backbtn.setOnClickListener(v -> finish());
        controller = new FirebaseController();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loading(true);
        controller.getMyProducts(list -> {
            RecyclerUserProductAdapter adapter = new RecyclerUserProductAdapter(this, list);
            recyclerView.setAdapter(adapter);
            loading(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> controller.getMarketPlaceProducts(posts -> {
            RecyclerUserProductAdapter adapter = new RecyclerUserProductAdapter(this, posts);
            recyclerView.setAdapter(adapter);
            loading(false);
            swipeRefreshLayout.setRefreshing(false);
        }));

    }
    private void loading(boolean status) {
        if(!status) {
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
            darkBackground.setVisibility(View.INVISIBLE);
        } else {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            darkBackground.setVisibility(View.VISIBLE);
        }
    }
}