package com.example.unilink.recyclerAdapters;

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

import com.example.unilink.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.objects.UserPosts;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPagerReelsAdapter extends RecyclerView.Adapter<ViewPagerReelsAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<UserPosts> list;
    private final FirebaseController controller;
    private final FirebaseUser user;
    private ArrayList<SimpleExoPlayer> players;
    private boolean flag = true;

    public ViewPagerReelsAdapter(Context context, ArrayList<UserPosts> list) {
        this.context = context;
        this.list = list;
        controller = new FirebaseController();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(context != null) {
            players = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
                players.add(player);
            }
        }
    }

    @NonNull
    @Override
    public ViewPagerReelsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reels_viewpager, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerReelsAdapter.ViewHolder holder, int position) {
        if(players != null) {
            holder.video.setPlayer(players.get(position));
            holder.video.setUseController(false);
        }
        Picasso.get().load(list.get(position).getUserImageURL()).into(holder.user_image);
        SimpleExoPlayer player = players.get(position);
        holder.video.setVisibility(View.VISIBLE);
        MediaItem mediaItem = MediaItem.fromUri(list.get(position).getPostImageURL());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.play();

        holder.username.setText(list.get(position).getUsername());
        String likes = String.valueOf(list.get(position).getLike());
        holder.likes_counter.setText(likes);
        holder.caption.setText(list.get(position).getCaption());

        holder.sound.setOnClickListener(v -> {
            if(flag) {
                player.setVolume(0f);
                holder.sound.setImageResource(R.drawable.mute);
            }
            else {
                player.setVolume(1f);
                holder.sound.setImageResource(R.drawable.sound);
            }
            flag = !flag;
        });

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

        holder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, list.get(position).getUserImageURL());

            context.startActivity(Intent.createChooser(shareIntent, "Share Image URL"));
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            SimpleExoPlayer player = players.get(position);
            if (player != null) {
                player.release();
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            SimpleExoPlayer player = players.get(position);
            if (player != null) {
                player.stop();
                player.setVolume(0f);
                holder.video.setPlayer(null); // Remove the player from the PlayerView
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void releasePlayers() {
        for (SimpleExoPlayer player : players) {
            if (player != null) {
                player.stop(); // Stop playback
                player.release(); // Release resources
            }
        }
        players.clear(); // Clear the list of players
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image;
        TextView username, likes_counter, caption;
        ImageButton like, share, sound;

        PlayerView video;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.userImage);
            username = itemView.findViewById(R.id.username);
            likes_counter = itemView.findViewById(R.id.likecounter);
            caption = itemView.findViewById(R.id.caption);
            like = itemView.findViewById(R.id.like);
            share = itemView.findViewById(R.id.share);
            video = itemView.findViewById(R.id.video);
            sound = itemView.findViewById(R.id.sound);

        }

    }

}
