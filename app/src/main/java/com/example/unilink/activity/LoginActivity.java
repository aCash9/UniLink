package com.example.unilink.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.unilink.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;

    private ImageView graphic;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        ImageButton back = findViewById(R.id.backButton);
        graphic = findViewById(R.id.graphic);
        Button loginbtn = findViewById(R.id.loginbtn);
        Button forgotPassword = findViewById(R.id.forgotPassword);

        forgotPassword.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String email = emailEditText.getText().toString();
            if (email.isEmpty()) {
                emailEditText.requestFocus();
                emailEditText.setError("This field can't be empty");
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!check(email)) {
                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(this);
                View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
                toast.setView(view);
                TextView promptTextview = view.findViewById(R.id.promptText);
                ImageView imageView = view.findViewById(R.id.action_image);
                toast.setDuration(Toast.LENGTH_LONG);
                promptTextview.setText("Invalid College Email Address");
                imageView.setImageResource(R.drawable.baseline_error_24);
                emailEditText.setText("");
                toast.show();
                passwordEditText.setText("");
                progressBar.setVisibility(View.GONE);
                return;
            }

            progressBar.setVisibility(View.GONE);
            Toast toast = new Toast(this);
            View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
            toast.setView(view);
            TextView promptTextview = view.findViewById(R.id.promptText);
            ImageView imageView = view.findViewById(R.id.action_image);
            toast.setDuration(Toast.LENGTH_LONG);

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            promptTextview.setText("Password Reset Email Sent");
                            imageView.setImageResource(R.drawable.baseline_cloud_done_24);
                            toast.show();
                        } else {
                            promptTextview.setText("Error Sending Password Reset Email");
                            imageView.setImageResource(R.drawable.baseline_error_24);
                            toast.show();
                        }
                    });
        });

        final boolean[] flag = {false};
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !flag[0]) {
                animateAndHideGraphic();
                flag[0] = true;
            }
        });

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !flag[0]) {
                animateAndHideGraphic();
                flag[0] = true;
            }
        });

        loginbtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (email.isEmpty()) {
                emailEditText.requestFocus();
                emailEditText.setError("This field can't be empty");
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!check(email)) {

                progressBar.setVisibility(View.GONE);
                Toast toast = new Toast(this);
                View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
                toast.setView(view);
                TextView promptTextview = view.findViewById(R.id.promptText);
                ImageView imageView = view.findViewById(R.id.action_image);
                toast.setDuration(Toast.LENGTH_LONG);
                promptTextview.setText("Invalid College Email Address");
                imageView.setImageResource(R.drawable.baseline_error_24);
                emailEditText.setText("");
                toast.show();
                passwordEditText.setText("");
                progressBar.setVisibility(View.GONE);
                return;
            }

            if(password.isEmpty()) {
                passwordEditText.requestFocus();
                passwordEditText.setError("This field can't be empty");
                progressBar.setVisibility(View.GONE);
                return;
            }
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        Toast toast = new Toast(this);
                        View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
                        toast.setView(view);
                        TextView promptTextview = view.findViewById(R.id.promptText);
                        ImageView imageView = view.findViewById(R.id.action_image);
                        imageView.setImageResource(R.drawable.baseline_cloud_done_24);
                        toast.setDuration(Toast.LENGTH_LONG);
                        if(task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(this, HomeActivity.class);
                                startActivity(intent);
                            } else {
                                promptTextview.setText("Email not verified");
                                emailEditText.setText("");
                                passwordEditText.setText("");
                                progressBar.setVisibility(View.GONE);
                            }

                        } else {
                            promptTextview.setText("Invalid credentials");
                            emailEditText.setText("");
                            passwordEditText.setText("");
                            progressBar.setVisibility(View.GONE);
                        }
                        toast.show();
                    });
        });
        back.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean check(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@bmsit\\.in$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void animateAndHideGraphic() {
        AnimationSet animationSet = getAnimationSet();

        // Set animation listener to hide the graphic when animation ends
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                graphic.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        graphic.startAnimation(animationSet);
    }
    @NonNull
    private static AnimationSet getAnimationSet() {
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(600); // Set duration in milliseconds

        // Create upward translation animation
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1.0f); // Move upward by 100%

        translateAnimation.setDuration(600); // Set duration in milliseconds

        // Combine both animations
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeOutAnimation);
        animationSet.addAnimation(translateAnimation);
        return animationSet;
    }
}