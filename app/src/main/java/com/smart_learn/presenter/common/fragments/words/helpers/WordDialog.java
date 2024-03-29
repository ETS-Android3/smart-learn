package com.smart_learn.presenter.common.fragments.words.helpers;

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
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.common.entities.Translation;
import com.smart_learn.databinding.LayoutDialogAddWordBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class WordDialog extends DialogFragment {

    private final WordDialog.Callback callback;
    private WordDialogViewModel dialogViewModel;

    public WordDialog(@NonNull WordDialog.Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogAddWordBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_add_word,null, false);
        dialogBinding.setLifecycleOwner(this);

        setViewModel();

        // link data binding with view model
        dialogBinding.setViewModel(dialogViewModel);

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setTitle(R.string.add_word)
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
                        String submittedWordValue = dialogViewModel.getDialogSubmittedWordValue(dialogBinding);
                        String submittedTranslation = dialogViewModel.getDialogSubmittedTranslation(dialogBinding);
                        if(submittedWordValue != null){
                            ArrayList<Translation> translations = new ArrayList<>();
                            if(submittedTranslation != null){
                                translations.add(new Translation(CoreUtilities.General.generateUniqueId(), submittedTranslation, "", ""));
                            }
                            callback.onAddWord(submittedWordValue, "", "", translations);
                            WordDialog.this.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    private void setViewModel(){
        dialogViewModel = new ViewModelProvider(this).get(WordDialogViewModel.class);
    }

    public interface Callback {
        void onAddWord(@NonNull @NotNull String wordValue, @NonNull @NotNull String phonetic,
                       @NonNull @NotNull String notes, @NonNull @NotNull ArrayList<Translation> translations);
    }

}
