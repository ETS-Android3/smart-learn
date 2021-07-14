package com.smart_learn.presenter.activities.notebook.guest.fragments.home_lesson;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_lesson.HomeLessonFragment;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;


public class GuestHomeLessonFragment extends HomeLessonFragment<GuestHomeLessonViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestHomeLessonViewModel> getModelClassForViewModel() {
        return GuestHomeLessonViewModel.class;
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
        ((GuestNotebookActivity)requireActivity()).showBottomNavigationMenu();
    }


    private void setLayoutUtilities(){
        Utilities.Activities.setCustomEditableLayout(binding.toolbarFragmentHomeLesson, binding.layoutLessonNameFragmentHomeLesson,
                binding.tvLessonNameFragmentHomeLesson, new Callbacks.CustomEditableLayoutCallback() {
                    @Override
                    public void savePreviousValue() {
                        viewModel.savePreviousLessonName();
                    }

                    @Override
                    public void revertToPreviousValue() {
                        viewModel.revertToPreviousLessonName();
                    }

                    @Override
                    public boolean isCurrentValueOk() {
                        return viewModel.updateLessonName(binding.layoutLessonNameFragmentHomeLesson);
                    }

                    @Override
                    public void saveCurrentValue() {

                    }
                });
    }


    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);

        GuestLessonService.getInstance().getSampleLiveLesson(sharedViewModel.getSelectedLessonId()).observe(this, new Observer<Lesson>() {
            @Override
            public void onChanged(Lesson lesson) {
                viewModel.setLiveLesson(lesson);
            }
        });
    }
}