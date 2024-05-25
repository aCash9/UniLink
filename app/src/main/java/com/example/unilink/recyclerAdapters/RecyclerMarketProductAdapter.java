package com.example.unilink.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.unilink.R;
import com.example.unilink.activity.ProfileActivity;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.Product;
import com.example.unilink.objects.UserPosts;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class RecyclerMarketProductAdapter extends RecyclerView.Adapter<RecyclerMarketProductAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Product> list;
    private final FirebaseController controller;
    private final FirebaseUser user;
    private boolean saved;

    public RecyclerMarketProductAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
        controller = new FirebaseController();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerMarketProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerMarketProductAdapter.ViewHolder holder, int position) {
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

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.save.setImageResource(R.drawable.save_filled_icon);
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageSlider image;
        TextView amount, timestamp;
        ImageButton save, info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            timestamp = itemView.findViewById(R.id.timestamp);
            amount = itemView.findViewById(R.id.amount);
            save = itemView.findViewById(R.id.save);
            info = itemView.findViewById(R.id.info);
        }

    }
}
