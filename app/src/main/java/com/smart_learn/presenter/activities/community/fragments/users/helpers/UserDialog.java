package com.smart_learn.presenter.activities.community.fragments.users.helpers;

import android.app.Dialog;
import android.os.Bundle;
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
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.databinding.LayoutDialogViewUserBinding;
import com.smart_learn.presenter.helpers.PresenterUtilities;

import org.jetbrains.annotations.NotNull;

public class UserDialog extends DialogFragment {

    private final UserDialog.Callback dialogCallback;
    private final UserDocument userDocument;
    private final boolean isPending;
    private final boolean isFriend;
    private final boolean isRequestReceived;
    private boolean requestAlreadySent;

    public UserDialog(@NonNull UserDialog.Callback dialogCallback, UserDocument userDocument,
                      boolean isPending, boolean isFriend, boolean isRequestReceived) {
        this.dialogCallback = dialogCallback;
        this.userDocument = userDocument;
        this.isPending = isPending;
        this.isFriend = isFriend;
        this.isRequestReceived = isRequestReceived;
        this.requestAlreadySent = false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogViewUserBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_view_user,null, false);
        binding.setLifecycleOwner(this);

        setLayoutUtilities(binding);

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setView(binding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true)
                // No need for a listener because I do no action when BUTTON_NEGATIVE is pressed.
                // Dialog will be dismissed automatically.
                .setNegativeButton(R.string.close, null);

        return builder.create();
    }

    private void setLayoutUtilities(LayoutDialogViewUserBinding binding){
        // get some views
        Button btnSend = binding.btnSendFriendRequestLayoutDialogViewUser;
        Button btnAccept = binding.btnAcceptLayoutDialogViewUser;
        Button btnDisabled = binding.btnDisabledLayoutDialogViewUser;

        // set dialog values
        if(userDocument == null){
            binding.setDisplayName("");
            binding.setEmail("");
            btnSend.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
            btnDisabled.setVisibility(View.GONE);
            return;
        }

        setListeners(btnSend, btnAccept);

        binding.setDisplayName(userDocument.getDisplayName());
        binding.setEmail(userDocument.getEmail());

        // set custom layout based on status (friend > isRequestReceived > isPending > no-status)
        if(isFriend){
            btnSend.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
            btnDisabled.setVisibility(View.VISIBLE);
            btnDisabled.setText(R.string.is_your_friend);
            return;
        }

        if(isRequestReceived){
            btnSend.setVisibility(View.GONE);
            btnAccept.setVisibility(View.VISIBLE);
            btnDisabled.setVisibility(View.GONE);
            return;
        }

        if(isPending){
            btnSend.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
            btnDisabled.setVisibility(View.VISIBLE);
            btnDisabled.setText(R.string.friend_request_sent);
            return;
        }

        // no-status configuration
        btnSend.setVisibility(View.VISIBLE);
        btnAccept.setVisibility(View.GONE);
        btnDisabled.setVisibility(View.GONE);
    }

    private void setListeners(Button btnSend, Button btnAccept){

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a request was already sent then wait until finishes
                if(requestAlreadySent){
                    PresenterUtilities.General.showShortToastMessage(UserDialog.this.requireContext(), UserDialog.this.getString(R.string.request_already_sent));
                    return;
                }

                requestAlreadySent = true;

                dialogCallback.onSendFriendRequest(new Listener() {
                    @Override
                    public void onSuccess() {
                        // if friend request was successfully sent then change button attributes
                        btnSend.setEnabled(false);
                        TextViewCompat.setTextAppearance(btnSend, R.style.AppTheme_ButtonStyle_WithBorder_Info_Disabled);
                        btnSend.setText(R.string.friend_request_sent);
                        btnSend.setCompoundDrawables(null, null,
                                ContextCompat.getDrawable(UserDialog.this.requireContext(), R.drawable.ic_baseline_done_24), null);

                        // A toast message with an error will be shown by the fragment so is not
                        // necessary here.
                    }

                    @Override
                    public void onFailure() {
                        // mark this as false an permit to user to try again
                        requestAlreadySent = false;

                        // A toast message with an error will be shown by the fragment so is not
                        // necessary here.
                    }
                });
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a request was already sent then wait until finishes
                if(requestAlreadySent){
                    PresenterUtilities.General.showShortToastMessage(UserDialog.this.requireContext(), UserDialog.this.getString(R.string.request_already_sent));
                    return;
                }
                requestAlreadySent = true;

                dialogCallback.onAcceptFriendRequest(new Listener() {
                    @Override
                    public void onSuccess() {
                        // if friend request was successfully sent then change button attributes
                        btnAccept.setEnabled(false);
                        TextViewCompat.setTextAppearance(btnAccept, R.style.AppTheme_ButtonStyle_WithBorder_Info_Disabled);
                        btnAccept.setText(R.string.request_accepted);
                        btnAccept.setCompoundDrawables(null, null,
                                ContextCompat.getDrawable(UserDialog.this.requireContext(), R.drawable.ic_baseline_done_24), null);

                        // A toast message with an error will be shown by the fragment so is not
                        // necessary here.
                    }

                    @Override
                    public void onFailure() {
                        // mark this as false an permit to user to try again
                        requestAlreadySent = false;

                        // A toast message with an error will be shown by the fragment so is not
                        // necessary here.
                    }
                });
            }
        });
    }

    public interface Callback {
        void onSendFriendRequest(@NotNull @NonNull UserDialog.Listener listener);
        void onAcceptFriendRequest(@NotNull @NonNull UserDialog.Listener listener);
    }

    public interface Listener {
        void onSuccess();
        void onFailure();
    }

}
