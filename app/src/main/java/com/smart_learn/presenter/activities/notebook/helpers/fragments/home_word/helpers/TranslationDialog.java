package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.helpers;

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
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.databinding.LayoutDialogTranslationBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class TranslationDialog extends DialogFragment {

    private final TranslationDialog.Callback callback;
    private final boolean forExpression;
    private final boolean view;
    private final boolean update;
    private final boolean isOwner;
    private final Translation translation;
    private final ArrayList<Translation> allTranslations;
    private TranslationDialogViewModel dialogViewModel;

    private final int saveButtonTitle;
    private final int dialogTitle;

    public TranslationDialog(boolean forExpression, boolean view, boolean update, boolean isOwner, Translation translation,
                             @NonNull ArrayList<Translation> allTranslations, @NonNull TranslationDialog.Callback callback) {
        if(view && update){
            throw new UnsupportedOperationException("Must choose or view or update");
        }
        if((view || update) && translation == null){
            throw new UnsupportedOperationException("For view or update translation must not be null");
        }
        this.forExpression = forExpression;
        this.view = view;
        this.update = update;
        this.isOwner = isOwner;
        this.translation = translation;
        this.allTranslations = allTranslations;
        this.callback = callback;

        if(!isOwner){
            saveButtonTitle = R.string.close;
            dialogTitle = R.string.translation_overview;
            return;
        }

        if(view || update){
            if(view){
                saveButtonTitle = R.string.change_info;
                dialogTitle = R.string.translation_overview;
            }
            else {
                saveButtonTitle = R.string.update;
                dialogTitle = R.string.update_translation;
            }
        }
        else {
            saveButtonTitle = R.string.add;
            dialogTitle = R.string.add_translation;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogTranslationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_translation,null, false);
        binding.setLifecycleOwner(this);

        setViewModel();

        // link data binding with view model
        binding.setViewModel(dialogViewModel);

        if(forExpression){
            setLayoutForExpression(binding);
        }
        else{
            setLayoutForWord(binding);
        }

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setTitle(dialogTitle)
                .setView(binding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true);

        if(!isOwner){
            builder.setNegativeButton(R.string.close, null);
            return builder.create();
        }

        // here is owner
        builder.setPositiveButton(saveButtonTitle, null)
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();

        if(view){
            setListenerForViewTranslation(dialog, binding);
            return dialog;
        }

        setListenerForAddOrUpdateTranslation(dialog, binding);
        return dialog;
    }

    private void setLayoutForExpression(LayoutDialogTranslationBinding binding){
        binding.linearLayoutWordLayoutDialogTranslation.setVisibility(View.GONE);
        binding.linearLayoutExpressionLayoutDialogTranslation.setVisibility(View.VISIBLE);
        dialogViewModel.setForExpression(true);

        if(view || update){
            String previousValue = translation.getTranslation() == null ? "" : translation.getTranslation();
            dialogViewModel.setLiveValue(previousValue);

            if(view){
                binding.layoutExpressionAddOrUpdateLayoutDialogTranslation.setVisibility(View.GONE);
                binding.layoutExpressionViewLayoutDialogTranslation.setVisibility(View.VISIBLE);
            }
            else {
                binding.layoutExpressionAddOrUpdateLayoutDialogTranslation.setVisibility(View.VISIBLE);
                binding.layoutExpressionViewLayoutDialogTranslation.setVisibility(View.GONE);

                dialogViewModel.setUpdate(true);
                dialogViewModel.setPreviousValue(previousValue);
            }
        }
        else{ // for add new translation
            binding.layoutExpressionAddOrUpdateLayoutDialogTranslation.setVisibility(View.VISIBLE);
            binding.layoutExpressionViewLayoutDialogTranslation.setVisibility(View.GONE);
        }
    }

    private void setLayoutForWord(LayoutDialogTranslationBinding binding){
        if(view || update){
            String previousValue = translation.getTranslation() == null ? "" : translation.getTranslation();
            dialogViewModel.setLiveValue(previousValue);
            dialogViewModel.setLivePhonetic(translation.getPhonetic() == null ? "" : translation.getPhonetic());

            if(view){
                binding.layoutWordAddOrUpdateLayoutDialogTranslation.setVisibility(View.GONE);
                binding.layoutWordViewLayoutDialogTranslation.setVisibility(View.VISIBLE);
            }
            else {
                dialogViewModel.setUpdate(true);
                dialogViewModel.setPreviousValue(previousValue);
            }
        }
    }

    private void setListenerForAddOrUpdateTranslation(AlertDialog dialog, LayoutDialogTranslationBinding binding){
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String submittedValue;
                        String submittedPhonetic;
                        if(forExpression){
                            submittedValue = dialogViewModel.getDialogSubmittedValue(binding.etExpressionTranslationValueLayoutDialogTranslation);
                            submittedPhonetic = "";
                        }
                        else{
                            submittedValue = dialogViewModel.getDialogSubmittedValue(binding.etWordTranslationValueLayoutDialogTranslation);
                            submittedPhonetic = dialogViewModel.getDialogSubmittedPhonetic(binding.etWordPhoneticLayoutDialogTranslation);
                        }
                        if(submittedValue != null && submittedPhonetic != null){
                            Translation newTranslation = new Translation(submittedValue, submittedPhonetic, "");
                            callback.onAddOrUpdatePositiveButtonPress(newTranslation);
                            TranslationDialog.this.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void setListenerForViewTranslation(AlertDialog dialog, LayoutDialogTranslationBinding binding){
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TranslationDialog.this.dismiss();
                        callback.onViewPositiveButtonPress();
                    }
                });
            }
        });
    }

    private void setViewModel(){
        dialogViewModel = new ViewModelProvider(this).get(TranslationDialogViewModel.class);

        // set translations
        dialogViewModel.setAllTranslations(allTranslations);
    }

    public interface Callback {
        default void onAddOrUpdatePositiveButtonPress(@NonNull @NotNull Translation newTranslation){}
        default void onViewPositiveButtonPress(){}
    }

}
