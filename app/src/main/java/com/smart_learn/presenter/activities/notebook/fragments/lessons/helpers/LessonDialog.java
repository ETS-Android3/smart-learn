package com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.databinding.LayoutDialogAddLessonBinding;

/** https://developer.android.com/guide/topics/ui/dialogs */
public class LessonDialog extends DialogFragment {

    private final LessonDialog.Callback dialogCallback;
    private LessonDialogViewModel lessonDialogViewModel;

    /**
     * @param dialogCallback Callback for actions related to positive and negative buttons
     * */
    public LessonDialog(@NonNull LessonDialog.Callback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogAddLessonBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_add_lesson,null, false);
        dialogBinding.setLifecycleOwner(this);

        setViewModel();

        // link data binding with view model
        dialogBinding.setViewModel(lessonDialogViewModel);

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setTitle(R.string.add_lesson)
                .setView(dialogBinding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true)
                // add action buttons

                // Set to null. We override the onclick.
                .setPositiveButton(R.string.save, null)

                // No need for a listener because I do no action when BUTTON_NEGATIVE is pressed.
                // Dialog will be dismissed automatically.
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();

        // Override onclick for positive button to turn off auto dismiss when BUTTON_POSITIVE is clicked.
        // Check this to see we is need an override.
        // https://www.xspdf.com/help/50319382.html
        // https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked/10661281#10661281
        // https://stackoverflow.com/questions/6142308/android-dialog-keep-dialog-open-when-button-is-pressed/6142413#6142413
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Lesson submittedLesson = lessonDialogViewModel.getDialogSubmittedLesson(dialogBinding);
                        if(submittedLesson != null){
                            dialogCallback.onAddLesson(submittedLesson);
                            LessonDialog.this.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private void setViewModel(){
        lessonDialogViewModel = new ViewModelProvider(this).get(LessonDialogViewModel.class);
    }

    public interface Callback {
        void onAddLesson(@NonNull Lesson lesson);
    }

}