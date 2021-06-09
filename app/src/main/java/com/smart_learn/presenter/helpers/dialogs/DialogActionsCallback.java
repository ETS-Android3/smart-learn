package com.smart_learn.presenter.helpers.dialogs;

import android.content.DialogInterface;

public interface DialogActionsCallback <T> {
    void onShowDialog(DialogInterface dialogInterface, T dialogBinding);
    default void onPositiveButtonPressed(DialogInterface dialog, T dialogBinding){}
}
