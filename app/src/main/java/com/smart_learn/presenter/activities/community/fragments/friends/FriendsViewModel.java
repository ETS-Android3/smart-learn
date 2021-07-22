package com.smart_learn.presenter.activities.community.fragments.friends;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.FriendService;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.adapters.friends.FriendsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class FriendsViewModel extends BasicViewModelForRecyclerView<FriendsAdapter> {

    private final AtomicBoolean isRemovingActive;

    public FriendsViewModel(@NonNull @NotNull Application application) {
        super(application);
        isRemovingActive = new AtomicBoolean(false);
    }

    public void removeFriend(@NonNull @NotNull FriendsFragment fragment, DocumentSnapshot friendSnapshot){
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

    private void continueWithRemovingFriend(FriendsFragment fragment, DocumentSnapshot friendSnapshot){
        FriendService.getInstance().removeFriend(friendSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.success_remove_friend));
                });
                isRemovingActive.set(false);
            }

            @Override
            public void onFailure() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_remove_friend));
                });
                isRemovingActive.set(false);
            }
        });
    }
}
