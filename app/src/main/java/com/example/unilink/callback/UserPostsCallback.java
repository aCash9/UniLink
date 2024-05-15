package com.example.unilink.callback;

import com.example.unilink.objects.UserPosts;

import java.util.ArrayList;

public interface UserPostsCallback {
    void onPostsLoaded(ArrayList<UserPosts> posts);
}
