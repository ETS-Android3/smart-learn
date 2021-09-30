package com.smart_learn.presenter.activities.test.user.fragments.test_types.quiz;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.helpers.fragments.test_types.quiz.QuizTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserQuizTestFragment extends QuizTestFragment<UserQuizTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserQuizTestViewModel> getModelClassForViewModel() {
        return UserQuizTestViewModel.class;
    }

    @Override
    protected void goToTestFinalizeFragment(String testId, int testType, int correctQuestions, int totalQuestions) {
        ((UserTestActivity)requireActivity()).goToUserFinalizeTestFragment(testId, testType, correctQuestions, totalQuestions);
    }

    @Override
    protected void customGoBack() {
        ((UserTestActivity)requireActivity()).goToUserTestHistoryFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserTestActivity)requireActivity()).hideBottomNavigationMenu();
    }
}