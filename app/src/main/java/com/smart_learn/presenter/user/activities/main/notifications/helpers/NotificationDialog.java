package com.smart_learn.presenter.user.activities.main.notifications.helpers;

import android.app.Dialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.user.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.databinding.LayoutDialogViewNotificationBinding;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;

import org.jetbrains.annotations.NotNull;

public class NotificationDialog extends DialogFragment {

    private final NotificationDocument notificationDocument;
    private final Callback callback;
    private boolean isAlreadyAccepted;

    public NotificationDialog(NotificationDocument notificationDocument, @Nullable Callback callback) {
        this.notificationDocument = notificationDocument;
        this.callback = callback;
        if(notificationDocument != null){
            this.isAlreadyAccepted = notificationDocument.getAccepted();
        }
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
            dialogBinding.setSpannedDescription(new SpannableString(""));
            dialogBinding.setMessage("");
            dialogTitleResourceId = R.string.no_notification_info;
        }
        else {
            dialogBinding.setFrom(notificationDocument.getFromDisplayName() == null ? "" : notificationDocument.getFromDisplayName());
            dialogBinding.setSpannedDescription(NotificationDocument.generateNotificationDescription(notificationDocument.getType(),
                    notificationDocument.getExtraInfo()));
            dialogTitleResourceId = NotificationDocument.generateNotificationTitle(notificationDocument.getType());

            if(notificationDocument.getDocumentMetadata().getCreatedAt() > 0){
                dialogBinding.setCreatedTime(CoreUtilities.General.longToDateTime(notificationDocument.getDocumentMetadata().getCreatedAt()));
            }

            if(!TextUtils.isEmpty(notificationDocument.getMessage())){
                dialogBinding.setMessage(notificationDocument.getMessage());
                // by default if no message exists layout for this is set to View.GONE
                dialogBinding.linearLayoutMessageLayoutDialogViewNotification.setVisibility(View.VISIBLE);
            }

            // special layout is prepared if is a friend request
            if(notificationDocument.getType() == NotificationDocument.Types.TYPE_FRIEND_REQUEST_RECEIVED){
                prepareLayoutForFriendRequest(dialogBinding, notificationDocument);
            }
        }

        return new AlertDialog.Builder(requireActivity())
                .setTitle(dialogTitleResourceId)
                .setView(dialogBinding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true)
                // No need for a listener because I do no action when BUTTON_POSITIVE is pressed.
                // Dialog will be dismissed automatically.
                .setPositiveButton(R.string.close, null)
                .create();
    }

    private void prepareLayoutForFriendRequest(LayoutDialogViewNotificationBinding binding, NotificationDocument notification){

        Button btnAccept = binding.btnAcceptLayoutDialogViewNotificationForFriendRequest;
        Button btnMaybeLater = binding.btnMaybeLaterLayoutDialogViewNotificationForFriendRequest;
        Button btnDisabled = binding.btnDisabledLayoutLayoutDialogViewNotificationForFriendRequest;

        if(notification.getAccepted()){
            btnAccept.setVisibility(View.GONE);
            btnMaybeLater.setVisibility(View.GONE);
            btnDisabled.setVisibility(View.VISIBLE);
            return;
        }

        if(notification.isFriendAccountMarkedForDeletion()){
            btnAccept.setVisibility(View.GONE);
            btnMaybeLater.setVisibility(View.GONE);
            btnDisabled.setVisibility(View.GONE);
            binding.tvAccountMarkedForDeletionLayoutDialogViewNotification.setVisibility(View.VISIBLE);
            return;
        }

        btnAccept.setVisibility(View.VISIBLE);
        btnMaybeLater.setVisibility(View.VISIBLE);
        btnDisabled.setVisibility(View.GONE);

        // set listeners if notification is not accepted/decline
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback == null || isAlreadyAccepted){
                    PresenterUtilities.General.showShortToastMessage(NotificationDialog.this.requireContext(),
                            NotificationDialog.this.getString(R.string.error_accept_request));
                    return;
                }

                isAlreadyAccepted = true;

                callback.onAcceptPress(new Listener() {
                    @Override
                    public void onSuccess() {
                        // if friend request was successfully accepted then change button attributes
                        btnAccept.setEnabled(false);
                        TextViewCompat.setTextAppearance(btnAccept, R.style.AppTheme_ButtonStyle_WithBorder_Info_Disabled);
                        btnAccept.setText(R.string.request_accepted);
                        btnAccept.setCompoundDrawables(null, null,
                                ContextCompat.getDrawable(NotificationDialog.this.requireContext(), R.drawable.ic_baseline_done_24), null);

                        // and disable maybe later button
                        btnMaybeLater.setVisibility(View.GONE);

                        // A toast message with an error will be shown by the fragment so is not
                        // necessary here.
                    }

                    @Override
                    public void onFailure() {
                        isAlreadyAccepted = false;
                        // A toast message with an error will be shown by the fragment so is not
                        // necessary here.
                    }
                });
            }
        });

        btnMaybeLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationDialog.this.dismiss();
            }
        });
    }

    public interface Callback {
        void onAcceptPress(@NotNull @NonNull Listener listener);
    }

    public interface Listener {
        void onSuccess();
        void onFailure();
    }
}