package com.smart_learn.presenter.activities.main.fragments.notifications.helpers;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.databinding.LayoutDialogViewNotificationBinding;

public class NotificationDialog extends DialogFragment {

    private final NotificationDocument notificationDocument;

    public NotificationDialog(NotificationDocument notificationDocument) {
        this.notificationDocument = notificationDocument;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogViewNotificationBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_view_notification,null, false);
        dialogBinding.setLifecycleOwner(this);

        int dialogTitleResourceId;

        // set dialog info
        if(notificationDocument == null){
            dialogBinding.setFrom("");
            dialogBinding.setCreatedTime("");
            dialogBinding.setDescription("");
            dialogTitleResourceId = R.string.no_notification_info;
        }
        else {
            dialogBinding.setFrom(notificationDocument.getFrom());
            dialogBinding.setCreatedTime(CoreUtilities.General.longToDateTime(notificationDocument.getDocumentMetadata().getCreatedAt()));
            dialogBinding.setDescription(notificationDocument.getDescription());
            dialogTitleResourceId = NotificationDocument.getNotificationTypeMessage(notificationDocument.getType());
        }

        return new AlertDialog.Builder(requireActivity())
                .setTitle(dialogTitleResourceId)
                .setView(dialogBinding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true)
                // No need for a listener because I do no action when BUTTON_POSITIVE is pressed.
                // Dialog will be dismissed automatically.
                .setPositiveButton(R.string.ok, null)
                .create();
    }
}