package com.example.unilink;

import java.util.ArrayList;
import java.util.List;

public interface UserPostsCallback {
    void onPostsLoaded(ArrayList<UserPosts> posts);
}
