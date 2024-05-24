package com.example.unilink.activity.events;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.unilink.R;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.Event;
import com.example.unilink.recyclerAdapters.RecyclerClubsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class EventsActivity extends AppCompatActivity {


    FirebaseController controller;
    private ImageButton back_btn;
    private Uri uriImage;
    TextView title, text, link;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean animFlag = false;
    private Dialog dialog;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_events);
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
        controller.getClubsCard(list -> {
            RecyclerClubsAdapter adapter = new RecyclerClubsAdapter(this, list);
            recyclerView.setAdapter(adapter);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            controller.getClubsCard(list -> {
                RecyclerClubsAdapter adapter = new RecyclerClubsAdapter(this, list);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            });
        });

        final FloatingActionButton addPost = findViewById(R.id.addPost);
        final FloatingActionButton updateBannerImage = findViewById(R.id.addImage);
        final FloatingActionButton addEvent = findViewById(R.id.addReel);

        Animation open = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        Animation close = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);

        Animation from = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        Animation to = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        addPost.setOnClickListener(v -> {
            if(animFlag) {
                addPost.startAnimation(close);
                animFlag = false;
                updateBannerImage.startAnimation(to);
                addEvent.startAnimation(to);
            } else {
                addPost.startAnimation(open);
                animFlag = true;
                updateBannerImage.startAnimation(from);
                addEvent.startAnimation(from);
            }
        });

        updateBannerImage.setOnClickListener(v -> {
            showDialogBox();
        });

        addEvent.setOnClickListener(v -> {
            showAddEventDialogBox();
        });

        controller.verifyIfAClub(response -> {
            if(response) {
                addPost.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showAddEventDialogBox() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_events_row);

        Button upload = dialog.findViewById(R.id.uploadEvent);

        upload.setOnClickListener(v -> {
            title = dialog.findViewById(R.id.title);
            text = dialog.findViewById(R.id.text);
            link = dialog.findViewById(R.id.link);

            String title_text = title.getText().toString();
            String text_text = text.getText().toString();
            String link_text = link.getText().toString();

            if(checkEditTextFields(title_text, text_text, link_text)) {
                return;
            }
            Event event = new Event(random(), title_text, text_text, link_text, FirebaseAuth.getInstance().getCurrentUser().getUid(), String.valueOf(System.currentTimeMillis()));
            upload.setEnabled(false);
            upload.setBackgroundColor(Color.GRAY);
            controller.addEvent(event, response -> {
                if(response)
                        dialog.dismiss();
            });

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @SuppressLint("SetTextI18n")
    private boolean checkEditTextFields(String titleT, String textT, String linkT) {
        if (titleT.isEmpty()) {
            title.requestFocus();
            title.setError("This field can't be empty");
            return true;
        }
        if (textT.isEmpty()) {
            text.requestFocus();
            text.setError("This field can't be empty");
            return true;
        }
        if (linkT.isEmpty()) {
            link.requestFocus();
            link.setError("This field can't be empty");
            return true;
        }

        return false;
    }

    private String random() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0, 10);
    }

    private void showDialogBox() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_banner_layout);

        image = dialog.findViewById(R.id.image);
        Button uploadBanner = dialog.findViewById(R.id.uploadBanner);

        image.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        uploadBanner.setOnClickListener(v -> {
            uploadBanner.setEnabled(false);
            uploadBanner.setBackgroundColor(Color.GRAY);
            String bannerID = UUID.randomUUID().toString();
            bannerID = bannerID.replaceAll("-", "").substring(0, 10);
            StorageReference ref = FirebaseStorage.getInstance().getReference("userPosts").child(bannerID);

            ref.putFile(uriImage)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            ref.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        controller.updateClubBanner(String.valueOf(uri), response -> {
                                            if(response)
                                                dialog.dismiss();

                                            uploadBanner.setEnabled(true);
                                            uploadBanner.setBackgroundColor(Color.parseColor("#E24BFB"));
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        uploadBanner.setEnabled(true);
                                        uploadBanner.setBackgroundColor(Color.parseColor("#E24BFB"));
                                    });
                        }
                    });
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            uriImage = selectedImageUri;
            image.setImageURI(selectedImageUri);
        }
    }

}