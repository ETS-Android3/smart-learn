package com.smart_learn.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.core.services.NotificationService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class FriendRepository extends BasicFirestoreRepository<FriendDocument> {

    private static FriendRepository instance;

    private FriendRepository() {

    }

    public static FriendRepository getInstance() {
        if(instance == null){
            instance = new FriendRepository();
        }
        return instance;
    }

    public Query getQueryForAllAcceptedFriends(long limit) {
        return getFriendsCollectionReference()
                .orderBy(BasicProfileDocument.Fields.DISPLAY_NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilter(long limit, @NonNull @NotNull String value) {
        return getFriendsCollectionReference()
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(BasicProfileDocument.Fields.DISPLAY_NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public CollectionReference getFriendsCollectionReference(){
        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_USERS + "/" + UserService.getInstance().getUserUid() + "/" + COLLECTION_FRIENDS);
    }


    public void sendFriendRequest(@NotNull @NonNull String newFriendUid,
                                  @NotNull @NonNull String newFriendDisplayName,
                                  @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToSendFriendRequest(newFriendUid, newFriendDisplayName, callback));
    }

    private void tryToSendFriendRequest(@NotNull @NonNull String newFriendUid,
                                        @NotNull @NonNull String newFriendDisplayName,
                                        @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add newFriend in currentUser pending list
        HashMap<String, Object> data = new HashMap<>();
        data.put(UserDocument.Fields.PENDING_FRIENDS_FIELD_NAME, FieldValue.arrayUnion(newFriendUid));
        batch.update(UserService.getInstance().getUserDocumentReference(), data);

        // 2. Send notification for newFriend with new friend request.
        NotificationDocument friendNotification = new NotificationDocument(
                new DocumentMetadata(newFriendUid, System.currentTimeMillis(), new ArrayList<>()),
                UserService.getInstance().getUserUid(),
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserDocumentReference(),
                NotificationDocument.Types.TYPE_FRIEND_REQUEST_RECEIVED
        );
        // use display name as extra info in order to show it in notification
        friendNotification.setExtraInfo(UserService.getInstance().getUserDisplayName());
        DocumentReference friendNotificationDocRef = NotificationService.getInstance().getSpecificNotificationsCollection(newFriendUid).document();
        batch.set(friendNotificationDocRef, NotificationDocument.convertDocumentToHashMap(friendNotification));

        // 3. Add a notification with type TYPE_YOU_SENT_A_FRIEND_REQUEST for currentUser
        NotificationDocument userNotification = new NotificationDocument(
                new DocumentMetadata(UserService.getInstance().getUserUid(), System.currentTimeMillis(), new ArrayList<>()),
                UserService.getInstance().getUserUid(),
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserDocumentReference(),
                NotificationDocument.Types.TYPE_FRIEND_REQUEST_SENT
        );
        // use friend display name as extra info in order to show it in notification
        userNotification.setExtraInfo(newFriendDisplayName);
        DocumentReference userNotificationDocRef = NotificationService.getInstance().getNotificationsCollection().document();
        batch.set(userNotificationDocRef, NotificationDocument.convertDocumentToHashMap(userNotification));

        // 4. Commit batch
        commitBatch(batch, callback);
    }


    public void acceptFriendRequest(@NonNull @NotNull String friendUid,
                                    @NonNull @NotNull DocumentReference friendDocumentReference,
                                    @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAcceptFriendRequest(friendUid, friendDocumentReference, callback));
    }


    private void tryToAcceptFriendRequest(@NonNull @NotNull String friendUid,
                                          @NonNull @NotNull DocumentReference friendDocumentReference,
                                          @NonNull @NotNull DataCallbacks.General callback){

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add friend in friends UID list from currentUser document, and remove from other UID list.
        HashMap<String, Object> data = new HashMap<>();
        data.put(UserDocument.Fields.RECEIVED_REQUESTS_FIELD_NAME, FieldValue.arrayRemove(friendUid));
        data.put(UserDocument.Fields.PENDING_FRIENDS_FIELD_NAME, FieldValue.arrayRemove(friendUid));
        data.put(UserDocument.Fields.FRIENDS_FIELD_NAME, FieldValue.arrayUnion(friendUid));
        batch.update(UserService.getInstance().getUserDocumentReference(), data);

        // 2. Send a notification with request accepted
        DocumentReference notDocRef = NotificationService.getInstance().getSpecificNotificationsCollection(friendUid).document();
        NotificationDocument newNotification = new NotificationDocument(
                new DocumentMetadata(friendUid, System.currentTimeMillis(), new ArrayList<>()),
                UserService.getInstance().getUserUid(),
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserDocumentReference(),
                NotificationDocument.Types.TYPE_FRIEND_REQUEST_ACCEPTED
        );
        // use display name as extra info in order to show it in notification
        newNotification.setExtraInfo(UserService.getInstance().getUserDisplayName());
        batch.set(notDocRef, NotificationDocument.convertDocumentToHashMap(newNotification));


        // 3. Update all notifications from friendUid and TYPE_NEW_FRIEND_REQUEST and set accepted
        // to true.
        NotificationService.getInstance()
                .getNotificationsCollection()
                .whereEqualTo(NotificationDocument.Fields.FROM_UID_FIELD_NAME, friendUid)
                .whereEqualTo(NotificationDocument.Fields.TYPE_FIELD_NAME, NotificationDocument.Types.TYPE_FRIEND_REQUEST_RECEIVED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                            callback.onFailure();
                            return;
                        }

                        List<DocumentSnapshot> snapshotList = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(DocumentSnapshot notification : snapshotList){
                            batch.update(notification.getReference(), NotificationDocument.Fields.ACCEPTED_FIELD_NAME, true);
                        }

                        // 4. Add friend in user friends collection if does not already exists (he should not exists
                        // but check to be sure).
                        getFriendsCollectionReference()
                                .whereEqualTo(FriendDocument.Fields.FRIEND_UID_FIELD_NAME, friendUid)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                                            callback.onFailure();
                                            return;
                                        }

                                        List<DocumentSnapshot> snapshotList = Objects.requireNonNull(task.getResult()).getDocuments();
                                        // only one friend must exists with same UID
                                        if(snapshotList.size() > 1){
                                            callback.onFailure();
                                            Timber.e("snapshotList has size [" + snapshotList.size() + "] ==> to much friends" +
                                                    " with UID = " + friendUid);
                                            return;
                                        }

                                        // friend already exists so commit because a new addition is not necessary
                                        if(snapshotList.size() == 1){
                                            // 5. Transaction is complete so commit
                                            commitBatch(batch, callback);
                                            return;
                                        }

                                        // Here friend does not exists so add it. Extract friend document from
                                        // friend reference and add it to currentUser friends collection.
                                        friendDocumentReference
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                                        if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                                                            callback.onFailure();
                                                            return;
                                                        }

                                                        UserDocument user = Objects.requireNonNull(task.getResult()).toObject(UserDocument.class);
                                                        if(user == null){
                                                            callback.onFailure();
                                                            Timber.w("user is null");
                                                            return;
                                                        }

                                                        // create search list
                                                        ArrayList<String> searchValues = new ArrayList<>();
                                                        searchValues.add(user.getDisplayName());
                                                        searchValues.add(user.getEmail());
                                                        ArrayList<String> searchList = CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues);

                                                        // create friend
                                                        FriendDocument newFriend = new FriendDocument(
                                                                new DocumentMetadata(UserService.getInstance().getUserUid(), System.currentTimeMillis(), searchList),
                                                                user.getEmail(),
                                                                user.getDisplayName(),
                                                                user.getProfilePhotoUrl(),
                                                                user.getDocumentMetadata().getOwner(),
                                                                System.currentTimeMillis(),
                                                                task.getResult().getReference()
                                                        );

                                                        // add friend
                                                        DocumentReference newFriendDocRef = getFriendsCollectionReference().document();
                                                        batch.set(newFriendDocRef, FriendDocument.convertDocumentToHashMap(newFriend));

                                                        // 5. Transaction is complete so commit
                                                        commitBatch(batch, callback);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }



    public void removeFriend(@NonNull @NotNull String friendUid, @NonNull @NotNull String friendDisplayName,
                             @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToRemoveFriend(friendUid, friendDisplayName, callback));
    }

    private void tryToRemoveFriend(@NonNull @NotNull String friendUid, @NonNull @NotNull String friendDisplayName,
                                   @NonNull @NotNull DataCallbacks.General callback){
        // Will be necessary a transaction for removing friend
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Remove friend UID from currentUser lists UID.
        HashMap<String, Object> data = new HashMap<>();
        data.put(UserDocument.Fields.RECEIVED_REQUESTS_FIELD_NAME, FieldValue.arrayRemove(friendUid));
        data.put(UserDocument.Fields.PENDING_FRIENDS_FIELD_NAME, FieldValue.arrayRemove(friendUid));
        data.put(UserDocument.Fields.FRIENDS_FIELD_NAME, FieldValue.arrayRemove(friendUid));
        batch.update(UserService.getInstance().getUserDocumentReference(), data);

        // 2. Send a notification with friend deleted to the user with friendUid.
        NotificationDocument friendNotification = new NotificationDocument(
                new DocumentMetadata(friendUid, System.currentTimeMillis(), new ArrayList<>()),
                UserService.getInstance().getUserUid(),
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserDocumentReference(),
                NotificationDocument.Types.TYPE_FRIEND_REMOVED_YOU
        );
        // by default this type of notification is marked as read in order to not be counted as unread
        friendNotification.setMarkedAsRead(true);
        // by default this type of notification is hidden in order to not be shown to the user
        friendNotification.setHidden(true);
        // by default set extra info with display name
        friendNotification.setExtraInfo(friendDisplayName);
        DocumentReference friendNotificationDocRef = NotificationService.getInstance().getSpecificNotificationsCollection(friendUid).document();
        batch.set(friendNotificationDocRef, NotificationDocument.convertDocumentToHashMap(friendNotification));


        // 3. Add a notification with type TYPE_YOU_REMOVED_FRIEND is user notifications
        NotificationDocument userNotification = new NotificationDocument(
                new DocumentMetadata(UserService.getInstance().getUserUid(), System.currentTimeMillis(), new ArrayList<>()),
                UserService.getInstance().getUserUid(),
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserDocumentReference(),
                NotificationDocument.Types.TYPE_YOU_REMOVED_FRIEND
        );
        // set extra info with ex-friend Display name in order to show-it to the user
        userNotification.setExtraInfo(friendDisplayName);
        DocumentReference userNotificationDocRef = NotificationService.getInstance().getNotificationsCollection().document();
        batch.set(userNotificationDocRef, NotificationDocument.convertDocumentToHashMap(userNotification));


        // 4. Delete friend from currentUser friends collection
        getFriendsCollectionReference()
                .whereEqualTo(FriendDocument.Fields.FRIEND_UID_FIELD_NAME, friendUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                            callback.onFailure();
                            return;
                        }

                        // Delete all apparitions (normally one apparition should exists if were
                        // friends but try to be sure).
                        List<DocumentSnapshot> snapshotList = Objects.requireNonNull(task.getResult()).getDocuments();
                        for(DocumentSnapshot snapshot : snapshotList){
                            batch.delete(snapshot.getReference());
                        }

                        // 5. Transaction is complete so commit
                        commitBatch(batch, callback);
                    }
                });
    }

}
