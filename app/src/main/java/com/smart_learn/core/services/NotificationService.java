package com.smart_learn.core.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.repository.NotificationRepository;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NotificationService extends BasicFirestoreService<NotificationDocument, NotificationRepository> {

    public NotificationService(@NonNull @NotNull String userUid){
        super(new NotificationRepository(userUid));
    }

    public void markAsHidden(@NonNull @NotNull DocumentSnapshot documentSnapshot,
                             @Nullable DataCallbacks.InsertUpdateDeleteCallback<DocumentSnapshot> callback){
        Map<String,Object> data = new HashMap<>();
        data.put(NotificationDocument.HIDDEN_FIELD_NAME, true);
        data.put(DocumentMetadata.DOCUMENT_METADATA_FIELD_NAME + "." + DocumentMetadata.MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        repository.updateDocument(data, documentSnapshot, callback);
    }

    public void markAsRead(@NonNull @NotNull DocumentSnapshot documentSnapshot,
                           @Nullable DataCallbacks.InsertUpdateDeleteCallback<DocumentSnapshot> callback){
        Map<String,Object> data = new HashMap<>();
        data.put(NotificationDocument.MARKED_AS_READ_FIELD_NAME, true);
        data.put(DocumentMetadata.DOCUMENT_METADATA_FIELD_NAME + "." + DocumentMetadata.MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        repository.updateDocument(data, documentSnapshot, callback);
    }

    public Query getQueryForAllVisibleNotifications(long limit) {
        return repository.getQueryForAllVisibleNotifications(limit);
    }

    public void addDocument(@NonNull @NotNull NotificationDocument item,
                            @Nullable DataCallbacks.InsertUpdateDeleteCallback<DocumentReference> callback){
        repository.addDocument(item, callback);
    }

}
