package com.smart_learn.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

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
     * Query to show only notifications which are NOT hidden and sorted by most recent.
     *
     * @param limit How many items to get.
     *
     * @return Query object which was created.
     */
    public Query getQueryForAllVisibleNotifications(long limit) {
        return getNotificationsCollection()
                .whereEqualTo(NotificationDocument.Fields.HIDDEN_FIELD_NAME, false)
                .orderBy(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME + "." + DocumentMetadata.Fields.CREATED_AT_FIELD_NAME,
                        Query.Direction.DESCENDING)
                .limit(limit);
    }

    public void addDocument(@NonNull @NotNull NotificationDocument item,
                            @NotNull DataCallbacks.General callback){
        super.addDocument(item, getNotificationsCollection(), callback);
    }
}
