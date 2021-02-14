package com.smart_learn.presenter.activities.dialogs;

import android.content.DialogInterface;
import android.view.View;

public interface DialogButtonsActionCallback {
    void onPositiveButtonPressed(View view, DialogInterface dialog, int which);
    default void onNeutralButtonPressed(View view, DialogInterface dialog, int which){}
    default void onNegativeButtonPressed(View view, DialogInterface dialog, int which){}
}
