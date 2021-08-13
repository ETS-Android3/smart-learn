package com.smart_learn.presenter.activities.test.helpers.fragments.select_test_type;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.entities.QuestionQuiz;
import com.smart_learn.data.entities.Test;
import com.smart_learn.databinding.FragmentSelectTestTypeBinding;
import com.smart_learn.presenter.activities.test.TestSharedViewModel;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;


public abstract class SelectTestTypeFragment <VM extends SelectTestTypeViewModel> extends BasicFragment<VM> {

    protected FragmentSelectTestTypeBinding binding;
    protected TestSharedViewModel sharedViewModel;

    protected abstract void navigateToTestSetupFragment();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentSelectTestTypeBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    protected void setLayoutUtilities(){
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity)requireActivity(), getString(R.string.select_test_type));
        if(sharedViewModel.getGeneratedTest() != null){
            sharedViewModel.getGeneratedTest().setType(Test.Types.NO_TYPE);
        }
    }

    private void setListeners(){
        binding.btnWordFullWriteFragmentSelectTestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedViewModel.getNrOfLessonWords() < 1){
                    showMessage(R.string.error_lesson_has_no_words);
                    return;
                }
                goToTestSetupFragment(Test.Types.WORD_WRITE);
            }
        });

        binding.btnWordQuizFragmentSelectTestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedViewModel.getNrOfLessonWords() < 1){
                    showMessage(R.string.error_lesson_has_no_words);
                    return;
                }
                if(sharedViewModel.getNrOfLessonWords() < QuestionQuiz.MIN_ITEMS_NECESSARY_FOR_GENERATION){
                    String tmp = getString(R.string.error_not_enough_words_1) + " " + QuestionQuiz.MIN_ITEMS_NECESSARY_FOR_GENERATION +
                            " " + getString(R.string.error_not_enough_words_2) + " " + sharedViewModel.getNrOfLessonWords() + " " + getString(R.string.error_not_enough_words_3);
                    GeneralUtilities.showShortToastMessage(SelectTestTypeFragment.this.requireContext(), tmp);
                    return;
                }
                goToTestSetupFragment(Test.Types.WORD_QUIZ);
            }
        });

        binding.btnWordMixedLettersFragmentSelectTestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedViewModel.getNrOfLessonWords() < 1){
                    showMessage(R.string.error_lesson_has_no_words);
                    return;
                }
                goToTestSetupFragment(Test.Types.WORD_MIXED_LETTERS);
            }
        });

        binding.btnExpressionMixedFragmentSelectTestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedViewModel.getNrOfLessonExpressions() < 1){
                    showMessage(R.string.error_lesson_has_no_expressions);
                    return;
                }
                goToTestSetupFragment(Test.Types.EXPRESSION_MIXED_WORDS);
            }
        });

        binding.btnExpressionTrueOrFalseFragmentSelectTestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedViewModel.getNrOfLessonExpressions() < 1){
                    showMessage(R.string.error_lesson_has_no_expressions);
                    return;
                }
                goToTestSetupFragment(Test.Types.EXPRESSION_TRUE_OR_FALSE);
            }
        });
    }

    private void goToTestSetupFragment(int type){
        if(sharedViewModel.getGeneratedTest() == null){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_page_can_not_be_opened));
            return;
        }

        // First update type and then go to next fragment.
        sharedViewModel.getGeneratedTest().setType(type);
        navigateToTestSetupFragment();
    }

}