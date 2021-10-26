package com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.finalize_test;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart_learn.R;
import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.user.activities.test.fragments.online_test_container.OnlineTestContainerFragment;
import com.smart_learn.presenter.common.fragments.test.test_finalize.FinalizeTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserFinalizeOnlineTestFragment extends FinalizeTestFragment<UserFinalizeOnlineTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserFinalizeOnlineTestViewModel> getModelClassForViewModel() {
        return UserFinalizeOnlineTestViewModel.class;
    }

    @Override
    protected void goBackOnLoadingError() {
        ((UserTestActivity)requireActivity()).goToUserTestHistoryFragment();
    }

    @Override
    protected void goBackOnHomeButtonPressed() {
        // no action needed here
    }

    @Override
    protected void onSeeResultPress(@NonNull @NotNull String participantTestId, int testType) {
        // first is NavHostFragment, and then fragment OnlineTestContainerFragment where NavHost is attached
        Fragment parentFragment = requireParentFragment().requireParentFragment();
        if(parentFragment instanceof OnlineTestContainerFragment){
            ((OnlineTestContainerFragment)parentFragment).goToUserTestResultsFragment(participantTestId, testType, false);
            return;
        }
        showMessage(R.string.error_can_not_continue);
    }
}