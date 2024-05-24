package com.example.unilink.recyclerAdapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unilink.firebase.FirebaseController;
import com.example.unilink.R;
import com.example.unilink.callback.OnVideoPreparedListener;
import com.example.unilink.objects.ExoPlayerItem;
import com.example.unilink.objects.UserPosts;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserReelsAdapter extends RecyclerView.Adapter<UserReelsAdapter.VideoViewHolder>{

    private final Context context;
    private final ArrayList<UserPosts> videos;
    private final OnVideoPreparedListener videoPreparedListener;
    int flag = 0;
    private boolean liked = false;

    private final FirebaseController controller;
    private final FirebaseUser user;
    public UserReelsAdapter(Context context, ArrayList<UserPosts> videos, OnVideoPreparedListener videoPreparedListener) {
        this.context = context;
        this.videos = videos;
        this.videoPreparedListener = videoPreparedListener;
        controller = new FirebaseController();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @NonNull
    @Override
    public UserReelsAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reels_viewpager, parent, false);
        return new VideoViewHolder(v, context, videoPreparedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReelsAdapter.VideoViewHolder holder, int position) {
        UserPosts post = videos.get(position);
        holder.setVideoPath(post.getPostImageURL());
        Picasso.get().load(videos.get(position).getUserImageURL()).into(holder.profile_pic);
        holder.username.setText(videos.get(position).getUsername());
        String likes = String.valueOf(videos.get(position).getLike());
        holder.like_counter.setText(likes);
        holder.caption.setText(videos.get(position).getCaption());

        holder.sound.setOnClickListener(v -> {
            if(flag == 0) {
                holder.muteVideo(true);
                holder.sound.setImageResource(R.drawable.mute);
                flag = 1;
            }
            else {
                holder.muteVideo(true);
                holder.sound.setImageResource(R.drawable.sound);
                flag = 0;
            }
        });

        controller.checkIfAlreadyLiked(2, videos.get(position).getPostID(), user.getUid(), response -> {
            if(response) {
                holder.like.setImageResource(R.drawable.living_filled_red);
                liked = true;
            }
        });

        holder.like.setOnClickListener(v -> {
            if(liked) {
                controller.PostLikeOperation(false,2, videos.get(position).getPostID(), user.getUid());
                controller.removeLikeToUserPost(videos.get(position));
                holder.like.setImageResource(R.drawable.living_filled_white);
                holder.like_counter.setText(String.valueOf(videos.get(position).getLike() - 1));
                controller.changeLike(false,2, videos.get(position).getPostID());
            } else {
                controller.PostLikeOperation(true,2, videos.get(position).getPostID(), user.getUid());
                controller.addLikeToUserPost(videos.get(position));
                holder.like.setImageResource(R.drawable.living_filled_red);
                holder.like_counter.setText(String.valueOf(videos.get(position).getLike() + 1));
                controller.changeLike(true,2, videos.get(position).getPostID());
            }
        });

        holder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, videos.get(position).getPostImageURL());

            context.startActivity(Intent.createChooser(shareIntent, "Share Image URL"));
        });

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private ExoPlayer exoPlayer;
        private final OnVideoPreparedListener videoPreparedListener;
        private final Context context;

        private final ImageButton sound;
        private final ImageButton like;
        private final ImageButton share;
        private final TextView like_counter;
        private final TextView caption;
        private final TextView username;
        private final ImageView profile_pic;
        private final PlayerView video;
        private final ProgressBar pbLoading;

        public VideoViewHolder(@NonNull View itemView, Context context, OnVideoPreparedListener videoPreparedListener) {
            super(itemView);
            this.context = context;
            this.videoPreparedListener = videoPreparedListener;
            video = itemView.findViewById(R.id.playerView);
            pbLoading = itemView.findViewById(R.id.pbLoading);
            like = itemView.findViewById(R.id.like);
            share = itemView.findViewById(R.id.share);
            sound = itemView.findViewById(R.id.sound);
            like_counter = itemView.findViewById(R.id.likecounter);
            profile_pic = itemView.findViewById(R.id.userImage);
            caption = itemView.findViewById(R.id.caption);
            username = itemView.findViewById(R.id.username);
        }

        public void muteVideo(boolean status) {
            if(status) {
                exoPlayer.setVolume(0f);
                sound.setImageResource(R.drawable.mute);
            }
            else {
                exoPlayer.setVolume(1f);
            }
        }

        public void setVideoPath(String url) {
            exoPlayer = new ExoPlayer.Builder(context).build();
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(@NonNull PlaybackException error) {
                    Player.Listener.super.onPlayerError(error);
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    Player.Listener.super.onPlaybackStateChanged(state);
                    if (state == Player.STATE_BUFFERING) {
                        pbLoading.setVisibility(View.VISIBLE);
                    } else if (state == Player.STATE_READY) {
                        pbLoading.setVisibility(View.GONE);
                    }
                }
            });

            video.setPlayer(exoPlayer);
            video.setUseController(false);
            exoPlayer.seekTo(0);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            exoPlayer.setVolume(0f);

            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);

            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));

            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.prepare();
            if (getAbsoluteAdapterPosition() == 0) {
                exoPlayer.setPlayWhenReady(true);
                exoPlayer.play();
            }
            videoPreparedListener.onVideoPrepared(new ExoPlayerItem(exoPlayer, getAbsoluteAdapterPosition()));
        }
    }
}
