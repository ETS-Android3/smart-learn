package com.smart_learn.presenter.activities.test.helpers.fragments.local_test_setup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.databinding.FragmentLocalTestSetupBinding;
import com.smart_learn.presenter.activities.test.TestSharedViewModel;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public abstract class LocalTestSetupFragment <VM extends LocalTestSetupViewModel> extends BasicFragment<VM> {

    protected FragmentLocalTestSetupBinding binding;
    @Getter
    protected TestSharedViewModel sharedViewModel;

    protected RadioGroup radioGroupValues;
    protected RadioGroup radioGroupCounter;
    protected NumberPicker numberPickerValues;
    protected NumberPicker numberPickerCounter;

    protected void navigateToSelectWordsFragment(){}
    protected void navigateToSelectExpressionsFragment(){}
    protected void saveScheduledTest(Test scheduledTest){}
    protected void generateTest(){}

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentLocalTestSetupBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    protected void setLayoutUtilities(){
        numberPickerValues = binding.numberPickerValuesFragmentLocalTestSetup;
        numberPickerCounter = binding.numberPickerCounterFragmentLocalTestSetup;
        radioGroupValues = binding.radioGroupValuesSelectionFragmentLocalTestSetup;
        radioGroupCounter = binding.radioGroupCounterFragmentLocalTestSetup;

        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_unable_to_continue_with_setup);
            this.requireActivity().onBackPressed();
            return;
        }

        // values selection setup
        radioGroupValues.check(R.id.rb_use_specific_number_fragment_local_test_setup);
        numberPickerValues.setMinValue(Test.MIN_CUSTOM_SELECTED_VALUES);
        switch (sharedViewModel.getGeneratedTest().getType()){
            case Test.Types.WORD_WRITE:
            case Test.Types.WORD_QUIZ:
            case Test.Types.WORD_MIXED_LETTERS:
                if(sharedViewModel.getNrOfLessonWords() < 1){
                    showMessage(R.string.error_lesson_has_no_words);
                    this.requireActivity().onBackPressed();
                    return;
                }
                numberPickerValues.setMaxValue(sharedViewModel.getNrOfLessonWords());
                break;
            case Test.Types.EXPRESSION_MIXED_WORDS:
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                if(sharedViewModel.getNrOfLessonExpressions() < 1){
                    showMessage(R.string.error_lesson_has_no_expressions);
                    this.requireActivity().onBackPressed();
                    return;
                }
                numberPickerValues.setMaxValue(sharedViewModel.getNrOfLessonExpressions());
                break;
            default:
                showMessage(R.string.error_unable_to_continue_with_setup);
                this.requireActivity().onBackPressed();
                return;
        }

        // counter setup
        radioGroupCounter.check(R.id.rb_no_counter_fragment_local_test_setup);
        numberPickerCounter.setEnabled(false);
        numberPickerCounter.setMinValue(Test.MIN_QUESTION_COUNTER_TIME);
        numberPickerCounter.setMaxValue(Test.MAX_QUESTION_COUNTER_TIME);

        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity)requireActivity(), getString(R.string.select_test_options));
        if(sharedViewModel.getGeneratedTest() != null){
            sharedViewModel.getGeneratedTest().setNrOfValuesForGenerating(Test.USE_ALL);
            sharedViewModel.getGeneratedTest().setQuestionCounter(Test.NO_COUNTER);
        }
    }

    private void setListeners(){
        binding.btnNextFragmentLocalTestSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSelections();
            }
        });

        radioGroupValues.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_use_specific_number_fragment_local_test_setup){
                    numberPickerValues.setEnabled(true);
                    return;
                }
                numberPickerValues.setEnabled(false);
            }
        });

        radioGroupCounter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_yes_counter_fragment_local_test_setup){
                    numberPickerCounter.setEnabled(true);
                    return;
                }
                numberPickerCounter.setEnabled(false);
            }
        });
    }

    private void processSelections(){
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }

        // extract values
        sharedViewModel.getGeneratedTest().setUseCustomSelection(false);
        if(radioGroupValues.getCheckedRadioButtonId() == R.id.rb_use_specific_number_fragment_local_test_setup){
            sharedViewModel.getGeneratedTest().setNrOfValuesForGenerating(numberPickerValues.getValue());
        }
        else{
            sharedViewModel.getGeneratedTest().setNrOfValuesForGenerating(Test.USE_ALL);
            if(radioGroupValues.getCheckedRadioButtonId() == R.id.rb_use_custom_selection_fragment_local_test_setup){
                sharedViewModel.getGeneratedTest().setUseCustomSelection(true);
            }
        }

        if(radioGroupCounter.getCheckedRadioButtonId() == R.id.rb_yes_counter_fragment_local_test_setup){
            sharedViewModel.getGeneratedTest().setQuestionCounter(numberPickerCounter.getValue());
        }
        else{
            sharedViewModel.getGeneratedTest().setQuestionCounter(Test.NO_COUNTER);
        }

        // go to specific fragments based on different values

        // if custom selection will be used then user will select specific words/expressions
        if(sharedViewModel.getGeneratedTest().isUseCustomSelection()){

            switch (sharedViewModel.getGeneratedTest().getType()){
                case Test.Types.WORD_WRITE:
                case Test.Types.WORD_QUIZ:
                case Test.Types.WORD_MIXED_LETTERS:
                    navigateToSelectWordsFragment();
                    return;
                case Test.Types.EXPRESSION_MIXED_WORDS:
                case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                    navigateToSelectExpressionsFragment();
                    return;
                default:
                    GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_can_not_continue));
            }
            return;
        }

        // If test is scheduled and no custom selection is set then generation will be made when
        // test will be taken.
        if(sharedViewModel.getGeneratedTest().isScheduled()){
            saveScheduledTest(sharedViewModel.getGeneratedTest());
            return;
        }

        // No custom selection is needed and test is not scheduled, so go to test generation.
        generateTest();
    }

}