package com.example.unilink.objects;

import com.google.android.exoplayer2.ExoPlayer;

public class ExoPlayerItem {
    ExoPlayer player;
    int position;

    public ExoPlayerItem(ExoPlayer player, int position) {
        this.player = player;
        this.position = position;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ExoPlayer player) {
        this.player = player;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
