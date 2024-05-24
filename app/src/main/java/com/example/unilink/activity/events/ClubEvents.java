package com.example.unilink.activity.events;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

import com.example.unilink.R;
import com.example.unilink.callback.ClubEventCallback;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.Event;
import com.example.unilink.recyclerAdapters.RecyclerClubsAdapter;
import com.example.unilink.recyclerAdapters.RecyclerEventAdapter;

import java.util.ArrayList;

public class ClubEvents extends AppCompatActivity {

    private FirebaseController controller;
    private ImageButton back_btn;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_club_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(v -> finish());

        controller = new FirebaseController();
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String clubUID = intent.getStringExtra("clubUID");

        controller.getEvents(clubUID, list -> {
            RecyclerEventAdapter adapter = new RecyclerEventAdapter(this, list);
            recyclerView.setAdapter(adapter);
        });


        swipeRefreshLayout.setOnRefreshListener(() -> {
            controller.getEvents(clubUID, list -> {
                RecyclerEventAdapter adapter = new RecyclerEventAdapter(this, list);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            });
        });
    }
}