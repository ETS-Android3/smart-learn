package com.smart_learn.presenter.user.activities.test.fragments.schedule_test_info;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.data.common.entities.Test;
import com.smart_learn.presenter.common.activities.test.fragments.scheduled_test_info.ScheduledTestInfoFragment;
import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.user.activities.test.UserTestSharedViewModel;

import org.jetbrains.annotations.NotNull;


public class UserScheduledTestInfoFragment extends ScheduledTestInfoFragment<UserScheduledTestInfoViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserScheduledTestInfoViewModel> getModelClassForViewModel() {
        return UserScheduledTestInfoViewModel.class;
    }

    @Override
    protected void navigateToSelectLessonFragment() {
        ((UserTestActivity)requireActivity()).goToUserSelectLessonFragment();
    }

    @Override
    protected void updateTest(@NonNull @NotNull Test newTest) {
        viewModel.updateTest(UserScheduledTestInfoFragment.this, newTest);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserTestActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);

        if(!viewModel.isForUpdate()){
            return;
        }

        // is is for update extract test in order to update if necessary
        viewModel.setUpdatedTest(UserScheduledTestInfoFragment.this);
    }
}