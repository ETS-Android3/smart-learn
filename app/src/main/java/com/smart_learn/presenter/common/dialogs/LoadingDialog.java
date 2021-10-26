package com.smart_learn.presenter.common.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.databinding.LoadingDialogLayoutBinding;

public class LoadingDialog extends DialogFragment {

    private final String message;

    public LoadingDialog(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // set data binding
        LoadingDialogLayoutBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.loading_dialog_layout,null, false);
        dialogBinding.setLifecycleOwner(this);
        dialogBinding.setMessage(message);

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setView(dialogBinding.getRoot())
                .setCancelable(false);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}