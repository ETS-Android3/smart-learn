package com.smart_learn.data.repository;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

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
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class NotificationRepository extends BasicFirestoreRepository<NotificationDocument> {

    private static NotificationRepository instance;

    private NotificationRepository() {

    }

    public static NotificationRepository getInstance() {
        if(instance == null){
            instance = new NotificationRepository();
        }
        return instance;
    }

    public CollectionReference getNotificationsCollection(){
        return getSpecificNotificationsCollection(CoreUtilities.Auth.getUserUid());
    }

    /**
     * Get a specific notification collection reference for a custom user.
     *
     * @param userUid UID of the owner of the collection.
     *
     * @return A reference to collection.
     * */
    public CollectionReference getSpecificNotificationsCollection(@NonNull @NotNull String userUid){
        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_USERS + "/" + userUid + "/" + COLLECTION_NOTIFICATIONS);
    }

    /**
     * Query to show only notifications which are NOT hidden and with finished internal processing.
     * Also will be sorted by most recent.
     *
     * @param limit How many items to get.
     *
     * @return Query object which was created.
     */
    public Query getQueryForAllVisibleNotifications(long limit) {
        return getNotificationsCollection()
                .whereEqualTo(NotificationDocument.Fields.HIDDEN_FIELD_NAME, false)
                .whereEqualTo(NotificationDocument.Fields.FINISHED_FIELD_NAME, true)
                .orderBy(DocumentMetadata.Fields.COMPOSED_CREATED_AT_FIELD_NAME,
                        Query.Direction.DESCENDING)
                .limit(limit);
    }

    public void markAsHidden(@NonNull @NotNull DocumentSnapshot notificationSnapshot, boolean markedAsRead,
                                  @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToMarkAsHidden(notificationSnapshot,
                markedAsRead, callback));
    }

    private void tryToMarkAsHidden(@NonNull @NotNull DocumentSnapshot notificationSnapshot, boolean markedAsRead,
                                  @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Set notification as hidden
        Map<String,Object> notData = new HashMap<>();
        notData.put(NotificationDocument.Fields.HIDDEN_FIELD_NAME, true);
        // by default if notification is hidden, then will be marked as read also if is not already marked
        if(!markedAsRead){
            notData.put(NotificationDocument.Fields.MARKED_AS_READ_FIELD_NAME, true);
        }
        notData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(notificationSnapshot.getReference(), notData);

        // 2. Decrement user counter only if notification was previously NOT marked as read
        if(!markedAsRead){
            Map<String,Object> userData = new HashMap<>();
            userData.put(UserDocument.Fields.NR_OF_UNREAD_NOTIFICATIONS_FIELD_NAME, FieldValue.increment(-1));
            userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
            batch.update(FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(CoreUtilities.Auth.getUserUid()), userData);
        }

        // 3. Transaction is complete so commit.
        commitBatch(batch, callback);
    }

    public void markAsRead(@NonNull @NotNull DocumentSnapshot notification, @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToMarkAsRead(notification, callback));
    }

    private void tryToMarkAsRead(@NonNull @NotNull DocumentSnapshot notification, @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Set notification as read
        Map<String,Object> notData = new HashMap<>();
        notData.put(NotificationDocument.Fields.MARKED_AS_READ_FIELD_NAME, true);
        notData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(notification.getReference(), notData);

        // 2. Decrement user counter
        Map<String,Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_UNREAD_NOTIFICATIONS_FIELD_NAME, FieldValue.increment(-1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(CoreUtilities.Auth.getUserUid()), userData);

        // 3. Transaction is complete so commit.
        commitBatch(batch, callback);
    }


    public void setNotificationsAsCounted(@NonNull @NotNull ArrayList<Pair<NotificationDocument, DocumentReference>> notificationsList,
                                          @NonNull @NotNull DataCallbacks.General callback) {
        if(notificationsList.isEmpty()){
            callback.onSuccess();
            return;
        }

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        long cnt = 0;

        // 1. Update counted as true in every notification
        for(Pair<NotificationDocument, DocumentReference> pair : notificationsList){
            // only uncounted notifications will marked as counted
            if(pair.first.getDocumentMetadata().getCounted()){
                continue;
            }

            batch.update(pair.second, DocumentMetadata.Fields.COMPOSED_COUNTED_FIELD_NAME, true);
            batch.update(pair.second, DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());

            // if notification is not read marked add it to the counter
            if(!pair.first.getMarkedAsRead()){
                cnt++;
            }
        }

        // 2. Update user document counter for unread notifications if necessary
        if(cnt > 0){
            batch.update(FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(CoreUtilities.Auth.getUserUid()),
                    UserDocument.Fields.NR_OF_UNREAD_NOTIFICATIONS_FIELD_NAME, FieldValue.increment(cnt));
            batch.update(FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(CoreUtilities.Auth.getUserUid()),
                    DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        }

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);

    }


    public void processNotificationForFriendRequestSent(@NonNull @NotNull DocumentSnapshot notificationSnapshot,
                                                        @NonNull @NotNull DataCallbacks.General callback){
        // for this operation is needed only to set notification to finish
        HashMap<String, Object> data = new HashMap<>();
        data.put(NotificationDocument.Fields.FINISHED_FIELD_NAME, true);
        updateDocument(data, notificationSnapshot, callback);
    }


    public void processNotificationForYouRemovedAFriend(@NonNull @NotNull DocumentSnapshot notificationSnapshot,
                                                        @NonNull @NotNull DataCallbacks.General callback){
        // for this operation is needed only to set notification to finish
        HashMap<String, Object> data = new HashMap<>();
        data.put(NotificationDocument.Fields.FINISHED_FIELD_NAME, true);
        updateDocument(data, notificationSnapshot, callback);
    }


    public void processNotificationForFriendRequestReceived(@NonNull @NotNull NotificationDocument notificationDocument,
                                                            @NonNull @NotNull DocumentReference notificationDocRef,
                                                            @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToProcessNotificationForFriendRequestReceived(notificationDocument, notificationDocRef, callback));
    }

    private void tryToProcessNotificationForFriendRequestReceived(@NonNull @NotNull NotificationDocument notificationDocument,
                                                                  @NonNull @NotNull DocumentReference notificationDocRef,
                                                                  @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Set finished on notification status
        batch.update(notificationDocRef, NotificationDocument.Fields.FINISHED_FIELD_NAME, true);

        // 2. Add UID in currentUser received list of UID
        batch.update(FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(CoreUtilities.Auth.getUserUid()),
                UserDocument.Fields.RECEIVED_REQUESTS_FIELD_NAME, FieldValue.arrayUnion(notificationDocument.getFromUid()));

        // 3. Transaction is complete so commit.
        commitBatch(batch, callback);
    }


    public void processNotificationForFriendRequestAccepted(@NonNull @NotNull NotificationDocument notificationDocument,
                                                            @NonNull @NotNull DocumentReference notificationDocRef,
                                                            @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToProcessNotificationForFriendRequestAccepted(notificationDocument,
                notificationDocRef, callback));
    }


    private void tryToProcessNotificationForFriendRequestAccepted(@NonNull @NotNull NotificationDocument notificationDocument,
                                                                  @NonNull @NotNull DocumentReference notificationDocRef,
                                                                  @NonNull @NotNull DataCallbacks.General callback){

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Update notification status with finished
        batch.update(notificationDocRef, NotificationDocument.Fields.FINISHED_FIELD_NAME, true);

        // 2. Add to friend list UID in currentUser document and remove from pending lists.
        //    Obs: also remove from received UID list just to be sure.
        HashMap<String, Object> data = new HashMap<>();
        data.put(UserDocument.Fields.RECEIVED_REQUESTS_FIELD_NAME, FieldValue.arrayRemove(notificationDocument.getFromUid()));
        data.put(UserDocument.Fields.PENDING_FRIENDS_FIELD_NAME, FieldValue.arrayRemove(notificationDocument.getFromUid()));
        data.put(UserDocument.Fields.FRIENDS_FIELD_NAME, FieldValue.arrayUnion(notificationDocument.getFromUid()));
        batch.update(FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(CoreUtilities.Auth.getUserUid()), data);

        // 3. Add friend in currentUser friendsCollection if does not already exist
        FirebaseFirestore.getInstance().collection(COLLECTION_FRIENDS)
                .whereEqualTo(FriendDocument.Fields.FRIEND_UID_FIELD_NAME, notificationDocument.getFromUid())
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
                            Timber.w("snapshotList has size [" + snapshotList.size() + "]");
                            return;
                        }


                        // friend already exists so commit because a new addition is not necessary
                        if(snapshotList.size() == 1){
                            // 4. Transaction is complete so commit
                            commitBatch(batch, callback);
                            return;
                        }

                        // Here friend does not exists so add it. Extract user document from
                        // notification from reference and add it to currentUser friends collection.
                        notificationDocument
                                .getFromDocumentReference()
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
                                                new DocumentMetadata(CoreUtilities.Auth.getUserUid(), System.currentTimeMillis(), searchList),
                                                user.getEmail(),
                                                user.getDisplayName(),
                                                user.getProfilePhotoUrl(),
                                                user.getDocumentMetadata().getOwner(),
                                                notificationDocument.getDocumentMetadata().getCreatedAt(),
                                                task.getResult().getReference()
                                        );

                                        // add friend
                                        DocumentReference newFriendDocRef = FirebaseFirestore.getInstance().collection(COLLECTION_FRIENDS).document();
                                        batch.set(newFriendDocRef, FriendDocument.convertDocumentToHashMap(newFriend));

                                        // 4. Transaction is complete so commit
                                        commitBatch(batch, callback);
                                    }
                                });
                    }
                });
    }


    public void processNotificationForFriendRemovedYou(@NonNull @NotNull NotificationDocument notificationDocument,
                                                       @NonNull @NotNull DocumentReference notificationDocRef,
                                                       @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToProcessNotificationForFriendRemovedYou(notificationDocument,
                notificationDocRef, callback));
    }

    private void tryToProcessNotificationForFriendRemovedYou(@NonNull @NotNull NotificationDocument notificationDocument,
                                                             @NonNull @NotNull DocumentReference notificationDocRef,
                                                             @NonNull @NotNull DataCallbacks.General callback){

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Update notification status with finished
        batch.update(notificationDocRef, NotificationDocument.Fields.FINISHED_FIELD_NAME, true);

        // 2. Remove friend from currentUser document UID lists.
        HashMap<String, Object> data = new HashMap<>();
        data.put(UserDocument.Fields.RECEIVED_REQUESTS_FIELD_NAME, FieldValue.arrayRemove(notificationDocument.getFromUid()));
        data.put(UserDocument.Fields.PENDING_FRIENDS_FIELD_NAME, FieldValue.arrayRemove(notificationDocument.getFromUid()));
        data.put(UserDocument.Fields.FRIENDS_FIELD_NAME, FieldValue.arrayRemove(notificationDocument.getFromUid()));
        batch.update(FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(CoreUtilities.Auth.getUserUid()), data);

        // 3. Remove friend from currentUser friendsCollection
        FirebaseFirestore.getInstance().collection(COLLECTION_FRIENDS)
                .whereEqualTo(FriendDocument.Fields.FRIEND_UID_FIELD_NAME, notificationDocument.getFromUid())
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

                        // 4. Transaction is complete so commit
                        commitBatch(batch, callback);
                    }
                });
    }

}
