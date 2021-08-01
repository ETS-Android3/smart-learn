package com.smart_learn.presenter.activities.test.user.fragments.test_types.test_full_write;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.helpers.fragments.test_types.full_write.FullWriteTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserFullWriteTestFragment extends FullWriteTestFragment<UserFullWriteTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserFullWriteTestViewModel> getModelClassForViewModel() {
        return UserFullWriteTestViewModel.class;
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
