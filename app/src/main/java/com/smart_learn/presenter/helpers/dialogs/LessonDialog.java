package com.smart_learn.presenter.helpers.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.databinding.DialogNewLessonBinding;
import com.smart_learn.presenter.activities.lesson.old.LessonDialogViewModel;

/** https://developer.android.com/guide/topics/ui/dialogs */
public class LessonDialog extends DialogFragment {

    private final Lesson lesson;
    private final boolean setCancelable;
    private final int title;
    private final int positiveButton;
    private final int negativeButton;
    private final DialogActionsCallback<DialogNewLessonBinding> dialogActionsCallback;
    private LessonDialogViewModel lessonDialogViewModel;


    /**
     * @param lesson Lesson to link with ViewModel which will be shown in dialog. If NULL ViewModel
     *              default info`s will be shown.
     * @param setCancelable If setCancelable is true when you click beside the dialog the dialog is
     *                     dismissed
     * @param title Resource string id for dialog title
     * @param positiveButton The resource id of the text to display in the positive button
     * @param negativeButton The resource id of the text to display in the negative button
     * @param dialogActionsCallback Callback for actions related to positive and negative buttons
     * */
    public LessonDialog(@Nullable Lesson lesson, boolean setCancelable, int title, int positiveButton,
                        int negativeButton, DialogActionsCallback<DialogNewLessonBinding> dialogActionsCallback) {
        this.lesson = lesson;
        this.setCancelable = setCancelable;
        this.title = title;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.dialogActionsCallback = dialogActionsCallback;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // set data binding
        DialogNewLessonBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_new_lesson,null, false);
        dialogBinding.setLifecycleOwner(this);

        setViewModel();

        // link data binding with view model
        dialogBinding.setViewModel(lessonDialogViewModel);

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setTitle(title)
                .setView(dialogBinding.getRoot())
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
                dialogActionsCallback.onShowDialog(dialogInterface,dialogBinding);
            }
        });

        return dialog;
    }

    private void setViewModel(){
        lessonDialogViewModel = new ViewModelProvider(this).get(LessonDialogViewModel.class);
        if(lesson != null){
            lessonDialogViewModel.setLiveLessonInfo(lesson);
        }
    }

}
