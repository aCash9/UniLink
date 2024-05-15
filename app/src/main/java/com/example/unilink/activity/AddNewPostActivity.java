package com.example.unilink.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.CropperActivity;
import com.example.unilink.objects.ClubDetails;
import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.objects.UserPosts;
import com.example.unilink.objects.UserProfile;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddNewPostActivity extends AppCompatActivity {

    private ImageButton backbtn;
    private ImageView image;
    private Button uploadUser, uploadClub;
    private Uri uriImage;
    private EditText caption;
    private StorageReference storageReference;

    private UserProfile profile;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;
    private View darkBackground;
    private ClubDetails details;
    ActivityResultLauncher<String> mGETcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backbtn = findViewById(R.id.back_btn);
        image = findViewById(R.id.image);
        uploadUser = findViewById(R.id.uploadUser);
        uploadClub = findViewById(R.id.uploadClub);
        caption = findViewById(R.id.caption);
        lottieAnimationView = findViewById(R.id.animationView);
        darkBackground = findViewById(R.id.darkBackground);
        profile = new UserProfile();

        storageReference = FirebaseStorage.getInstance().getReference("userPosts");
        controller = new FirebaseController();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        controller.getUserData(userProfile -> {
            profile = userProfile;
        });


        controller.verifyIfPartOfAnyClub(clubDetails -> {
            if(clubDetails != null) {
                uploadClub.setVisibility(View.VISIBLE);
                details = clubDetails;
            }
        });

        backbtn.setOnClickListener(v -> {
            finish();
        });

        image.setOnClickListener(v -> {
            mGETcontent.launch("image/*");

        });

        mGETcontent = registerForActivityResult(new ActivityResultContracts.GetContent(), o -> {
            Intent intent = new Intent(AddNewPostActivity.this, CropperActivity.class);
            if(o != null) {
                intent.putExtra("data", o.toString());
                intent.putExtra("crop", "post");
                startActivityForResult(intent, 101);
            }
        });


        Toast toast = new Toast(this);
        View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
        toast.setView(view);
        TextView promptTextview = view.findViewById(R.id.promptText);
        ImageView imageView = view.findViewById(R.id.action_image);
        toast.setDuration(Toast.LENGTH_LONG);

        uploadUser.setOnClickListener(v -> {
            String captions = caption.getText().toString();

            if(captions.isEmpty()) {
                caption.setError("Please enter a caption");
                caption.requestFocus();
                return;
            }

            buttons(false);
            if(uriImage != null) {
                String postID = random();
                StorageReference ref = storageReference.child(postID + getFileExtension(uriImage));
                ref.putFile(uriImage)
                        .addOnSuccessListener(taskSnapshot -> {
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                UserPosts userPosts = new UserPosts(profile.getUsername(), postID, 0, String.valueOf(user.getPhotoUrl()), String.valueOf(uri), captions, String.valueOf(System.currentTimeMillis()), user.getUid());

                                controller.addPost(0, userPosts, true, response -> {
                                    if(response) {
                                        promptTextview.setText("Post uploaded successfully");
                                        imageView.setImageResource(R.drawable.baseline_cloud_done_24);
                                        toast.show();
                                        finish();
                                    } else {
                                        promptTextview.setText("Post upload unsuccessful");
                                        imageView.setImageResource(R.drawable.baseline_error_24);
                                        toast.show();
                                    }
                                    buttons(true);
                                });
                            });
                        })
                        .addOnFailureListener(e -> {
                            promptTextview.setText("Post upload unsuccessful");
                            imageView.setImageResource(R.drawable.baseline_error_24);
                            toast.show();
                            buttons(true);
                        });
            } else {
                promptTextview.setText("Please select an image");
                imageView.setImageResource(R.drawable.baseline_error_24);
                toast.show();
                buttons(true);
            }
        });

        uploadClub.setOnClickListener(v -> {
            String captions = caption.getText().toString();

            if(captions.isEmpty()) {
                caption.setError("Please enter a caption");
                caption.requestFocus();
                return;
            }

            buttons(false);
            if(uriImage != null) {
                String postID = random();
                StorageReference ref = storageReference.child(postID + getFileExtension(uriImage));
                ref.putFile(uriImage)
                        .addOnSuccessListener(taskSnapshot -> {
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {

                                UserPosts userPosts = new UserPosts(details.getName(), postID, 0, String.valueOf(details.getClubImageURL()), String.valueOf(uri), captions, String.valueOf(System.currentTimeMillis()), user.getUid());
                                controller.addPost(1, userPosts, true, response -> {
                                    if(response) {
                                        promptTextview.setText("Post uploaded successfully");
                                        imageView.setImageResource(R.drawable.baseline_cloud_done_24);
                                        toast.show();
                                        finish();
                                    } else {
                                        promptTextview.setText("Post upload unsuccessful");
                                        imageView.setImageResource(R.drawable.baseline_error_24);
                                        toast.show();
                                    }
                                    buttons(true);
                                });
                            });
                        })
                        .addOnFailureListener(e -> {
                            promptTextview.setText("Post upload unsuccessful");
                            imageView.setImageResource(R.drawable.baseline_error_24);
                            toast.show();
                            buttons(true);
                        });
            } else {
                promptTextview.setText("Please select an image");
                imageView.setImageResource(R.drawable.baseline_error_24);
                toast.show();
                buttons(true);
            }
        });

    }

    private void buttons(boolean status) {
        backbtn.setEnabled(status);
        uploadUser.setEnabled(status);
        uploadClub.setEnabled(status);
        if(status) {
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
            darkBackground.setVisibility(View.INVISIBLE);
        } else {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            darkBackground.setVisibility(View.VISIBLE);
        }
    }
    private String random() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == -1) {
            String result = data.getStringExtra("result");
            Uri resultUri = null;
            if(result != null) {
                resultUri = Uri.parse(result);
                uriImage = resultUri;
                image.setImageURI(uriImage);
                Picasso.get().load(uriImage).into(image);
            }
        }
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uriImage));
    }

}