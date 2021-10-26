package com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.test_types.quiz;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart_learn.R;
import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.user.activities.test.fragments.online_test_container.OnlineTestContainerFragment;
import com.smart_learn.presenter.common.fragments.test.test_types.quiz.QuizTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserQuizOnlineTestFragment extends QuizTestFragment<UserQuizOnlineTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserQuizOnlineTestViewModel> getModelClassForViewModel() {
        return UserQuizOnlineTestViewModel.class;
    }

    @Override
    protected void goToTestFinalizeFragment(String testId, int testType, int correctQuestions, int totalQuestions) {
        // first is NavHostFragment, and then fragment OnlineTestContainerFragment where NavHost is attached
        Fragment parentFragment = requireParentFragment().requireParentFragment();
        if(parentFragment instanceof OnlineTestContainerFragment){
            ((OnlineTestContainerFragment)parentFragment).goToFinalizeTestFragment(testId, testType, correctQuestions, totalQuestions);
            return;
        }
        showMessage(R.string.error_can_not_continue);
    }

    @Override
    protected void customGoBack() {
        ((UserTestActivity)requireActivity()).goToUserTestHistoryFragment();
    }

}