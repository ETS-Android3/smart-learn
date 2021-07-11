package com.smart_learn.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class NotificationRepository extends BasicFirestoreRepository<NotificationDocument> {

    @Getter
    private final CollectionReference notificationsCollectionRef;

    public NotificationRepository(@NonNull @NotNull String userUid) {
        // init notifications collection reference
        notificationsCollectionRef = getSpecificNotificationsCollection(userUid);
    }

    /**
     * Query to show only notifications which are NOT hidden and sorted by most recent.
     *
     * @param limit How many items to get.
     *
     * @return Query object which was created.
     */
    public Query getQueryForAllVisibleNotifications(long limit) {
        return notificationsCollectionRef
                .whereEqualTo(NotificationDocument.Fields.HIDDEN_FIELD_NAME, false)
                .orderBy(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME + "." + DocumentMetadata.Fields.CREATED_AT_FIELD_NAME,
                        Query.Direction.DESCENDING)
                .limit(limit);
    }

    public void addDocument(@NonNull @NotNull NotificationDocument item,
                            @NotNull DataCallbacks.General callback){
        super.addDocument(item, notificationsCollectionRef, callback);
    }


    /**
     * Get a specific notification collection reference for a custom user.
     *
     * @param userUid UID of the owner of the collection.
     *
     * @return A reference to collection.
     * */
    public static CollectionReference getSpecificNotificationsCollection(@NonNull @NotNull String userUid){
        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_USERS + "/" + userUid + "/" + COLLECTION_NOTIFICATIONS);
    }
}
