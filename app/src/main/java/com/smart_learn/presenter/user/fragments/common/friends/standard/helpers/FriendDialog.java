package com.smart_learn.presenter.user.fragments.common.friends.standard.helpers;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.user.firebase.firestore.entities.FriendDocument;
import com.smart_learn.databinding.LayoutDialogViewFriendBinding;
import com.smart_learn.presenter.common.helpers.PresenterCallbacks;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;

public class FriendDialog extends DialogFragment {

    private final FriendDialog.Callback dialogCallback;
    private final FriendDocument friendDocument;

    /**
     * @param dialogCallback Callback for action related to positive press on remove friend.
     * @param friendDocument FriendDocument used to populate dialog fields.
     * */
    public FriendDialog(@NonNull FriendDialog.Callback dialogCallback, FriendDocument friendDocument) {
        this.dialogCallback = dialogCallback;
        this.friendDocument = friendDocument;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogViewFriendBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_view_friend,null, false);
        dialogBinding.setLifecycleOwner(this);

        dialogBinding.btnRemoveFriendLayoutDialogViewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.remove_friend);
                String description = getString(R.string.remove_friend_description);
                String positiveButtonDescription = getString(R.string.remove);
                PresenterUtilities.Activities.showStandardAlertDialog(requireContext(), title, description, positiveButtonDescription,
                        new PresenterCallbacks.StandardAlertDialogCallback() {
                            @Override
                            public void onPositiveButtonPress() {
                                dialogCallback.onRemoveFriend();
                                FriendDialog.this.dismiss();
                            }
                        });
            }
        });

        // set dialog info
        if(friendDocument == null){
            dialogBinding.setDisplayName("");
            dialogBinding.setEmail("");
            dialogBinding.setFriendsSince("");
            dialogBinding.setAccountMarkedForDeletion(false);
        }
        else {
            PresenterUtilities.Activities.loadProfileImage(friendDocument.getProfilePhotoUrl(), dialogBinding.ivProfileLayoutDialogViewFriend);
            dialogBinding.setDisplayName(friendDocument.getDisplayName());
            dialogBinding.setEmail(friendDocument.getEmail());
            dialogBinding.setFriendsSince(CoreUtilities.General.longToDateTime(friendDocument.getFriendsSince()));
            dialogBinding.setAccountMarkedForDeletion(friendDocument.isAccountMarkedForDeletion());
        }

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setView(dialogBinding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true)
                // No need for a listener because I do no action when BUTTON_NEGATIVE is pressed.
                // Dialog will be dismissed automatically.
                .setNegativeButton(R.string.close, null);

        return builder.create();
    }

    public interface Callback {
        void onRemoveFriend();
    }

}