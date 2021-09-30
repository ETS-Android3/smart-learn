package com.smart_learn.presenter.activities.test.user.fragments.test_types.mixed;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.helpers.fragments.test_types.mixed.MixedTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserMixedTestFragment extends MixedTestFragment<UserMixedTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserMixedTestViewModel> getModelClassForViewModel() {
        return UserMixedTestViewModel.class;
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