package com.smart_learn.presenter.activities.dialogs;

import android.content.DialogInterface;

import com.smart_learn.databinding.DialogNewLessonBinding;

public interface DialogActionsCallback <T> {
    void onShowDialog(DialogInterface dialogInterface, T dialogBinding);
    default void onPositiveButtonPressed(DialogInterface dialog, T dialogBinding){}
}
