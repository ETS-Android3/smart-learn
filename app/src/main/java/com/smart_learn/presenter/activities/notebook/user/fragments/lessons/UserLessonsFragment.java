package com.smart_learn.presenter.activities.notebook.user.fragments.lessons;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.lessons.user.standard.UserStandardLessonsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class UserLessonsFragment extends UserStandardLessonsFragment<UserLessonsViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserLessonsViewModel> getModelClassForViewModel() {
        return UserLessonsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
        super.onAdapterSimpleClick(item);
        goToUserHomeLessonFragment(item);
    }

    @Override
    protected void onAdapterShareLessonClick(@NonNull @NotNull DocumentSnapshot lessonSnapshot) {
        super.onAdapterShareLessonClick(lessonSnapshot);
        shareLesson(lessonSnapshot);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).hideBottomNavigationMenu();
        sharedViewModel.setSelectedLesson(null);
        sharedViewModel.setSelectedWord(null);
        sharedViewModel.setSelectedExpression(null);
        sharedViewModel.setAddNewEmptySharedLesson(false);
        sharedViewModel.setNewEmptySharedLessonName(null);
        sharedViewModel.setSharedLessonSelected(false);
        sharedViewModel.setSelectedSharedLessonParticipants(null);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);
    }

    private void goToUserHomeLessonFragment(DocumentSnapshot lessonSnapshot){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lessonSnapshot == null){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        LessonDocument lessonDocument = lessonSnapshot.toObject(LessonDocument.class);
        if(lessonDocument == null){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson document id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLesson(lessonSnapshot);
        boolean isSharedLessonSelected = lessonDocument.getType() == LessonDocument.Types.SHARED;
        sharedViewModel.setSharedLessonSelected(isSharedLessonSelected);
        ((UserNotebookActivity)requireActivity()).goToUserHomeLessonFragment(isSharedLessonSelected);
    }

    private void shareLesson(DocumentSnapshot lessonSnapshot){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lessonSnapshot == null){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson document id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLesson(lessonSnapshot);
        ((UserNotebookActivity)requireActivity()).goToSelectFriendsFragment(false);
    }

    @Override
    protected void addNewEmptySharedLesson(@NonNull @NotNull String lessonName) {
        sharedViewModel.setAddNewEmptySharedLesson(true);
        sharedViewModel.setNewEmptySharedLessonName(lessonName);
        ((UserNotebookActivity)requireActivity()).goToSelectFriendsFragment(true);
    }
}