package com.smart_learn.presenter.guest.activities.notebook.fragments.lessons;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.presenter.guest.activities.notebook.GuestNotebookActivity;
import com.smart_learn.presenter.guest.activities.notebook.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.common.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.fragments.common.lessons.standard.GuestStandardLessonsFragment;

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
    protected boolean isFragmentWithBottomNav() {
        return false;
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
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLessonId(lesson.getLessonId());
        ((GuestNotebookActivity)requireActivity()).goToGuestHomeLessonFragment();
    }

}