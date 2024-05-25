package com.example.unilink.firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.unilink.callback.ClubCardCallback;
import com.example.unilink.callback.ClubEventCallback;
import com.example.unilink.callback.IntegerCallback;
import com.example.unilink.callback.ProductListCallback;
import com.example.unilink.callback.UpdatesCallback;
import com.example.unilink.callback.UrlListCallback;
import com.example.unilink.callback.UserPostsCallback;
import com.example.unilink.callback.booleanCallback;
import com.example.unilink.callback.userProfileCallback;
import com.example.unilink.objects.ClubEventCard;
import com.example.unilink.objects.Event;
import com.example.unilink.objects.Product;
import com.example.unilink.objects.Updates;
import com.example.unilink.objects.UserPosts;
import com.example.unilink.objects.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseController {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference userCol = db.collection("users");
    private final CollectionReference postCol = db.collection("posts");
    private final CollectionReference reelsCol = db.collection("reels");
    private final CollectionReference clubPostCol = db.collection("clubPosts");
    private final CollectionReference clubInfoCol = db.collection("clubsInfo");
    private final CollectionReference updatesCol = db.collection("updates");
    private final CollectionReference userUploadsCol = db.collection("userUploads");
    private final CollectionReference productsCol = db.collection("products");
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public FirebaseController() {
        db.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build());
    }

    public void setUserData(UserProfile profile, booleanCallback callback) {
        db.enableNetwork();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = userCol.document(user.getUid());
            docRef.set(profile)
                    .addOnCompleteListener(task -> callback.response(task.isSuccessful()));
        }
        callback.response(false);
    }

    public void getUserData(userProfileCallback callback) {
        db.enableNetwork();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = userCol.document(user.getUid());
            docRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            UserProfile profile;
                            if (document != null) {
                                profile = document.toObject(UserProfile.class);
                            } else {
                                profile = new UserProfile("Error", "Error", "", false, "", "");
                            }
                            callback.onProfileLoaded(profile);
                        }
                    });
        }
        UserProfile profile = new UserProfile("Error", "Error", "", false, "", "");
        callback.onProfileLoaded(profile);
    }

    public void checkIfUsernameExists(String username, booleanCallback callback) {
        db.enableNetwork();
        userCol.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.response(!task.getResult().isEmpty());
                    }
                });
    }

    public void addPost(int code, UserPosts post, boolean type, booleanCallback callback) {
        DocumentReference docRef;
        if (type) {
            if (code == 0) {
                docRef = postCol.document(post.getPostID());
                addToUserPost(post, true);
            } else {
                docRef = clubPostCol.document(post.getPostID());
                addToUserPost(post, true);
            }
        } else {
            if (code == 0) {
                addToUserPost(post, false);
            }
            docRef = reelsCol.document(post.getPostID());
        }

        docRef.set(post)
                .addOnCompleteListener(task -> callback.response(task.isSuccessful()));
    }

    public void addProduct(Product product, booleanCallback callback) {
        DocumentReference docRef = productsCol.document(product.getProductID());
        docRef.set(product)
                .addOnCompleteListener(task -> callback.response(task.isSuccessful()));
    }


    private void addToUserPost(UserPosts post, boolean type) {
        String col;
        if (type) {
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
        if (code == 0) {
            colUsed = postCol;
        } else if (code == 1) {
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

    public void changeLike(boolean op, int code, String postID) {
        DocumentReference docRef;
        if (code == 0) {
            docRef = postCol.document(postID);
        } else if (code == 1) {
            docRef = clubPostCol.document(postID);
        } else {
            docRef = reelsCol.document(postID);
        }
        if (op)
            docRef.update("like", FieldValue.increment(1));
        else
            docRef.update("like", FieldValue.increment(-1));
    }

    public void PostLikeOperation(boolean op, int code, String postID, String uid) {
        Map<String, Object> likeData = new HashMap<>();
        likeData.put("userId", uid);
        likeData.put("timestamp", FieldValue.serverTimestamp());
        DocumentReference docRef;
        if (code == 0) {
            docRef = postCol.document(postID).collection("likes").document(uid);
        } else if (code == 1) {
            docRef = clubPostCol.document(postID).collection("likes").document(uid);
        } else {
            docRef = reelsCol.document(postID).collection("likes").document(uid);
        }
        if (op)
            docRef.set(likeData);
        else
            docRef.delete();
    }

    public Task<Void> PostLikeOperationDemo(boolean op, int code, String postID, String uid) {
        Map<String, Object> likeData = new HashMap<>();
        likeData.put("userId", uid);
        likeData.put("timestamp", FieldValue.serverTimestamp());
        DocumentReference docRef;
        if (code == 0) {
            docRef = postCol.document(postID).collection("likes").document(uid);
        } else if (code == 1) {
            docRef = clubPostCol.document(postID).collection("likes").document(uid);
        } else {
            docRef = reelsCol.document(postID).collection("likes").document(uid);
        }

        changeLike(op, code, postID);
        if (op)
            return docRef.set(likeData);
        else
            return docRef.delete();
    }


    public void checkIfAlreadyLiked(int code, String postID, String uid, booleanCallback callback) {
        DocumentReference docRef;
        switch (code) {
            case 0:
                docRef = postCol.document(postID).collection("likes").document(uid);
                break;
            case 1:
                docRef = clubPostCol.document(postID).collection("likes").document(uid);
                break;
            case 2:
                docRef = reelsCol.document(postID).collection("likes").document(uid);
                break;
            default:
                throw new IllegalArgumentException("Invalid code: " + code);
        }

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean exists = task.getResult().exists();
                callback.response(exists);
            } else {
                Log.e("checkIfAlreadyLiked", "Error checking like status", task.getException());
                callback.response(false);
            }
        });
    }


    public void verifyIfAClub(booleanCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = clubInfoCol.document(user.getUid());
            docRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean exists = task.getResult().exists();
                            callback.response(exists);
                        }
                        callback.response(false);
                    });
        } else {
            callback.response(false);
        }
    }

    public void addLikeToUserPost(UserPosts userPosts) {
        DocumentReference docRef = userUploadsCol.document(userPosts.getUserUID()).collection("posts").document(userPosts.getPostID());
        docRef.update("like", FieldValue.increment(1));
    }

    public void removeLikeToUserPost(UserPosts userPosts) {
        DocumentReference docRef = userUploadsCol.document(userPosts.getUserUID()).collection("posts").document(userPosts.getPostID());
        docRef.update("like", FieldValue.increment(-1));
    }


    public void getMyPosts(String uid, UserPostsCallback callback) {
        ArrayList<UserPosts> posts = new ArrayList<>();

        userUploadsCol.document(uid).collection("posts")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (!task1.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task1.getResult()) {
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

    public void getMyProducts(ProductListCallback callback) {
        ArrayList<Product> list = new ArrayList<>();
        if (auth.getCurrentUser() != null) {
            productsCol.whereEqualTo("userUID", auth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                list.add(product);
                            }
                        }
                        callback.response(list);
                    });
        }
        callback.response(list);
    }


    public void getMyReels(String uid, UserPostsCallback callback) {
        ArrayList<UserPosts> posts = new ArrayList<>();

        userUploadsCol.document(uid).collection("reels")
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (!task1.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task1.getResult()) {
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

    public void deleteMyProduct(Product product, booleanCallback callback) {
        for (String url : product.getImages()) {
            deleteMediaUsingURL(url);
        }
        DocumentReference docRef = productsCol.document(product.getProductID());
        docRef.delete()
                .addOnCompleteListener(task -> callback.response(task.isSuccessful()));
    }


    public void deleteUserPost(String postID, String postURL, booleanCallback callback) {
        DocumentReference docRef;
        deleteFromPosts(postID);
        deleteMediaUsingURL(postURL);
        if (auth.getCurrentUser() != null) {
            docRef = userUploadsCol.document(auth.getCurrentUser().getUid()).collection("posts").document(postID);
            docRef.delete()
                    .addOnCompleteListener(task -> callback.response(task.isSuccessful()));
        }
        callback.response(false);
    }

    public void deleteMediaUsingURL(String url) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        imageRef.delete();
    }


    public void deleteUserReels(String postID, String postURL, booleanCallback callback) {
        DocumentReference docRef;
        deleteFromReels(postID);
        deleteMediaUsingURL(postURL);
        if (auth.getCurrentUser() != null) {
            docRef = userUploadsCol.document(auth.getCurrentUser().getUid()).collection("reels").document(postID);
            docRef.delete();
            deleteTheLikes(docRef);
        }
        callback.response(false);

    }

    private void deleteFromPosts(String postID) {
        DocumentReference docRef = postCol.document(postID);
        docRef.delete();
        deleteTheLikes(docRef);
    }

    private void deleteFromReels(String postID) {
        DocumentReference docRef = reelsCol.document(postID);
        docRef.delete();
        deleteTheLikes(docRef);
    }

    private void deleteTheLikes(DocumentReference docRef) {
        CollectionReference documentReference = docRef.collection("likes");
        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = db.batch();
                        QuerySnapshot snapshot = task.getResult();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            batch.delete(document.getReference());
                        }
                        batch.commit();
                    }
                });
    }


    public void findUser(String username, userProfileCallback callback) {
        userCol.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
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
        DocumentReference docRef = userCol.document(uid);

        docRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        UserProfile profile;
                        if (document != null) {
                            profile = document.toObject(UserProfile.class);
                        } else {
                            profile = new UserProfile("Error", "Error", "", false, "", "");
                        }
                        callback.onProfileLoaded(profile);
                    }
                });
    }

    public void updateUserImageURL(String url) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = userCol.document(user.getUid());
            docRef.update("imageURL", url);
        }
    }


    public void checkUpdates(UpdatesCallback callback) {
        DocumentReference documentReference = updatesCol.document("info");
        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Updates updates = document.toObject(Updates.class);
                            callback.callbackUpdate(updates);
                        }
                    }
                });
    }

    public void getNumberOfUser(IntegerCallback callback) {
        userCol.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        callback.count(count);
                    }
                });
    }


    public void getClubsCard(ClubCardCallback callback) {
        ArrayList<ClubEventCard> clubs = new ArrayList<>();
        clubInfoCol.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                ClubEventCard post = document.toObject(ClubEventCard.class);
                                clubs.add(post);
                            }
                        }
                        callback.onCardReceived(clubs);
                    } else {
                        callback.onCardReceived(clubs);
                    }
                });
    }

    public void updateClubBanner(String url, booleanCallback callback) {
        if (auth.getCurrentUser() != null) {
            DocumentReference docRef = clubInfoCol.document(auth.getCurrentUser().getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("bannerURL", url);
            //update the banner url
            docRef.update(updates)
                    .addOnSuccessListener(unused -> {
                        callback.response(true);
                    }).addOnFailureListener(e -> {
                        callback.response(false);
                    });
        }
        callback.response(false);
    }


    public void addEvent(Event event, booleanCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = clubInfoCol.document(user.getUid()).collection("events").document(event.getEid());
            docRef.set(event)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                            callback.response(true);
                        else
                            callback.response(false);
                    });
        }
        callback.response(false);
    }

    public void getEvents(String clubUID, ClubEventCallback callback) {
        ArrayList<Event> list = new ArrayList<>();

        if (clubUID != null) {
            CollectionReference colRef = clubInfoCol.document(clubUID).collection("events");
            colRef.orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    Event event = document.toObject(Event.class);
                                    list.add(event);
                                }
                                callback.response(list);
                            }
                            callback.response(list);
                        }
                        callback.response(list);
                    });
        }
        callback.response(list);
    }

    public void deleteEvent(Event event) {
        DocumentReference docRef = clubInfoCol.document(event.getClubID()).collection("events").document(event.getEid());
        docRef.delete();
    }


    public void getMarketPlaceProducts(ProductListCallback callback) {
        ArrayList<Product> clubs = new ArrayList<>();
        productsCol.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Product post = document.toObject(Product.class);
                                clubs.add(post);
                            }
                            callback.response(clubs);
                        }
                        callback.response(clubs);
                    } else {
                        callback.response(clubs);
                    }
                });
    }

    public void uploadPhotosAndGenerateUrls(ArrayList<Uri> uriList, UrlListCallback callBack) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("marketPLace");
        ArrayList<String> downloadUrls = new ArrayList<>();

        for (Uri photoUri : uriList) {
            String fileName = "images/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference photoRef = storageRef.child(fileName);

            UploadTask uploadTask = photoRef.putFile(photoUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(downloadUrl -> {
                    String url = downloadUrl.toString();
                    downloadUrls.add(url);

                    if (downloadUrls.size() == uriList.size()) {
                        callBack.response(downloadUrls);
                    }
                });
            });
        }
    }


}
