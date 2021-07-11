package com.smart_learn.core.services;

import android.text.TextUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.NotificationRepository;

import timber.log.Timber;

public class NotificationService extends BasicFirestoreService<NotificationDocument, NotificationRepository> {

    private static NotificationService instance;

    private NotificationService() {
        super(NotificationRepository.getInstance());
    }

    public static NotificationService getInstance() {
        if(instance == null){
            instance = new NotificationService();
        }
        return instance;
    }

    public CollectionReference getNotificationsCollection(){
        return repositoryInstance.getNotificationsCollection();
    }

    public CollectionReference getSpecificNotificationsCollection(String userUid){
        if(TextUtils.isEmpty(userUid)){
            throw new UnsupportedOperationException("userUid must not be null or empty");
        }
        return repositoryInstance.getSpecificNotificationsCollection(userUid);
    }

    public Query getQueryForAllVisibleNotifications(long limit) {
        return repositoryInstance.getQueryForAllVisibleNotifications(limit);
    }

    public void markAsHidden(DocumentSnapshot notificationSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(notificationSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + notificationSnapshot.getId() + " marked as hidden",
                    "Document " + notificationSnapshot.getId() + " was NOT marked as hidden");
        }

        NotificationDocument notification = notificationSnapshot.toObject(NotificationDocument.class);
        if(notification == null){
            callback.onFailure();
            Timber.w("notification is null");
            return;
        }

        repositoryInstance.markAsHidden(notificationSnapshot, notification.getMarkedAsRead(), callback);
    }

    public void markAsRead(DocumentSnapshot notificationSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(notificationSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + notificationSnapshot.getId() + " marked as read",
                    "Document " + notificationSnapshot.getId() + " was NOT marked as read");
        }

        repositoryInstance.markAsRead(notificationSnapshot, callback);
    }

}
