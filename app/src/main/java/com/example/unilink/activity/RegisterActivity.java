package com.example.unilink.activity;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.objects.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private ImageView graphic;
    private LottieAnimationView lottieAnimationView;
    private View darkBackground;
    private ImageButton back;
    private Button signup;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Progress bar

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        EditText nameEditText = findViewById(R.id.name);
        back = findViewById(R.id.backButton);
        graphic = findViewById(R.id.graphic);
        signup = findViewById(R.id.signupbtn);
        lottieAnimationView = findViewById(R.id.animationView);
        darkBackground = findViewById(R.id.darkBackground);
        usernameEditText = findViewById(R.id.username);

        lottieAnimationView.setAnimation(R.raw.register_animation);
        loading(false);

        Handler handler = new Handler();
        Runnable hideGraphic = () -> {
            darkBackground.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.GONE);
            back.setEnabled(true);
            signup.setEnabled(true);
            // Stop the animation
            lottieAnimationView.cancelAnimation();
        };

        final boolean[] flag = {false};
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !flag[0]) {
                animateAndHideGraphic();
                flag[0] = true;
            }
        });

        usernameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !flag[0]) {
                animateAndHideGraphic();
                flag[0] = true;
            }
        });

        nameEditText.setOnFocusChangeListener((v, hasFocus) -> {
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

        back.setOnClickListener(v -> {
            finish();
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        controller = new FirebaseController();
        signup.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            if (checkEditTextFields(email, name, password, username)) {
                return;
            }

            controller.checkIfUsernameExists(username, response1 -> {
                Toast toast = new Toast(this);
                View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
                toast.setView(view);
                TextView promptTextview = view.findViewById(R.id.promptText);
                ImageView imageView = view.findViewById(R.id.action_image);
                toast.setDuration(Toast.LENGTH_LONG);
                if(!response1) {
                    loading(true);
                    UserProfile profile = new UserProfile(name, email, username, "", "", "");
                    collapseKeyboard();

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()) {
                                            FirebaseController controller = new FirebaseController();
                                            profile.setUserUID(auth.getCurrentUser().getUid());
                                            controller.setUserData(profile, response -> {
                                                promptTextview.setText("User Registered Successfully\nPLease verify your email");
                                                imageView.setImageResource(R.drawable.baseline_cloud_done_24);
                                                toast.show();
//                                        loading(false);

                                                handler.postDelayed(hideGraphic, 500);
                                            });
                                        } else {
                                            promptTextview.setText("Email Verification Failed\nPLease Try Again");
                                            imageView.setImageResource(R.drawable.baseline_error_24);
                                            toast.show();
//                                    loading(false);
                                            handler.postDelayed(hideGraphic, 500);

                                        }
                                    });
                                } else {
                                    Exception exception = task.getException();
                                    imageView.setImageResource(R.drawable.baseline_error_24);
                                    if (exception instanceof FirebaseAuthUserCollisionException) {
                                        promptTextview.setText("User Already Registered");
                                    } else {
                                        promptTextview.setText("Registration Failed");
                                    }
                                    toast.show();
//                            loading(false);
                                    handler.postDelayed(hideGraphic, 500);

                                }
                            });
                } else {
                    handler.postDelayed(hideGraphic, 500);
                    promptTextview.setText("Username already exists");
                    imageView.setImageResource(R.drawable.baseline_error_24);
                    toast.show();
                }

                usernameEditText.setText("");
                emailEditText.setText("");
                passwordEditText.setText("");
                nameEditText.setText("");
            });


        });

    }

    private boolean checkEditTextFields(String email, String name, String password, String username) {
        if (email.isEmpty()) {
            emailEditText.requestFocus();
            emailEditText.setError("This field can't be empty");
            return true;
        }

        if (!check(email)) {
            collapseKeyboard();
            Toast toast = new Toast(this);
            View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
            toast.setView(view);
            TextView promptTextview = view.findViewById(R.id.promptText);
            ImageView imageView = view.findViewById(R.id.action_image);
            toast.setDuration(Toast.LENGTH_LONG);
            promptTextview.setText("Invalid College Email Address");
            imageView.setImageResource(R.drawable.baseline_error_24);

            toast.show();
            return true;
        }

        if(name.isEmpty()) {
            passwordEditText.requestFocus();
            passwordEditText.setError("This field can't be empty");
            return true;
        }

        if(password.isEmpty()) {
            passwordEditText.requestFocus();
            passwordEditText.setError("This field can't be empty");
            return true;
        }
        if(password.length() < 6) {
            passwordEditText.requestFocus();
            passwordEditText.setError("Password too small");
            return true;
        }
        if(username.isEmpty()) {
            usernameEditText.requestFocus();
            usernameEditText.setError("This field can't be empty");
            return true;
        }
        return false;
    }

    private void loading(boolean loading){
        if(loading) {
            darkBackground.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            back.setEnabled(false);
            signup.setEnabled(false);
        } else {
            darkBackground.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
            lottieAnimationView.setVisibility(View.GONE);
        }
    }

    private void collapseKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Objects.requireNonNull(this.getCurrentFocus()).getWindowToken(), 0);
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
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                graphic.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // Start animation on the graphic
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