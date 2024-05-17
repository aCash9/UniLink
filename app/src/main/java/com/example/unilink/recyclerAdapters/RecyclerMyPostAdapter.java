package com.example.unilink.recyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.objects.UserPosts;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerMyPostAdapter extends RecyclerView.Adapter<RecyclerMyPostAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<UserPosts> list;
    private final FirebaseController controller;
    private final FirebaseUser user;


    public RecyclerMyPostAdapter(Context context, ArrayList<UserPosts> list) {
        this.context = context;
        this.list = list;
        controller = new FirebaseController();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerMyPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mypost_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerMyPostAdapter.ViewHolder holder, int position) {
        Picasso.get().load(list.get(position).getPostImageURL()).into(holder.image);
        String likes = String.valueOf(list.get(position).getLike());
        holder.likes_counter.setText(likes);
        holder.caption.setText(list.get(position).getCaption());

        String time = TimeAgo.Companion.using(Long.parseLong(list.get(position).getTimestamp()));
        holder.timestamp.setText(time);


        controller.checkIfAlreadyLiked(0, list.get(position).getPostID(), user.getUid(), response -> {
           if(response) {
               holder.like.setImageResource(R.drawable.living_filled_red);
               holder.like.setEnabled(false);
           }
        });

        holder.like.setOnClickListener(v -> {
            controller.addLikeToPost(0, list.get(position).getPostID(), user.getUid());
            controller.addLikeToUserPost(list.get(position));
            holder.like.setImageResource(R.drawable.living_filled_red);
            holder.like.setEnabled(false);
            holder.likes_counter.setText(String.valueOf(list.get(position).getLike() + 1));
            controller.incrementLike(0, list.get(position).getPostID());
        });

        holder.delete.setOnClickListener(v -> {
            controller.deleteUserPost(list.get(position).getPostID(), list.get(position).getPostImageURL(), response -> {
                list.remove(position);
                notifyItemRemoved(position);
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView likes_counter, timestamp, caption;
        ImageButton like, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            likes_counter = itemView.findViewById(R.id.likecounter);
            timestamp = itemView.findViewById(R.id.timestamp);
            caption = itemView.findViewById(R.id.caption);
            like = itemView.findViewById(R.id.like);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
