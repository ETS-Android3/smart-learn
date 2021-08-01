package com.smart_learn.presenter.activities.test.user.fragments.select_lesson;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.TestSharedViewModel;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.lessons.user.standard.UserStandardLessonsFragment;

import org.jetbrains.annotations.NotNull;


public class UserSelectLessonFragment extends UserStandardLessonsFragment<UserSelectLessonViewModel> {

    private UserTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserSelectLessonViewModel> getModelClassForViewModel() {
        return UserSelectLessonViewModel.class;
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
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
        super.onAdapterSimpleClick(item);
        goToUserTestTypesFragment(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TestActivity<?>)requireActivity()).hideBottomNavigationMenu();
        if(sharedViewModel.getGeneratedTest() != null){
            sharedViewModel.getGeneratedTest().setLessonId("");
            sharedViewModel.getGeneratedTest().setLessonName("");
            sharedViewModel.setNrOfLessonWords(TestSharedViewModel.NO_VALUE);
            sharedViewModel.setNrOfLessonExpressions(TestSharedViewModel.NO_VALUE);
        }
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    private void goToUserTestTypesFragment(DocumentSnapshot lessonSnapshot) {
        // when navigation is made a valid lesson id must be set on shared view model
        if (lessonSnapshot == null || TextUtils.isEmpty(lessonSnapshot.getId()) || sharedViewModel.getGeneratedTest() == null) {
            showMessage(R.string.error_page_can_not_be_opened);
            return;
        }

        LessonDocument lesson = lessonSnapshot.toObject(LessonDocument.class);
        if(lesson == null){
            showMessage(R.string.error_page_can_not_be_opened);
            return;
        }

        int nrOfWords = lesson.getNrOfWords();
        int nrOfExpressions = lesson.getNrOfExpressions();
        if(nrOfWords < 1 && nrOfExpressions < 1){
            showMessage(R.string.error_lesson_has_no_entries);
            return;
        }

        // set values
        sharedViewModel.getGeneratedTest().setLessonId(lessonSnapshot.getId());
        sharedViewModel.getGeneratedTest().setLessonName(lesson.getName());
        sharedViewModel.setNrOfLessonWords(nrOfWords);
        sharedViewModel.setNrOfLessonExpressions(nrOfExpressions);
        // and navigate to next fragment
        ((UserTestActivity) requireActivity()).goToUserTestTypesFragment();

    }

}