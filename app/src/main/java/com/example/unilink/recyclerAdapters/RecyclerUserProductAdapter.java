package com.example.unilink.recyclerAdapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.unilink.R;
import com.example.unilink.callback.booleanCallback;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerUserProductAdapter extends RecyclerView.Adapter<RecyclerUserProductAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Product> list;
    private final FirebaseController controller;
    private final FirebaseUser user;
    private boolean saved;
    private Dialog dialog;

    public RecyclerUserProductAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
        controller = new FirebaseController();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerUserProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerUserProductAdapter.ViewHolder holder, int position) {
        Product product = list.get(position);

        ArrayList<SlideModel> images = new ArrayList<>();
        for(String url: product.getImages()) {
            images.add(new SlideModel(url, ScaleTypes.FIT));
        }

        holder.image.setImageList(images);

        holder.amount.setText("₹" + product.getAmount());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(new Date(Long.parseLong(product.getTimestamp())));
        holder.timestamp.setText(formattedDate);

        holder.delete.setVisibility(View.VISIBLE);
        holder.delete.setOnClickListener(v -> controller.deleteMyProduct(product, response -> {
            notifyItemRemoved(position);
            list.remove(position);
        }));

        holder.info.setOnClickListener(v -> showDialogBox(product));



    }
    private void showDialogBox(Product product) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.show_product_desc);

        ImageSlider image;
        TextView amount, timestamp, contact_info, description;;

        image = dialog.findViewById(R.id.image);
        amount = dialog.findViewById(R.id.amount);
        timestamp = dialog.findViewById(R.id.timestamp);
        contact_info = dialog.findViewById(R.id.contact);
        description = dialog.findViewById(R.id.descr);

        ArrayList<SlideModel> images = new ArrayList<>();
        for(String url: product.getImages()) {
            images.add(new SlideModel(url, ScaleTypes.FIT));
        }

        image.setImageList(images);
        amount.setText("₹" + product.getAmount());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(new Date(Long.parseLong(product.getTimestamp())));
        timestamp.setText(formattedDate);
        contact_info.setText(product.getContactInfo());
        description.setText(product.getDescription());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageSlider image;
        TextView amount, timestamp;
        ImageButton delete, info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            timestamp = itemView.findViewById(R.id.timestamp);
            amount = itemView.findViewById(R.id.amount);
            delete = itemView.findViewById(R.id.delete);
            info = itemView.findViewById(R.id.info);
        }

    }
}
