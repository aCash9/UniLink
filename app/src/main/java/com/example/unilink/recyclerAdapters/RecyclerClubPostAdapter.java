package com.example.unilink.recyclerAdapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unilink.activity.events.ClubEvents;
import com.example.unilink.activity.events.EventsActivity;
import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.objects.UserPosts;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerClubPostAdapter extends RecyclerView.Adapter<RecyclerClubPostAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<UserPosts> list;
    private final FirebaseController controller;
    private final FirebaseUser user;
    private boolean liked = false;

    public RecyclerClubPostAdapter(Context context, ArrayList<UserPosts> list) {
        this.context = context;
        this.list = list;
        controller = new FirebaseController();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerClubPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.club_post_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerClubPostAdapter.ViewHolder holder, int position) {
        if(list.get(position).getUserImageURL().isEmpty()) {
            holder.user_image.setImageResource(R.drawable.user_profile_standard);
        } else {
            Picasso.get().load(list.get(position).getUserImageURL()).into(holder.user_image);
        }
        Picasso.get().load(list.get(position).getPostImageURL()).into(holder.image);

        holder.username.setText(list.get(position).getUsername());
        String likes = String.valueOf(list.get(position).getLike());
        holder.likes_counter.setText(likes);
        holder.caption.setText(list.get(position).getCaption());

        String time = TimeAgo.Companion.using(Long.parseLong(list.get(position).getTimestamp()));
        holder.timestamp.setText(time);


        controller.checkIfAlreadyLiked(1, list.get(position).getPostID(), user.getUid(), response -> {
           if(response) {
               holder.like.setImageResource(R.drawable.living_filled_red);
                liked = true;
           }
        });

        holder.username.setOnClickListener(v -> {
           Intent intent = new Intent(context, ClubEvents.class);
           intent.putExtra("club_id", list.get(position).getUserUID());
           context.startActivity(intent);
        });

        holder.like.setOnClickListener(v -> {
            if(liked) {
                controller.PostLikeOperation(false, 1, list.get(position).getPostID(), user.getUid());
                controller.removeLikeToUserPost(list.get(position));
                holder.like.setImageResource(R.drawable.living_filled_white);
                holder.likes_counter.setText(String.valueOf(list.get(position).getLike() - 1));
                controller.changeLike(false, 1, list.get(position).getPostID());
            } else {
                controller.PostLikeOperation(true, 1, list.get(position).getPostID(), user.getUid());
                controller.addLikeToUserPost(list.get(position));
                holder.like.setImageResource(R.drawable.living_filled_red);
                holder.likes_counter.setText(String.valueOf(list.get(position).getLike() + 1));
                controller.changeLike(true, 1, list.get(position).getPostID());
            }
        });

        holder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, list.get(position).getPostImageURL());

            context.startActivity(Intent.createChooser(shareIntent, "Share Image URL"));
        });


        holder.event.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClubEvents.class);
            intent.putExtra("clubUID", list.get(position).getUserUID());
            startActivity(context, intent, null);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image, image;
        TextView username, likes_counter, timestamp, caption;
        ImageButton like, share, event;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.userImage);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            likes_counter = itemView.findViewById(R.id.likecounter);
            timestamp = itemView.findViewById(R.id.timestamp);
            caption = itemView.findViewById(R.id.caption);
            like = itemView.findViewById(R.id.like);
            share = itemView.findViewById(R.id.share);
            event = itemView.findViewById(R.id.event);
        }
    }
}
