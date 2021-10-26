package com.smart_learn.presenter.user.fragments.common.friends.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.user.services.FriendService;
import com.smart_learn.core.common.helpers.ConnexionChecker;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.presenter.user.fragments.common.friends.BasicFriendsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class StandardFriendsViewModel extends BasicFriendsViewModel {

    private final AtomicBoolean isRemovingActive;

    public StandardFriendsViewModel(@NonNull @NotNull Application application) {
        super(application);
        isRemovingActive = new AtomicBoolean(false);
    }

    public void removeFriend(@NonNull @NotNull StandardFriendsFragment<?> fragment, DocumentSnapshot friendSnapshot){
        if(isRemovingActive.get()){
            return;
        }
        isRemovingActive.set(true);

        fragment.showProgressDialog("", fragment.getString(R.string.removing_friend));

        // check if connection is available in order to continue with sending friend request
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> continueWithRemovingFriend(fragment, friendSnapshot));
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
                fragment.requireActivity().runOnUiThread(fragment::closeProgressDialog);
                isRemovingActive.set(false);
            }
        }).check();
    }

    private void continueWithRemovingFriend(StandardFriendsFragment<?> fragment, DocumentSnapshot friendSnapshot){
        FriendService.getInstance().removeFriend(friendSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                fragment.requireActivity().runOnUiThread(fragment::closeProgressDialog);
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_remove_friend));
                isRemovingActive.set(false);
            }

            @Override
            public void onFailure() {
                fragment.requireActivity().runOnUiThread(fragment::closeProgressDialog);
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_remove_friend));
                isRemovingActive.set(false);
            }
        });
    }

}
