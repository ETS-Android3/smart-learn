package com.smart_learn.presenter.guest.activities.notebook.fragments.home_lesson;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.core.guest.services.GuestExpressionService;
import com.smart_learn.core.guest.services.GuestLessonService;
import com.smart_learn.core.guest.services.GuestWordService;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.presenter.guest.activities.notebook.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.common.activities.notebook.fragments.home_lesson.HomeLessonFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class GuestHomeLessonFragment extends HomeLessonFragment<GuestHomeLessonViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestHomeLessonViewModel> getModelClassForViewModel() {
        return GuestHomeLessonViewModel.class;
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

        GuestWordService.getInstance().getLiveNumberOfWordsForSpecificLesson(sharedViewModel.getSelectedLessonId())
                .observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                viewModel.setLiveNumberOfWords(integer);
            }
        });

        GuestExpressionService.getInstance().getLiveNumberOfExpressionsForSpecificLesson(sharedViewModel.getSelectedLessonId())
                .observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                viewModel.setLiveNumberOfExpressions(integer);
            }
        });
    }
}