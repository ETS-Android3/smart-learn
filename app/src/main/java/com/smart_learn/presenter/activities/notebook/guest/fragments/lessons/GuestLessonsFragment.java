package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.fragments.lessons.guest.standard.GuestStandardLessonsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class GuestLessonsFragment extends GuestStandardLessonsFragment<GuestLessonsViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestLessonsViewModel> getModelClassForViewModel() {
        return GuestLessonsViewModel.class;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull Lesson item) {
        super.onAdapterSimpleClick(item);
        goToGuestHomeLessonFragment(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).hideBottomNavigationMenu();
        sharedViewModel.setSelectedLessonId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setSelectedWordId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setSelectedExpressionId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);
    }

    private void goToGuestHomeLessonFragment(Lesson lesson){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lesson == null || lesson.getLessonId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLessonId(lesson.getLessonId());
        ((GuestNotebookActivity)requireActivity()).goToGuestHomeLessonFragment();
    }

}