package com.smart_learn.presenter.user.activities.test.fragments.finalize_test;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.common.fragments.test.test_finalize.FinalizeTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserFinalizeTestFragment extends FinalizeTestFragment<UserFinalizeTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserFinalizeTestViewModel> getModelClassForViewModel() {
        return UserFinalizeTestViewModel.class;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserTestActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void goBackOnLoadingError() {
        ((UserTestActivity)requireActivity()).goToUserTestHistoryFragment();
    }

    @Override
    protected void goBackOnHomeButtonPressed() {
        ((UserTestActivity)requireActivity()).goToUserTestHistoryFragment();
    }

    @Override
    protected void onSeeResultPress(@NonNull @NotNull String testId, int testType) {
        ((UserTestActivity)requireActivity()).goToUserTestResultsFragment(testId, testType);
    }
}