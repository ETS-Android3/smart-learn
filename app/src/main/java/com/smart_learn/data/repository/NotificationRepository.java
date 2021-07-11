package com.smart_learn.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
}
