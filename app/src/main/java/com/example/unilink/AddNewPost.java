package com.example.unilink;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddNewPost extends AppCompatActivity {

    private ImageButton backbtn;
    private ImageView image;
    private VideoView video;
    private Button uploadUser, uploadClub;
    private Uri uriImage;
    private EditText caption;
    private StorageReference storageReference;

    private UserProfile profile;
    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;
    private View darkBackground;
    private ClubDetails details;
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
        video = findViewById(R.id.video);
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

        backbtn.setOnClickListener(v -> finish());

        image.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
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

                                controller.addPost(0, userPosts, response -> {
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
                                controller.addPost(1, userPosts, response -> {
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
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            String mimeType = getContentResolver().getType(selectedFileUri);

            if (mimeType != null) {
                if (mimeType.startsWith("image/")) {
                    uriImage = data.getData();
                    image.setImageURI(uriImage);
                    Picasso.get().load(uriImage).into(image);
                } else if (mimeType.startsWith("video/")) {
                    // Handle video
                    // Play video or perform operations specific to videos
                    Toast.makeText(this, "Selected video", Toast.LENGTH_SHORT).show();
                } else {
                    // Unsupported file type
                    Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Error getting MIME type
                Toast.makeText(this, "Error getting file type", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            uriImage = data.getData();
//            image.setImageURI(uriImage);
//            Picasso.get().load(uriImage).into(image);
//        }
//    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uriImage));
    }
}