package com.smart_learn.presenter.common.fragments.test.test_types.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart_learn.data.common.entities.question.QuestionQuiz;
import com.smart_learn.databinding.FragmentQuizTestBinding;
import com.smart_learn.presenter.common.fragments.test.test_types.BasicTestTypeFragment;

import org.jetbrains.annotations.NotNull;


public abstract class QuizTestFragment <VM extends QuizTestViewModel> extends BasicTestTypeFragment<VM> {

    protected FragmentQuizTestBinding binding;

    @Override
    protected boolean useReverseSwitch() {
        return true;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentQuizTestBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        binding.btnOptionAFragmentQuizTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setUserAnswer(QuestionQuiz.INDEX_OPTION_A);
                viewModel.showNextQuestion(QuizTestFragment.this);
            }
        });

        binding.btnOptionBFragmentQuizTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setUserAnswer(QuestionQuiz.INDEX_OPTION_B);
                viewModel.showNextQuestion(QuizTestFragment.this);
            }
        });

        binding.btnOptionCFragmentQuizTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setUserAnswer(QuestionQuiz.INDEX_OPTION_C);
                viewModel.showNextQuestion(QuizTestFragment.this);
            }
        });

        binding.btnOptionDFragmentQuizTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setUserAnswer(QuestionQuiz.INDEX_OPTION_D);
                viewModel.showNextQuestion(QuizTestFragment.this);
            }
        });
    }
}