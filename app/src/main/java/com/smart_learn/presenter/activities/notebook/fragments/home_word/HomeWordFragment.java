package com.smart_learn.presenter.activities.notebook.fragments.home_word;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart_learn.databinding.FragmentHomeWordBinding;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public abstract class HomeWordFragment <VM extends HomeWordViewModel> extends BasicFragment<VM> {

    @Getter
    protected FragmentHomeWordBinding binding;
    @Getter
    protected GuestNotebookSharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeWordBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }
}
