package com.smart_learn.presenter.activities.test.guest.fragments.select_lesson;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.TestSharedViewModel;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.lessons.guest.standard.GuestStandardLessonsFragment;

import org.jetbrains.annotations.NotNull;

public class GuestSelectLessonFragment extends GuestStandardLessonsFragment<GuestSelectLessonViewModel> {

    private GuestTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestSelectLessonViewModel> getModelClassForViewModel() {
        return GuestSelectLessonViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected boolean showFloatingActionButton() {
        return false;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.select_lesson;
    }

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return false;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull Lesson item) {
        super.onAdapterSimpleClick(item);
        goToGuestTestTypesFragment(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TestActivity<?>)requireActivity()).hideBottomNavigationMenu();
        if(sharedViewModel.getGeneratedTest() != null){
            sharedViewModel.getGeneratedTest().setLessonId(String.valueOf(GuestTestSharedViewModel.NO_ITEM_SELECTED));
            sharedViewModel.getGeneratedTest().setLessonName("");
            sharedViewModel.setNrOfLessonWords(TestSharedViewModel.NO_VALUE);
            sharedViewModel.setNrOfLessonExpressions(TestSharedViewModel.NO_VALUE);
        }
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestTestSharedViewModel.class);
    }

    private void goToGuestTestTypesFragment(Lesson lesson) {
        // when navigation is made a valid lesson id must be set on shared view model
        if (lesson == null || lesson.getLessonId() == GuestTestSharedViewModel.NO_ITEM_SELECTED || sharedViewModel.getGeneratedTest() == null) {
            showMessage(R.string.error_page_can_not_be_opened);
            return;
        }

        viewModel.extractLessonCounters(lesson, this, new GuestSelectLessonViewModel.Callback() {
            @Override
            public void onComplete(int nrOfWords, int nrOfExpressions) {
                if(nrOfWords < 1 && nrOfExpressions < 1){
                    showMessage(R.string.error_lesson_has_no_entries);
                    return;
                }

                // set values
                sharedViewModel.getGeneratedTest().setLessonId(String.valueOf(lesson.getLessonId()));
                sharedViewModel.getGeneratedTest().setLessonName(lesson.getName());
                sharedViewModel.setNrOfLessonWords(nrOfWords);
                sharedViewModel.setNrOfLessonExpressions(nrOfExpressions);
                // and navigate to next fragment
                ((GuestTestActivity) requireActivity()).goToGuestTestTypesFragment();
            }
        });
    }

}