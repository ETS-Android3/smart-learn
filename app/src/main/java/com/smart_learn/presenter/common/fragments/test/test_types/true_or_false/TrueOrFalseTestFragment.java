package com.smart_learn.presenter.common.fragments.test.test_types.true_or_false;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart_learn.data.common.entities.question.QuestionTrueOrFalse;
import com.smart_learn.databinding.FragmentTrueOrFalseTestBinding;
import com.smart_learn.presenter.common.fragments.test.test_types.BasicTestTypeFragment;

import org.jetbrains.annotations.NotNull;


public abstract class TrueOrFalseTestFragment <VM extends TrueOrFalseTestViewModel> extends BasicTestTypeFragment<VM> {

    protected FragmentTrueOrFalseTestBinding binding;

    @Override
    protected boolean useReverseSwitch() {
        return true;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentTrueOrFalseTestBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        binding.btnTrueFragmentTrueOrFalseTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setUserAnswer(QuestionTrueOrFalse.RESPONSE_TRUE);
                viewModel.showNextQuestion(TrueOrFalseTestFragment.this);
            }
        });

        binding.btnFalseFragmentTrueOrFalseTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setUserAnswer(QuestionTrueOrFalse.RESPONSE_FALSE);
                viewModel.showNextQuestion(TrueOrFalseTestFragment.this);
            }
        });

    }
}