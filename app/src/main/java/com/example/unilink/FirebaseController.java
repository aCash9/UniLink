package com.example.unilink;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseController {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userCol = db.collection("users");
    private final CollectionReference postCol = db.collection("posts");
    private final CollectionReference clubPostCol = db.collection("clubPosts");
    private final CollectionReference userPostCol = db.collection("userPosts");
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void setUserData(UserProfile profile, booleanCallback callback) {
        db.enableNetwork();
        FirebaseUser user = auth.getCurrentUser();
        //store user data in the cloud firestore database
        DocumentReference docRef = userCol.document(user.getUid());
        docRef.set(profile)
                .addOnCompleteListener(task -> {
                    callback.response(task.isSuccessful());
                });
    }

    public void getUserData(userProfileCallback callback) {
        db.enableNetwork();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference docRef = userCol.document(user.getUid());

        docRef.get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       DocumentSnapshot document = task.getResult();
                       if(document != null) {
                           UserProfile profile = document.toObject(UserProfile.class);
                           callback.onProfileLoaded(profile);
                       } else {
                           UserProfile profile = new UserProfile("Error", "Error", "", "");
                           callback.onProfileLoaded(profile);
                       }
                   }
                });
    }

    public void checkIfUsernameExists(String username, booleanCallback callback) {
        db.enableNetwork();
        userCol.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        callback.response(!task.getResult().isEmpty());
                    }
                });
    }

    public void addPost(int code, UserPosts post, booleanCallback callback) {
        DocumentReference docRef;
        if(code == 0) {
            docRef = postCol.document(post.getPostID());
            addToUserPost(post);
        } else {
            docRef = clubPostCol.document(post.getPostID());
        }
        docRef.set(post)
                .addOnCompleteListener(task -> {
                    callback.response(task.isSuccessful());
                });
    }

    private void addToUserPost(UserPosts post) {
        DocumentReference docRef = userPostCol.document(post.getUsername()).collection("posts").document(post.getPostID());
        docRef.set(post);
    }


    public void getUserPosts(UserPostsCallback callback) {
        ArrayList<UserPosts> list = new ArrayList<>();
        postCol.orderBy("timestamp", Query.Direction.DESCENDING) // Assuming timestamp is the field name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            UserPosts post = document.toObject(UserPosts.class);
                            list.add(post);
                        }
                    }
                    callback.onPostsLoaded(list);
                });
    }

    public void getClubPosts(UserPostsCallback callback) {
        ArrayList<UserPosts> list = new ArrayList<>();
        clubPostCol.orderBy("timestamp", Query.Direction.DESCENDING) // Assuming timestamp is the field name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            UserPosts post = document.toObject(UserPosts.class);
                            list.add(post);
                        }
                    }
                    callback.onPostsLoaded(list);
                });
    }

    public void incrementLike(int code,String postID) {
        DocumentReference docRef;
        if(code == 0) {
            docRef = postCol.document(postID);
        } else {
            docRef = clubPostCol.document(postID);
        }
        docRef.update("like", FieldValue.increment(1));
    }

    public void addLikeToPost(int code, String postID, String uid) {

        Map<String, Object> likeData = new HashMap<>();
        likeData.put("userId", uid);
        likeData.put("timestamp", FieldValue.serverTimestamp());
        DocumentReference docRef;
        if(code == 0){
            docRef = postCol.document(postID).collection("likes").document(uid);
        } else {
            docRef = clubPostCol.document(postID).collection("likes").document(uid);
        }
        docRef.set(likeData)
                .addOnSuccessListener(aVoid -> {
                    // Like added successfully
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }


    public void checkIfAlreadyLiked(int code, String postID, String uid, booleanCallback callback) {
        DocumentReference docRef;
        if(code == 0) {
            docRef = postCol.document(postID).collection("likes").document(uid);
        } else {
            docRef = clubPostCol.document(postID).collection("likes").document(uid);
        }

        docRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        boolean exists = task.getResult().exists();
                        callback.response(exists);
                    } else {
                        callback.response(false);
                    }
                });

    }

    public void verifyIfPartOfAnyClub(ClubDetailsCallback callback){
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference docRef = userCol.document(user.getUid());

        docRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null) {
                            String clubCode = document.getString("clubCode");
                            if(!clubCode.isEmpty()) {
                                DocumentReference clubDocRef = db.collection("clubs").document(clubCode);
                                clubDocRef.get()
                                        .addOnCompleteListener(task1 -> {
                                            if(task.isSuccessful()) {
                                                DocumentSnapshot clubDocument = task1.getResult();
                                                if(clubDocument != null) {
                                                    ClubDetails clubDetails = clubDocument.toObject(ClubDetails.class);
                                                    callback.onClubDetails(clubDetails);
                                                } else {
                                                    callback.onClubDetails(null);
                                                }
                                            } else {
                                                callback.onClubDetails(null);
                                            }
                                        });
                            } else {
                                callback.onClubDetails(null);
                            }
                        }
                    } else {
                        callback.onClubDetails(null);
                    }
                });
    }

    public void addLikeToUserPost(UserPosts userPosts) {
        DocumentReference docRef = userPostCol.document(userPosts.getUsername()).collection("posts").document(userPosts.getPostID());
        docRef.update("like", FieldValue.increment(1));
    }


    public void getMyPosts(UserPostsCallback callback) {
        ArrayList<UserPosts> posts = new ArrayList<>();
        FirebaseUser user = auth.getCurrentUser();

        getUserData(userProfile -> {
            if(userProfile != null) {
                String username = userProfile.getUsername();
                userPostCol.document(username).collection("posts")
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                if(!task1.getResult().isEmpty()) {
                                    for(DocumentSnapshot document : task1.getResult()) {
                                        UserPosts post = document.toObject(UserPosts.class);
                                        posts.add(post);
                                    }
                                }
                                callback.onPostsLoaded(posts);
                            } else {
                                callback.onPostsLoaded(posts);
                            }
                        });
            } else {
                callback.onPostsLoaded(posts);
            }
        });
    }

    public void deleteUserPost(String postID, booleanCallback callback) {
        DocumentReference docRef;
        deleteFromPosts(postID);
        docRef = userPostCol.document(auth.getCurrentUser().getUid()).collection("posts").document(postID);
        docRef.delete()
                .addOnCompleteListener(task -> {
                    callback.response(task.isSuccessful());
                });
    }

    private void deleteFromPosts(String postID) {
        DocumentReference docRef = postCol.document(postID);
        docRef.delete();
    }

}
