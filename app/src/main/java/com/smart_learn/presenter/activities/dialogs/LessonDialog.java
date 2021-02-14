package com.smart_learn.presenter.activities.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/** https://developer.android.com/guide/topics/ui/dialogs */
public class LessonDialog extends DialogFragment {

    private final boolean setCancelable;
    private final int title;
    private final int layout;
    private final int positiveButton;
    private final int negativeButton;
    private final DialogButtonsActionCallback dialogButtonsActionCallback;


    /**
     * @param setCancelable If setCancelable is true when you click beside the dialog the dialog is
     *                     dismissed
     * @param title Resource string id for dialog title
     * @param layout Resource id for dialog custom layout
     * @param positiveButton The resource id of the text to display in the positive button
     * @param negativeButton The resource id of the text to display in the negative button
     * @param dialogButtonsActionCallback Callback for actions related to positive and negative buttons
     * */
    public LessonDialog(boolean setCancelable, int title, int layout, int positiveButton, int negativeButton,
                        DialogButtonsActionCallback dialogButtonsActionCallback) {
        this.setCancelable = setCancelable;
        this.title = title;
        this.layout = layout;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.dialogButtonsActionCallback = dialogButtonsActionCallback;
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
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(setCancelable)
                // add action buttons

                // Set to null. We override the onclick.
                .setPositiveButton(positiveButton, null)

                // No need for a listener because I do no action when BUTTON_NEGATIVE is pressed.
                // Dialog will be dismissed automatically.
                .setNegativeButton(negativeButton, null);



        AlertDialog dialog = builder.create();

        // Override onclick for positive button to turn off auto dismiss when BUTTON_POSITIVE is clicked.
        // Check this to see we is need an override.
        // https://www.xspdf.com/help/50319382.html
        // https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked/10661281#10661281
        // https://stackoverflow.com/questions/6142308/android-dialog-keep-dialog-open-when-button-is-pressed/6142413#6142413
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialogButtonsActionCallback.onPositiveButtonPressed(view, dialogInterface,
                                AlertDialog.BUTTON_POSITIVE);
                    }
                });
            }
        });

        return dialog;
    }

}
