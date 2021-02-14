package com.smart_learn.presenter.activities.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/** https://developer.android.com/guide/topics/ui/dialogs */
public class SettingsDialog extends DialogFragment {

    private final boolean setCancelable;
    private final int title;
    private final int layout;


    /**
     * @param setCancelable If setCancelable is true when you click beside the dialog the dialog is
     *                     dismissed
     * @param title Resource string id for dialog title
     * @param layout Resource id for dialog custom layout
     * */
    public SettingsDialog(boolean setCancelable, int title, int layout) {
        this.setCancelable = setCancelable;
        this.title = title;
        this.layout = layout;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // set dialog characteristics
        builder.setTitle(title)
                .setView(inflater.inflate(layout, null))
                // if setCancelable is true when you click beside the dialog the dialog is dismissed
                .setCancelable(setCancelable);

        // Create the AlertDialog object and return it
        return builder.create();
    }

}

