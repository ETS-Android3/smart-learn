package com.smart_learn.core.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Pair;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.core.services.helpers.ThreadExecutorService;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.NotificationRepository;
import com.smart_learn.presenter.activities.main.MainActivity;

import java.util.ArrayList;

import timber.log.Timber;

public class NotificationService extends BasicFirestoreService<NotificationDocument, NotificationRepository> {

    private final static String CHANNEL_ID = "smart-learn-firestore-cloud-notification-channel-id";
    private final static String CHANNEL_NAME = "smart-learn-firestore-cloud-notification-channel-name";

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

    public void showPushNotification(Context context, NotificationDocument notification){
        if(context == null || notification == null){
            Timber.w("Context [" + context + "] and/or Notification [" + notification + "] is/are null" );
            return;
        }

        switch (notification.getType()) {
            case NotificationDocument.Types.TYPE_FRIEND_REQUEST_RECEIVED:
            case NotificationDocument.Types.TYPE_FRIEND_REQUEST_ACCEPTED:
            case NotificationDocument.Types.TYPE_NORMAL_LESSON_RECEIVED:
            case NotificationDocument.Types.TYPE_SHARED_LESSON_RECEIVED:
            case NotificationDocument.Types.TYPE_ONLINE_TEST_INVITATION_RECEIVED:
                final String title = context.getString(NotificationDocument.generateNotificationTitle(notification.getType()));
                final String message = NotificationDocument.generatePushNotificationDescription(notification.getType());
                launchPushNotification(context, title, message);
                break;
            // For the other type of notifications will not be used a push notification.
            default:
                break;
        }
    }

    private void launchPushNotification(Context context, String title, String message){
        // prepare activity which will be opened when click on notification is made
        Intent activityIntent = new Intent(context, MainActivity.class);

        // https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
        activityIntent.putExtra(MainActivity.CALLED_BY_PUSH_NOTIFICATION_KEY, true);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // https://stackoverflow.com/questions/67045607/how-to-resolve-missing-pendingintent-mutability-flag-lint-warning-in-android-a
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // https://developer.android.com/training/notify-user/build-notification.html#java
        // https://www.youtube.com/watch?v=CZ575BuLBo4&ab_channel=CodinginFlow
        // prepare notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setContentIntent(notifyPendingIntent);

        // https://developer.android.com/training/notify-user/build-notification.html#java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        // https://stackoverflow.com/questions/39607856/what-is-notification-id-in-android
        // TODO: try to generate specific unique id's and save id's in DB
        int uniqueId = (int) System.currentTimeMillis();
        notificationManager.notify(uniqueId, builder.build());
    }

}
