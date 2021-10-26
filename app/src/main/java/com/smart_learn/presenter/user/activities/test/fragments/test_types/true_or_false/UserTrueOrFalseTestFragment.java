package com.smart_learn.presenter.user.activities.test.fragments.test_types.true_or_false;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.common.fragments.test.test_types.true_or_false.TrueOrFalseTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserTrueOrFalseTestFragment extends TrueOrFalseTestFragment<UserTrueOrFalseTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserTrueOrFalseTestViewModel> getModelClassForViewModel() {
        return UserTrueOrFalseTestViewModel.class;
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