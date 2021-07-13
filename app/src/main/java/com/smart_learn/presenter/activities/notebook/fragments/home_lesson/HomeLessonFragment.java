package com.smart_learn.presenter.activities.notebook.fragments.home_lesson;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart_learn.databinding.FragmentHomeLessonBinding;
import com.smart_learn.presenter.activities.notebook.NotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public abstract class HomeLessonFragment <VM extends HomeLessonViewModel> extends BasicFragment<VM> {

    @Getter
    protected FragmentHomeLessonBinding binding;
    @Getter
    protected GuestNotebookSharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeLessonBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

}

