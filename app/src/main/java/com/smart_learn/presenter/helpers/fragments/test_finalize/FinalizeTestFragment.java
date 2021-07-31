package com.smart_learn.presenter.helpers.fragments.test_finalize;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.R;
import com.smart_learn.data.entities.Test;
import com.smart_learn.databinding.FragmentFinalizeTestBinding;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public abstract class FinalizeTestFragment <VM extends FinalizeTestViewModel> extends BasicFragment<VM> {

    public static String TEST_ID_KEY = "TEST_ID_KEY";
    public static String TEST_TYPE_KEY = "TEST_TYPE_KEY";
    public static String TEST_TOTAL_QUESTIONS_KEY = "TEST_TOTAL_QUESTIONS_KEY";
    public static String TEST_CORRECT_ANSWERS_KEY = "TEST_CORRECT_ANSWERS_KEY";

    protected FragmentFinalizeTestBinding binding;

    protected abstract void goBackOnLoadingError();
    protected abstract void goBackOnHomeButtonPressed();
    protected abstract void onSeeResultPress(@NonNull @NotNull String testId, int testType);

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentFinalizeTestBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        // use this to set toolbar menu inside fragment
        // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    protected void setLayoutUtilities() {
        binding.btnSeeResultFragmentFinalizeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSeeResultPress(viewModel.getTestId(), viewModel.getTestType());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.test_result));
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        if (getArguments() == null){
            Timber.w("no arguments");
            showMessage(R.string.error_can_not_continue);
            goBackOnLoadingError();
            return;
        }

        // extract arguments
        String testId = getArguments().getString(TEST_ID_KEY);
        if (testId == null || testId.isEmpty()) {
            Timber.w("testId is not selected");
            showMessage(R.string.error_can_not_continue);
            goBackOnLoadingError();
            return;
        }

        int totalQuestions = getArguments().getInt(TEST_TOTAL_QUESTIONS_KEY);
        if (totalQuestions <= 0) {
            Timber.w("totalQuestions [" + totalQuestions + "] is not valid");
            showMessage(R.string.error_can_not_continue);
            goBackOnLoadingError();
            return;
        }

        int correctAnsweredQuestions = getArguments().getInt(TEST_CORRECT_ANSWERS_KEY);
        if (correctAnsweredQuestions < 0 || correctAnsweredQuestions > totalQuestions) {
            Timber.w("correctAnsweredQuestions [" + correctAnsweredQuestions + "] is not valid");
            showMessage(R.string.error_can_not_continue);
            goBackOnLoadingError();
            return;
        }

        int testType = getArguments().getInt(TEST_TYPE_KEY);
        switch (testType){
            case Test.Types.WORD_WRITE:
            case Test.Types.WORD_QUIZ:
            case Test.Types.WORD_MIXED_LETTERS:
            case Test.Types.EXPRESSION_MIXED_WORDS:
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                break;
            default:
                Timber.w("testType [" + testType + "] is not valid");
                showMessage(R.string.error_can_not_continue);
                goBackOnLoadingError();
                return;
        }

        viewModel.setIdAndType(testId, testType);
        viewModel.setDescriptions(correctAnsweredQuestions, totalQuestions);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == android.R.id.home){
            goBackOnHomeButtonPressed();
            return true;
        }
        return true;
    }
}