package com.example.unilink.recyclerAdapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unilink.R;
import com.example.unilink.RoomDatabase.objects.ClubsInfo;
import com.example.unilink.activity.events.ClubEvents;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.objects.ClubEventCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerClubsAdapter<T> extends RecyclerView.Adapter<RecyclerClubsAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<T> list;

    public RecyclerClubsAdapter(Context context, ArrayList<T> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerClubsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.club_rows, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerClubsAdapter.ViewHolder holder, int position) {
        T item = list.get(position);
        if (item instanceof ClubEventCard) {
            ClubEventCard card = (ClubEventCard) item;

            if(card.getBannerURL() != null) {
                Picasso.get().load(card.getBannerURL()).into(holder.user_image);
            }

            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ClubEvents.class);
                intent.putExtra("clubUID", card.getClubUID());
                context.startActivity(intent);
            });
        } else {
            ClubsInfo card = (ClubsInfo) item;

            Glide.with(context)
                    .load(card.getBannerURL())
                    .into(holder.user_image);

            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ClubEvents.class);
                intent.putExtra("clubUID", card.getClubUID());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.userImage);
            cardView = itemView.findViewById(R.id.cardView);
        }

    }
}
