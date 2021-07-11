package com.smart_learn.presenter.activities.main.fragments.notifications;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.FriendService;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.presenter.activities.main.fragments.notifications.helpers.NotificationDialog;
import com.smart_learn.presenter.activities.main.fragments.notifications.helpers.NotificationsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class NotificationsViewModel extends BasicViewModelForRecyclerView<NotificationsAdapter> {

    public NotificationsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void acceptFriendRequest(@NonNull @NotNull NotificationsFragment fragment,
                                       DocumentSnapshot notificationSnapshot,
                                       @NonNull @NotNull NotificationDialog.Listener listener){

        if(DataUtilities.Firestore.notGoodDocumentSnapshot(notificationSnapshot)){
            liveToastMessage.setValue(fragment.getString(R.string.error_accept_request));
            listener.onFailure();
            return;
        }

        NotificationDocument notification = notificationSnapshot.toObject(NotificationDocument.class);
        if(notification == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_accept_request));
            listener.onFailure();
            Timber.w("notification is null");
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.accepting_request));

        // check if connection is available in order to continue with sending friend request
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> continueWithAcceptingRequest(fragment, notification, listener));
            }
            @Override
            public void networkDisabled() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_network));
            }

            @Override
            public void internetNotAvailable() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_internet_connection));
            }
            @Override
            public void notConnected() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    listener.onFailure();
                });
            }
        }).check();

    }

    private void continueWithAcceptingRequest(NotificationsFragment fragment, NotificationDocument notification,
                                              NotificationDialog.Listener listener){

        // extract userSnapshot from notificationSnapshot
        notification
                .getFromDocumentReference()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                            fragment.requireActivity().runOnUiThread(() -> {
                                fragment.closeProgressDialog();
                                liveToastMessage.setValue(fragment.getString(R.string.error_accept_request));
                                listener.onFailure();
                            });
                            return;
                        }

                        // try to accept request
                        FriendService.getInstance().acceptFriendRequest(task.getResult(), new DataCallbacks.General() {
                            @Override
                            public void onSuccess() {
                                fragment.requireActivity().runOnUiThread(() -> {
                                    fragment.closeProgressDialog();
                                    listener.onSuccess();
                                });
                            }

                            @Override
                            public void onFailure() {
                                fragment.requireActivity().runOnUiThread(() -> {
                                    fragment.closeProgressDialog();
                                    liveToastMessage.setValue(fragment.getString(R.string.error_accept_request));
                                    listener.onFailure();
                                });
                            }
                        });
                    }
                });
    }

}
