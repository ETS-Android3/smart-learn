package com.smart_learn.presenter.activities.dialogs;

import android.content.DialogInterface;

import com.smart_learn.databinding.DialogNewLessonBinding;

public interface DialogActionsCallback {
    void onShowDialog(DialogInterface dialogInterface, DialogNewLessonBinding dialogBinding);
    default void onPositiveButtonPressed(DialogInterface dialog, DialogNewLessonBinding dialogBinding){}
}
