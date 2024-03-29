package com.smart_learn.core.common.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.smart_learn.BuildConfig;
import com.smart_learn.core.user.services.NotificationService;
import com.smart_learn.core.common.services.SettingsService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.user.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.common.helpers.DataCallbacks;

import java.util.ArrayList;

import timber.log.Timber;

public class ApplicationController extends Application {

    // for shared preferences
    public static final String LOGIN_STATUS_KEY = "LOGIN_STATUS_KEY";
    public static final String LOGGED_IN = "LOGGED_IN";

    private static ApplicationController applicationController;

    private ListenerRegistration notificationsListener;
    private ListenerRegistration alarmsListener;

    public static ApplicationController getInstance() {return applicationController;}

    @Override
    public void onCreate() {
        super.onCreate();
        applicationController = this;
        if(BuildConfig.DEBUG){
            // https://gist.github.com/katowulf/0475fb7a5907ed757f687aab6ed15878
            // https://stackoverflow.com/questions/48674134/how-to-set-log-level-for-firestore
            FirebaseFirestore.setLoggingEnabled(true);

            Timber.plant(new CustomTimberDebugTree());
        }

        addUserStatusListener();
    }

    private void addUserStatusListener(){

        // https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase/51571501#51571501
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // This listener is used only to mark if user is no longer logged in.
                // To mark that a user is logged it will be made in login functions for every provider.
                if(firebaseAuth.getCurrentUser() == null){
                    SharedPreferences preferences = getSharedPreferences(LOGIN_STATUS_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(LOGGED_IN, false);
                    editor.apply();

                    // remove listeners because user is not logged in anymore
                    removeNotificationsListener();
                    removeAlarmsListener();
                }
                else{
                    addNotificationsCollectionListener();
                    addAlarmListener();
                }
            }
        };

        // attach listener
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void addNotificationsCollectionListener(){
        // first remove old listener (if any)
        removeNotificationsListener();

        notificationsListener = NotificationService.getInstance().getNotificationsCollection()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Timber.e(e);
                            return;
                        }

                        if(snapshots == null){
                            Timber.i("snapshots is null");
                            return;
                        }

                        ArrayList<Pair<NotificationDocument, DocumentReference>> countingList = new ArrayList<>();

                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            NotificationDocument notification = document.toObject(NotificationDocument.class);
                            if(notification == null){
                                continue;
                            }

                            // if notification is not counted add it for counting
                            if(!notification.getDocumentMetadata().getCounted()){
                                countingList.add(new Pair<>(notification, document.getReference()));
                                continue;
                            }

                            // check if notification was already processed
                            if(notification.getFinished()){
                                continue;
                            }

                            // here notification was counted but not processed
                            NotificationService.getInstance().processNotificationInCurrentThread(document, new DataCallbacks.General() {
                                @Override
                                public void onSuccess() {
                                    Timber.i("Processed notification with document id = [%s]", document.getId());
                                    // launch also a push notification in order to notify user
                                    NotificationService.getInstance()
                                            .showPushNotification(ApplicationController.getInstance().getApplicationContext(), notification);
                                }
                                @Override
                                public void onFailure() {
                                    Timber.e("Failed to process notification with document id = [%s]", document.getId());
                                }
                            });
                        }

                        // After counting is made, notifications will be updated and this listener
                        // will be triggered again. When will be triggered again, notifications will
                        // be processed, and finished status will be set.
                        if(!countingList.isEmpty()){
                            NotificationService.getInstance().setNotificationsAsCounted(countingList, null);
                        }

                    }
                });

    }

    private void removeNotificationsListener(){
        if (notificationsListener != null){
            notificationsListener.remove();
            notificationsListener = null;
        }
    }

    private void addAlarmListener(){
        // first remove old listener (if any)
        removeAlarmsListener();

        // When a new scheduled active test is added/modified, alarm will be reset on device also.
        // When a scheduled active test is removed, his alarm will be unset from device.

        // https://stackoverflow.com/questions/54837988/android-firestore-view-changes-between-snapshots-from-document
        alarmsListener = TestService.getInstance()
                .getQueryForAllScheduledActiveLocalTests()
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Timber.e(error);
                            return;
                        }

                        if(value == null){
                            Timber.i("value is null");
                            return;
                        }

                        for (DocumentChange change : value.getDocumentChanges()) {
                            TestDocument test = change.getDocument().toObject(TestDocument.class);
                            // If update was made on this device means that alarm was set/canceled
                            // already when update was made.
                            if(test.getAlarmDeviceId().equals(SettingsService.getInstance().getSimulatedDeviceId())){
                                Timber.i("Alarm [" + test.getAlarmId() + "] was modified on current device [" + test.getAlarmDeviceId() +
                                        "] . No other modification is needed.");
                                continue;
                            }

                            // Do not update test in Firestore db after alarm is reset/canceled
                            // because it will trigger listener on the other device and then the
                            // other device will do same thing (reset/cancel alarm and update) and
                            // this will trigger an infinite loop.
                            switch (change.getType()) {
                                case ADDED:
                                case MODIFIED:
                                    Timber.i("Test for alarm [" + test.getAlarmId() + "] was added or modified. Alarm is resetting.");
                                    // If alarm was launched on other device avoid to reset alarm,
                                    // because this is oneTime alarm and it will be canceled after
                                    // it is launched on this device also.
                                    if(test.isOneTime() && test.isAlarmWasLaunched()){
                                        Timber.i("Alarm [" + test.getAlarmId() + "] was already launched on other devices. Alarm will not be reset.");
                                        break;
                                    }

                                    test.resetAlarm(change.getDocument().getId(), true);
                                    break;
                                case REMOVED:
                                    Timber.i("Test for alarm [" + test.getAlarmId() + "] was removed. Alarm is canceling.");
                                    // If alarm was launched on other device avoid to cancel alarm,
                                    // because this is one time alarm and it will be canceled after
                                    // it is launched on this device also.
                                    if(test.isOneTime() && test.isAlarmWasLaunched()){
                                        Timber.i("Alarm [" + test.getAlarmId() + "] was already launched on other devices. Alarm will not be canceled.");
                                        break;
                                    }

                                    test.cancelAlarm(change.getDocument().getId(), true);
                                    break;
                            }
                        }
                    }
                });
    }

    private void removeAlarmsListener(){
        if (alarmsListener != null){
            alarmsListener.remove();
            alarmsListener = null;
        }
    }
}
