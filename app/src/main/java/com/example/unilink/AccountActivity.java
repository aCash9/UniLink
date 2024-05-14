package com.example.unilink;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {

    //add private field to all the variables

    private ImageView userImage;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference storageReference;
    private TextView chooseImage, updateImage;
    private Uri uriImage;
    private ImageButton backBtn;

    private Button saveChanges;

    private EditText name, email, username;

    private LottieAnimationView lottieAnimationView;
    private View darkBackground;
    private FirebaseController controller;
    private String originalUsername;
    private UserProfile userProfile;
    ActivityResultLauncher<String> mGETcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userImage = findViewById(R.id.userImage);
        chooseImage = findViewById(R.id.chooseImage);
        updateImage = findViewById(R.id.updateImage);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(v -> finish());

        saveChanges = findViewById(R.id.saveChanges);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        lottieAnimationView = findViewById(R.id.animationView);
        darkBackground = findViewById(R.id.darkBackground);

        controller = new FirebaseController();



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        buttons(false);


        storageReference = FirebaseStorage.getInstance().getReference("UserDisplayImages");
        Uri uri = user.getPhotoUrl();

        //loading the user image from firebase storage
        if(uri != null) {
            lottieAnimationView.playAnimation();
            Picasso.get().load(uri).into(userImage, new Callback() {
                @Override
                public void onSuccess() {
                    controller.getUserData(userProfile -> {
                        name.setText(userProfile.getName());
                        email.setText(userProfile.getEmail());
                        username.setText(userProfile.getUsername());
                        originalUsername = userProfile.getUsername();
                        userProfile = userProfile;
                        buttons(true);
                    });
                }
                @Override
                public void onError(Exception e) {

                }
            });
        } else {
            controller.getUserData(userProfile -> {
                name.setText(userProfile.getName());
                email.setText(userProfile.getEmail());
                originalUsername = userProfile.getUsername();
                username.setText(userProfile.getUsername());
                buttons(true);
            });
        }


        chooseImage.setOnClickListener(v -> {
            mGETcontent.launch("image/*");
        });

        mGETcontent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                Intent intent = new Intent(AccountActivity.this, CropperActivity.class);
                if(o != null) {
                    intent.putExtra("data", o.toString());
                    startActivityForResult(intent, 101);
                }
            }
        });


        Toast toast = new Toast(this);
        View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
        toast.setView(view);
        TextView promptTextview = view.findViewById(R.id.promptText);
        ImageView imageView = view.findViewById(R.id.action_image);
        toast.setDuration(Toast.LENGTH_LONG);

        updateImage.setOnClickListener(v -> {
            buttons(false);
            if(uriImage != null) {
                StorageReference ref = storageReference.child(user.getUid() + getFileExtension(uriImage));

                ref.putFile(uriImage)
                        .addOnSuccessListener(taskSnapshot -> {
                            ref.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                Uri downloadUri = uri1;
                                user = auth.getCurrentUser();

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(downloadUri).build();

                                user.updateProfile(profileChangeRequest);
                                buttons(true);
                            });
                        })
                        .addOnFailureListener(e -> {
                            promptTextview.setText("Image updation failed");
                            imageView.setImageResource(R.drawable.baseline_error_24);
                            toast.show();
                            buttons(true);
                        });
            }
        });

        saveChanges.setOnClickListener(v -> {
            buttons(false);
            String nameText = name.getText().toString();
            String emailText = email.getText().toString();
            String usernameText = username.getText().toString();

            if(nameText.isEmpty() || emailText.isEmpty() || usernameText.isEmpty()) {
                promptTextview.setText("Please fill in all the fields");
                imageView.setImageResource(R.drawable.baseline_error_24);
                toast.show();
                return;
            }
            controller.checkIfUsernameExists(usernameText, response -> {
                if(originalUsername.equals(usernameText) && response) {
                    response = false;
                }

                UserProfile profile = new UserProfile(nameText, emailText, usernameText, userProfile.getClubCode());
                if(!response) {
                    controller.setUserData(profile, response1 -> {
                        if(response1) {
                            buttons(true);
                        } else {
                            promptTextview.setText("Details updation failed");
                            imageView.setImageResource(R.drawable.baseline_error_24);
                            toast.show();
                            buttons(true);
                        }
                    });
                } else {
                    promptTextview.setText("Username already taken");
                    imageView.setImageResource(R.drawable.baseline_error_24);
                    toast.show();
                    buttons(true);
                }
            });

        });
    }

    private void buttons(boolean status) {
        backBtn.setEnabled(status);
        saveChanges.setEnabled(status);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == -1) {
            String result = data.getStringExtra("result");
            Uri resultUri = null;
            if(result != null) {
                resultUri = Uri.parse(result);
                uriImage = resultUri;
                userImage.setImageURI(uriImage);
                Picasso.get().load(uriImage).into(userImage);
            }
        }
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uriImage));
    }
}