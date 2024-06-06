package com.example.unilink.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.unilink.R;
import com.example.unilink.callback.UrlListCallback;
import com.example.unilink.callback.booleanCallback;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    private SoftInputAssist softInputAssist;
    private ImageSlider imageSlider;
    private EditText amount, description, contact_info;
    private Button upload;
    private ArrayList<Uri> imagesUri;
    private View darkBackground;
    private ImageButton back;

    private FirebaseController controller;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        softInputAssist = new SoftInputAssist(this);

        imagesUri = new ArrayList<>();
        imageSlider = findViewById(R.id.image);
        amount = findViewById(R.id.amount);
        description = findViewById(R.id.description);
        upload = findViewById(R.id.upload);
        back = findViewById(R.id.back_btn);
        contact_info = findViewById(R.id.contact_info);
        darkBackground = findViewById(R.id.darkBackground);
        controller = new FirebaseController();
        lottieAnimationView = findViewById(R.id.animationView);

        back.setOnClickListener(v -> finish());

        Toast toast = new Toast(this);
        View view = getLayoutInflater().inflate(R.layout.custom_toast_box, findViewById(R.id.viewContainer));
        toast.setView(view);
        TextView promptTextview = view.findViewById(R.id.promptText);
        ImageView imageView = view.findViewById(R.id.action_image);
        toast.setDuration(Toast.LENGTH_LONG);

        loading(false);
        upload.setOnClickListener(v -> {
            String amt = amount.getText().toString();
            String descr = description.getText().toString();
            String contact = contact_info.getText().toString();

            if(checkEditTextFields(amt, descr, contact) || imagesUri.isEmpty()) {
                return;
            }
            loading(true);


            if(amount.isFocused() || description.isFocused() || contact_info.isFocused()) {
                collapseKeyboard();
            }

            controller.uploadPhotosAndGenerateUrls(imagesUri, list -> {
                Product product = new Product(random(), amt, contact, descr, list, String.valueOf(System.currentTimeMillis()), FirebaseAuth.getInstance().getCurrentUser().getUid());
                controller.addProduct(product, response -> {
                    if(response) {
                        promptTextview.setText("Product added successfully");
                        imageView.setImageResource(R.drawable.baseline_cloud_done_24);
                        toast.show();
                        loading(false);
                        finish();
                    } else {
                        promptTextview.setText("Something went wrong");
                        imageView.setImageResource(R.drawable.baseline_error_24);
                        toast.show();
                        loading(false);
                    }
                });
            });
            amount.setText("");
            description.setText("");
            contact_info.setText("");
        });

        imageSlider.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, 1);
        });
    }

    private String random() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "").substring(0, 10);
    }
    private boolean checkEditTextFields(String amounts, String descr, String contact) {
        if (amounts.isEmpty()) {
            amount.requestFocus();
            amount.setError("This field can't be empty");
            return true;
        }
        if (descr.isEmpty()) {
            description.requestFocus();
            description.setError("This field can't be empty");
            return true;
        }
        if (contact.isEmpty()) {
            contact_info.requestFocus();
            contact_info.setError("This field can't be empty");
            return true;
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            if(data != null) {
                if(data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for(int i = 0; i < count; i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        imagesUri.add(uri);
                    }
                    ArrayList<SlideModel> slideModels = new ArrayList<>();
                    for(Uri url: imagesUri) {
                        slideModels.add(new SlideModel(url.toString(), ScaleTypes.FIT));
                    }
                    imageSlider.setImageList(slideModels);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        softInputAssist.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        softInputAssist.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        softInputAssist.onPause();
        super.onPause();
    }

    private void loading(boolean loading){
        if(loading) {
            darkBackground.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation();
            back.setEnabled(false);
            upload.setEnabled(false);
        } else {
            darkBackground.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
            lottieAnimationView.setVisibility(View.GONE);
            back.setEnabled(true);
            upload.setEnabled(true);
        }
    }
    private void collapseKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Objects.requireNonNull(this.getCurrentFocus()).getWindowToken(), 0);
    }
}