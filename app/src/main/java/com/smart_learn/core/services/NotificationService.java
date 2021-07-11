package com.smart_learn.core.services;

import androidx.annotation.NonNull;

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
                             @NotNull DataCallbacks.General callback){
        Map<String,Object> data = new HashMap<>();
        data.put(NotificationDocument.Fields.HIDDEN_FIELD_NAME, true);
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME + "." + DocumentMetadata.Fields.MODIFIED_AT_FIELD_NAME,
                System.currentTimeMillis());
        repositoryInstance.updateDocument(data, documentSnapshot, callback);
    }

    public void markAsRead(@NonNull @NotNull DocumentSnapshot documentSnapshot,
                           @NotNull DataCallbacks.General callback){
        Map<String,Object> data = new HashMap<>();
        data.put(NotificationDocument.Fields.MARKED_AS_READ_FIELD_NAME, true);
        data.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME + "." + DocumentMetadata.Fields.MODIFIED_AT_FIELD_NAME,
                System.currentTimeMillis());
        repositoryInstance.updateDocument(data, documentSnapshot, callback);
    }

    public Query getQueryForAllVisibleNotifications(long limit) {
        return repositoryInstance.getQueryForAllVisibleNotifications(limit);
    }

    public void addDocument(@NonNull @NotNull NotificationDocument item,
                            @NotNull DataCallbacks.General callback){
        repositoryInstance.addDocument(item, callback);
    }

}
