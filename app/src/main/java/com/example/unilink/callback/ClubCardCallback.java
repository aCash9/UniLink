package com.example.unilink.callback;

import com.example.unilink.objects.ClubEventCard;

import java.util.ArrayList;

public interface ClubCardCallback {
    void onCardReceived(ArrayList<ClubEventCard> list);
}
