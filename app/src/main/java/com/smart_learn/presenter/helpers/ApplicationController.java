package com.smart_learn.presenter.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smart_learn.BuildConfig;
import com.smart_learn.core.services.NotificationService;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.helpers.DataCallbacks;

import java.util.ArrayList;

import timber.log.Timber;

public class ApplicationController extends Application {

    // for shared preferences
    public static final String LOGIN_STATUS_KEY = "LOGIN_STATUS_KEY";
    public static final String LOGGED_IN = "LOGGED_IN";

    private static ApplicationController applicationController;

    public static ApplicationController getInstance() {return applicationController;}

    @Override
    public void onCreate() {
        super.onCreate();
        applicationController = this;
        if(BuildConfig.DEBUG){
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

                    // TODO: disable addNotificationsCollectionListener listener
                }
                else{
                    addNotificationsCollectionListener();
                }
            }
        };

        // attach listener
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    private void addNotificationsCollectionListener(){

        NotificationService.getInstance().getNotificationsCollection()
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
                            NotificationService.getInstance().processNotification(document, new DataCallbacks.General() {
                                @Override
                                public void onSuccess() {
                                    Timber.i("Processed notification with document id = [%s]", document.getId());
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

}
