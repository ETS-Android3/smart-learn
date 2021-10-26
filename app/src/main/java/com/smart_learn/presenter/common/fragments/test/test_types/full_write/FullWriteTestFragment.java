package com.smart_learn.presenter.common.fragments.test.test_types.full_write;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart_learn.databinding.FragmentFullWriteTestBinding;
import com.smart_learn.presenter.common.fragments.test.test_types.BasicTestTypeFragment;

import org.jetbrains.annotations.NotNull;


public abstract class FullWriteTestFragment <VM extends FullWriteTestViewModel> extends BasicTestTypeFragment<VM> {

    protected FragmentFullWriteTestBinding binding;

    @Override
    protected boolean useReverseSwitch() {
        return true;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentFullWriteTestBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        binding.btnSubmitAnswerFragmentFullWriteTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               viewModel.showNextQuestion(FullWriteTestFragment.this);
            }
        });
    }
}