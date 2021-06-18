package com.smart_learn.presenter.activities.notebook.fragments.home_lesson;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.databinding.FragmentHomeLessonBinding;
import com.smart_learn.presenter.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.NotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class HomeLessonFragment extends Fragment {

    @Getter
    private FragmentHomeLessonBinding binding;
    @Getter
    private HomeLessonViewModel homeLessonViewModel;
    @Getter
    private NotebookSharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeLessonBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        setViewModel();
        binding.setViewModel(homeLessonViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.home));
        ((NotebookActivity)requireActivity()).showBottomNavigationMenu();
    }


    private void setLayoutUtilities(){
        Utilities.Activities.setCustomEditableLayout(binding.toolbarFragmentHomeLesson, binding.layoutLessonNameFragmentHomeLesson,
                binding.tvLessonNameFragmentHomeLesson, new Callbacks.CustomEditableLayoutCallback() {
            @Override
            public void savePreviousValue() {
                homeLessonViewModel.savePreviousLessonName();
            }

            @Override
            public void revertToPreviousValue() {
                homeLessonViewModel.revertToPreviousLessonName();
            }

            @Override
            public boolean isCurrentValueOk() {
                return homeLessonViewModel.updateLessonName(binding.layoutLessonNameFragmentHomeLesson);
            }

            @Override
            public void saveCurrentValue() {

            }
        });
    }

    private void setViewModel(){
        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(NotebookSharedViewModel.class);

        // set fragment view model
        homeLessonViewModel = new ViewModelProvider(this).get(HomeLessonViewModel.class);

        // set observers
        homeLessonViewModel.getLiveToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });

        homeLessonViewModel.getLessonService().getSampleLiveLesson(sharedViewModel.getSelectedLessonId()).observe(getViewLifecycleOwner(), new Observer<Lesson>() {
            @Override
            public void onChanged(Lesson lesson) {
                homeLessonViewModel.setLiveLesson(lesson);
            }
        });
    }
}

