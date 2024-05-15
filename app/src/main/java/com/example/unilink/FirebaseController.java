package com.example.unilink;

import com.example.unilink.callback.ClubDetailsCallback;
import com.example.unilink.callback.UserPostsCallback;
import com.example.unilink.callback.booleanCallback;
import com.example.unilink.callback.userProfileCallback;
import com.example.unilink.objects.ClubDetails;
import com.example.unilink.objects.UserPosts;
import com.example.unilink.objects.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseController {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference userCol = db.collection("users");
    private final CollectionReference postCol = db.collection("posts");
    private final CollectionReference reelsCol = db.collection("reels");
    private final CollectionReference clubPostCol = db.collection("clubPosts");
    private final CollectionReference userUploadsCol = db.collection("userUploads");
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
                           UserProfile profile = new UserProfile("Error", "Error", "", "", "", "");
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

    public void addPost(int code, UserPosts post, boolean type, booleanCallback callback) {
        DocumentReference docRef;
        if(type) {
            if(code == 0) {
                docRef = postCol.document(post.getPostID());
                addToUserPost(post, true);
            } else {
                docRef = clubPostCol.document(post.getPostID());
            }
        } else {
            if(code == 0) {
                addToUserPost(post, false);
            }
            docRef = reelsCol.document(post.getPostID());
        }

        docRef.set(post)
                .addOnCompleteListener(task -> {
                    callback.response(task.isSuccessful());
                });
    }

    private void addToUserPost(UserPosts post, boolean type) {
        String col;
        if(type) {
            col = "posts";
        } else {
            col = "reels";
        }
        DocumentReference docRef = userUploadsCol.document(post.getUserUID()).collection(col).document(post.getPostID());
        docRef.set(post);
    }


    public void getPosts(int code, UserPostsCallback callback) {
        ArrayList<UserPosts> list = new ArrayList<>();
        CollectionReference colUsed;
        if(code == 0) {
            colUsed = postCol;
        } else if(code == 1) {
            colUsed = clubPostCol;
        } else {
            colUsed = reelsCol;
        }

        colUsed.orderBy("timestamp", Query.Direction.DESCENDING) // Assuming timestamp is the field name
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
        } else if(code == 1){
            docRef = clubPostCol.document(postID);
        } else {
            docRef = reelsCol.document(postID);
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
        } else if(code == 1){
            docRef = clubPostCol.document(postID).collection("likes").document(uid);
        } else {
            docRef = reelsCol.document(postID).collection("likes").document(uid);
        }
        docRef.set(likeData);
    }

    public void checkIfAlreadyLiked(int code, String postID, String uid, booleanCallback callback) {
        DocumentReference docRef;
        if(code == 0) {
            docRef = postCol.document(postID).collection("likes").document(uid);
        } else if(code == 1) {
            docRef = clubPostCol.document(postID).collection("likes").document(uid);
        } else {
            docRef = reelsCol.document(postID).collection("likes").document(uid);
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
        DocumentReference docRef = userUploadsCol.document(userPosts.getUserUID()).collection("posts").document(userPosts.getPostID());
        docRef.update("like", FieldValue.increment(1));
    }


    public void getMyPosts(String uid, UserPostsCallback callback) {
        ArrayList<UserPosts> posts = new ArrayList<>();

        userUploadsCol.document(uid).collection("posts")
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
    }

    public void deleteUserPost(String postID, booleanCallback callback) {
        DocumentReference docRef;
        deleteFromPosts(postID);
        docRef = userUploadsCol.document(auth.getCurrentUser().getUid()).collection("posts").document(postID);
        docRef.delete()
                .addOnCompleteListener(task -> {
                    callback.response(task.isSuccessful());
                });
    }

    private void deleteFromPosts(String postID) {
        DocumentReference docRef = postCol.document(postID);
        docRef.delete();
    }


    public void findUser(String username, userProfileCallback callback) {
        userCol.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            UserProfile profile = document.toObject(UserProfile.class);
                            callback.onProfileLoaded(profile);
                        }
                        callback.onProfileLoaded(null);
                    }
                });
    }

    public void getUserDataUsingUID(String uid, userProfileCallback callback) {
        db.enableNetwork();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference docRef = userCol.document(uid);

        docRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null) {
                            UserProfile profile = document.toObject(UserProfile.class);
                            callback.onProfileLoaded(profile);
                        } else {
                            UserProfile profile = new UserProfile("Error", "Error", "", "", "", "");
                            callback.onProfileLoaded(profile);
                        }
                    }
                });
    }

    public void updateUserImageURL(String url) {
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference docRef = userCol.document(user.getUid());
        docRef.update("imageURL", url);
    }

}
