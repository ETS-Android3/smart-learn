package com.smart_learn.presenter.helpers.fragments.friends.standard;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.presenter.helpers.fragments.friends.standard.helpers.FriendDialog;
import com.smart_learn.presenter.helpers.fragments.friends.BasicFriendsFragment;

import org.jetbrains.annotations.NotNull;


public abstract class StandardFriendsFragment <VM extends StandardFriendsViewModel> extends BasicFriendsFragment<VM> {

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
        showFriendDialog(item);
    }

    @Override
    protected void onAdapterRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot) {
        viewModel.removeFriend(StandardFriendsFragment.this, friendSnapshot);
    }

    private void showFriendDialog(@NonNull @NotNull DocumentSnapshot friendSnapshot){
        DialogFragment dialogFragment = new FriendDialog(new FriendDialog.Callback() {
            @Override
            public void onRemoveFriend() {
                viewModel.removeFriend(StandardFriendsFragment.this, friendSnapshot);
            }
        }, friendSnapshot.toObject(FriendDocument.class));

        dialogFragment.show(requireActivity().getSupportFragmentManager(), "StandardFriendsFragment");
    }

}