package com.example.unilink;

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

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecyclerClubPostAdapter extends RecyclerView.Adapter<RecyclerClubPostAdapter.ViewHolder> {
    Context context;
    ArrayList<UserPosts> list;
    FirebaseController controller;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public RecyclerClubPostAdapter(Context context, ArrayList<UserPosts> list) {
        this.context = context;
        this.list = list;
        controller = new FirebaseController();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerClubPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_row, parent, false);
        RecyclerClubPostAdapter.ViewHolder viewHolder = new RecyclerClubPostAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerClubPostAdapter.ViewHolder holder, int position) {
        Picasso.get().load(list.get(position).getUserImageURL()).into(holder.user_image);
        Picasso.get().load(list.get(position).getPostImageURL()).into(holder.image);

        holder.username.setText(list.get(position).getUsername());
        String likes = String.valueOf(list.get(position).getLike());
        holder.likescounter.setText(likes);
        holder.caption.setText(list.get(position).getCaption());

        String time = TimeAgo.Companion.using(Long.parseLong(list.get(position).getTimestamp()));
        holder.timestamp.setText(time);


        controller.checkIfAlreadyLiked(1, list.get(position).getPostID(), user.getUid(), response -> {
           if(response) {
               holder.like.setImageResource(R.drawable.living_filled_red);
               holder.like.setEnabled(false);
           }
        });

        holder.like.setOnClickListener(v -> {
            controller.addLikeToPost(1, list.get(position).getPostID(), user.getUid());
            holder.like.setImageResource(R.drawable.living_filled_red);
            holder.like.setEnabled(false);
            holder.likescounter.setText(String.valueOf(list.get(position).getLike() + 1));
            controller.incrementLike(1, list.get(position).getPostID());
        });

        holder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, list.get(position).getUserImageURL());

            context.startActivity(Intent.createChooser(shareIntent, "Share Image URL"));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image, image;
        TextView username, likescounter, timestamp, caption;
        ImageButton like, share;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.userImage);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            likescounter = itemView.findViewById(R.id.likecounter);
            timestamp = itemView.findViewById(R.id.timestamp);
            caption = itemView.findViewById(R.id.caption);
            like = itemView.findViewById(R.id.like);
            share = itemView.findViewById(R.id.share);
        }
    }

    public String timeagoFormat(String timestamp) {
        String timestampString = "May 12, 2024 at 8:17:13 PM UTC+5:30";
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a 'UTC'Z", Locale.ENGLISH);
        try {
            Date postDate = format.parse(timestampString);

            long postTimeMillis = postDate.getTime();

            return TimeAgo.Companion.using(postTimeMillis);
        } catch(ParseException e) {
            return "";
        }
    }

}
