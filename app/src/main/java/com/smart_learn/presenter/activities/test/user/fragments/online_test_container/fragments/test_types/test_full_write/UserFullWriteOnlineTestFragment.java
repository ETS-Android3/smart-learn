package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.test_types.test_full_write;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.fragments.online_test_container.OnlineTestContainerFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.full_write.FullWriteTestFragment;

import org.jetbrains.annotations.NotNull;


public class UserFullWriteOnlineTestFragment extends FullWriteTestFragment<UserFullWriteOnlineTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserFullWriteOnlineTestViewModel> getModelClassForViewModel() {
        return UserFullWriteOnlineTestViewModel.class;
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
