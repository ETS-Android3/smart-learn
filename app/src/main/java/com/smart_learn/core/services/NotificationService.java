package com.smart_learn.core.services;

import android.text.TextUtils;

import androidx.core.util.Pair;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.NotificationRepository;

import java.util.ArrayList;

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

    public void setNotificationsAsCounted(ArrayList<Pair<NotificationDocument, DocumentReference>> notificationsList,
                                          DataCallbacks.General callback) {
        if(notificationsList == null){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("notificationsList is null");
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Notifications was set as counted\n " + notificationsList.toString(),
                    "Notifications was NOT set as counted:\n " + notificationsList.toString());
        }

        repositoryInstance.setNotificationsAsCounted(notificationsList, callback);
    }

    public void processNotificationInCurrentThread(DocumentSnapshot notificationSnapshot, DataCallbacks.General callback){
        processNotification(notificationSnapshot, callback);
    }

    public void processNotificationInBackgroundThread(DocumentSnapshot notificationSnapshot, DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> processNotification(notificationSnapshot, callback));
    }

    private void processNotification(DocumentSnapshot notificationSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(notificationSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Notification with document snapshot ID [" +
                    notificationSnapshot.getId() + " was processed",
                    "Notification with document snapshot ID [" + notificationSnapshot.getId() + " was NOT processed");
        }

        NotificationDocument notification = notificationSnapshot.toObject(NotificationDocument.class);
        if(notification == null){
            callback.onFailure();
            return;
        }

        switch (notification.getType()) {
            case NotificationDocument.Types.TYPE_FRIEND_REQUEST_SENT:
                repositoryInstance.processNotificationForFriendRequestSent(notificationSnapshot, callback);
                break;
            case NotificationDocument.Types.TYPE_FRIEND_REQUEST_RECEIVED:
                repositoryInstance.processNotificationForFriendRequestReceived(notification, notificationSnapshot.getReference(), callback);
                break;
            case NotificationDocument.Types.TYPE_FRIEND_REQUEST_ACCEPTED:
                repositoryInstance.processNotificationForFriendRequestAccepted(notification, notificationSnapshot.getReference(), callback);
                break;

            case NotificationDocument.Types.TYPE_YOU_REMOVED_FRIEND:
                repositoryInstance.processNotificationForYouRemovedAFriend(notificationSnapshot, callback);
                break;
            case NotificationDocument.Types.TYPE_FRIEND_REMOVED_YOU:
                repositoryInstance.processNotificationForFriendRemovedYou(notification, notificationSnapshot.getReference(), callback);
                break;

            case NotificationDocument.Types.TYPE_NORMAL_LESSON_SENT:
                repositoryInstance.processNotificationForNormalLessonSent(notificationSnapshot, callback);
                break;
            case NotificationDocument.Types.TYPE_NORMAL_LESSON_RECEIVED:
                repositoryInstance.processNotificationForNormalLessonReceived(notification, notificationSnapshot.getReference(), callback);
                break;

            case NotificationDocument.Types.TYPE_SHARED_LESSON_SENT:
                repositoryInstance.processNotificationForSharedLessonSent(notificationSnapshot, callback);
                break;
            case NotificationDocument.Types.TYPE_SHARED_LESSON_RECEIVED:
                repositoryInstance.processNotificationForSharedLessonReceived(notificationSnapshot, callback);
                break;

            case NotificationDocument.Types.TYPE_ONLINE_TEST_INVITATION_RECEIVED:
                repositoryInstance.processNotificationForOnlineTestInvitationReceived(notificationSnapshot.getReference(), callback);
                break;

            case NotificationDocument.Types.TYPE_NONE:
                Timber.e("type none");
                break;
            default:
                Timber.e("default");
                break;
        }
    }

}
