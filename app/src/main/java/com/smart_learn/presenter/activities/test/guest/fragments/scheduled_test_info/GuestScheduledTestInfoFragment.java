package com.smart_learn.presenter.activities.test.guest.fragments.scheduled_test_info;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.data.entities.Test;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestSharedViewModel;
import com.smart_learn.presenter.activities.test.helpers.fragments.scheduled_test_info.ScheduledTestInfoFragment;

import org.jetbrains.annotations.NotNull;


public class GuestScheduledTestInfoFragment extends ScheduledTestInfoFragment<GuestScheduledTestInfoViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestScheduledTestInfoViewModel> getModelClassForViewModel() {
        return GuestScheduledTestInfoViewModel.class;
    }

    @Override
    protected void navigateToSelectLessonFragment() {
       ((GuestTestActivity)requireActivity()).goToGuestSelectLessonFragment();
    }

    @Override
    protected void updateTest(@NonNull @NotNull Test newTest) {
        viewModel.updateTest(GuestScheduledTestInfoFragment.this, newTest);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GuestTestActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestTestSharedViewModel.class);

        if(!viewModel.isForUpdate()){
            return;
        }

        // is is for update extract test in order to update if necessary
        int testIdInteger = viewModel.getTestIdInteger();
        if(testIdInteger == GuestScheduledTestInfoViewModel.NO_TEST_ID){
            goBack();
            return;
        }

        viewModel.setUpdatedTest(GuestScheduledTestInfoFragment.this, testIdInteger);
    }

}