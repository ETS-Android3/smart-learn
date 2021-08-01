package com.smart_learn.presenter.helpers.fragments.tests.history.helpers;

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

import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.databinding.LayoutDialogTestInfoBinding;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;


public class TestInfoDialog extends DialogFragment {

    private final TestInfoDialog.Callback dialogCallback;
    private final Test testInfo;

    private final String generationDateDescription;
    private final String testTypeDescription;
    private final String valuesSelectionDescription;
    private final String counterDescription;
    private final String progressDescription;
    private final String extraProgressDescription;
    private final String totalTestTimeDescription;


    public TestInfoDialog(@NonNull @NotNull Test test, @NonNull @NotNull TestInfoDialog.Callback dialogCallback) {
        this.dialogCallback = dialogCallback;
        this.testInfo = test;

        // set specific values for views
        generationDateDescription = CoreUtilities.General.longToDate(testInfo.getTestGenerationDate());
        testTypeDescription = Test.getTestTypeDescription(testInfo.getType());
        totalTestTimeDescription = testInfo.getTotalTimeDescription();

        if(testInfo.getNrOfValuesForGenerating() == Test.USE_ALL){
            valuesSelectionDescription = testInfo.isUseCustomSelection() ?
                    ApplicationController.getInstance().getString(R.string.custom_selection) :
                    ApplicationController.getInstance().getString(R.string.use_all);
        }
        else{
            valuesSelectionDescription = ApplicationController.getInstance().getString(R.string.use_specific_number_of_selections) +
                    " (" + testInfo.getNrOfValuesForGenerating() + ")";
        }

        counterDescription = testInfo.getQuestionCounter() == Test.NO_COUNTER ?
                ApplicationController.getInstance().getString(R.string.no) :
                ApplicationController.getInstance().getString(R.string.yes) + " (" + testInfo.getQuestionCounter() + ")";


        if(testInfo.getTotalQuestions() != 0){
            if(testInfo.isFinished()){
                progressDescription = CoreUtilities.General.formatFloatValue(testInfo.getSuccessRate()) + " %";
                extraProgressDescription = testInfo.getCorrectAnswers() + " " + ApplicationController.getInstance().getString(R.string.from) + " " + testInfo.getTotalQuestions();
            }
            else{
                progressDescription = testInfo.getAnsweredQuestions() + "/" + testInfo.getTotalQuestions();
                extraProgressDescription = CoreUtilities.General.formatFloatValue(testInfo.getSuccessRate()) + " %";
            }
        }
        else{
            progressDescription = "";
            extraProgressDescription = "";
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // set data binding
        LayoutDialogTestInfoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_dialog_test_info,null, false);
        binding.setLifecycleOwner(this);

        // set binding with variables
        binding.setTestInfo(testInfo);
        binding.setGenerationDateDescription(generationDateDescription);
        binding.setTestTypeDescription(testTypeDescription);
        binding.setValuesSelectionDescription(valuesSelectionDescription);
        binding.setCounterDescription(counterDescription);
        binding.setProgressDescription(progressDescription);
        binding.setExtraProgressDescription(extraProgressDescription);
        binding.setTotalTestTimeDescription(totalTestTimeDescription);

        // build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // set dialog characteristics
        builder.setView(binding.getRoot())
                // If setCancelable is true when you click beside the dialog the dialog is dismissed.
                .setCancelable(true);

        if(testInfo.isFinished()){
                builder.setPositiveButton(R.string.see_results, null)
                        .setNegativeButton(R.string.close, null);
        }
        else {
            // test is in progress
            if(testInfo.getAnsweredQuestions() > 0){
                builder.setPositiveButton(R.string.see_progress, null)
                        .setNeutralButton(R.string.continue_test, null)
                        .setNegativeButton(R.string.close, null);
            }
            else{
                builder.setNeutralButton(R.string.continue_test, null)
                        .setNegativeButton(R.string.close, null);
            }
        }

        AlertDialog dialog = builder.create();

        // Override onclick for positive button to turn off auto dismiss when BUTTON_POSITIVE is clicked.
        // Check this to see we is need an override.
        // https://www.xspdf.com/help/50319382.html
        // https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked/10661281#10661281
        // https://stackoverflow.com/questions/6142308/android-dialog-keep-dialog-open-when-button-is-pressed/6142413#6142413
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonPositive = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        dialogCallback.onSeeResults();
                    }
                });

                Button buttonNeutral = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEUTRAL);
                buttonNeutral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        dialogCallback.onContinueTest();
                    }
                });
            }
        });

        return dialog;
    }


    public interface Callback {
        void onSeeResults();
        void onContinueTest();
    }
}