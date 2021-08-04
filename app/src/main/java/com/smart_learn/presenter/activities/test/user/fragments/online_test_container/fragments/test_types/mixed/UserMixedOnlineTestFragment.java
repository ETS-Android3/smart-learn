package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.test_types.mixed;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.fragments.online_test_container.OnlineTestContainerFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.mixed.MixedTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserMixedOnlineTestFragment extends MixedTestFragment<UserMixedOnlineTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserMixedOnlineTestViewModel> getModelClassForViewModel() {
        return UserMixedOnlineTestViewModel.class;
    }

    @Override
    protected void goToTestFinalizeFragment(String participantTestId, int testType, int correctQuestions, int totalQuestions) {
        // first is NavHostFragment, and then fragment OnlineTestContainerFragment where NavHost is attached
        Fragment parentFragment = requireParentFragment().requireParentFragment();
        if(parentFragment instanceof OnlineTestContainerFragment){
            ((OnlineTestContainerFragment)parentFragment).goToFinalizeTestFragment(participantTestId, testType, correctQuestions, totalQuestions);
            return;
        }
        showMessage(R.string.error_can_not_continue);
    }

    @Override
    protected void customGoBack() {
        ((UserTestActivity)requireActivity()).goToUserTestHistoryFragment();
    }
}