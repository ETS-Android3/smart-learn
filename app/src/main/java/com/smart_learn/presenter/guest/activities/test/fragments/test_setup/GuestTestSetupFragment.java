package com.smart_learn.presenter.guest.activities.test.fragments.test_setup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.presenter.guest.activities.test.GuestTestActivity;
import com.smart_learn.presenter.guest.activities.test.GuestTestSharedViewModel;
import com.smart_learn.presenter.common.activities.test.fragments.local_test_setup.LocalTestSetupFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class GuestTestSetupFragment extends LocalTestSetupFragment<GuestTestSetupViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestTestSetupViewModel> getModelClassForViewModel() {
        return GuestTestSetupViewModel.class;
    }

    @Override
    protected void navigateToSelectWordsFragment() {
        super.navigateToSelectWordsFragment();
        if(sharedViewModel.getGeneratedTest() != null){
            int lessonId;
            try{
                lessonId = Integer.parseInt(sharedViewModel.getGeneratedTest().getLessonId());
            } catch (NumberFormatException ex){
                showMessage(R.string.error_can_not_continue);
                Timber.w(ex);
                return;
            }
            ((GuestTestActivity)requireActivity()).goToGuestSelectWordsFragment(lessonId);
        }
    }

    @Override
    protected void navigateToSelectExpressionsFragment() {
        super.navigateToSelectExpressionsFragment();
        if(sharedViewModel.getGeneratedTest() != null){
            int lessonId;
            try{
                lessonId = Integer.parseInt(sharedViewModel.getGeneratedTest().getLessonId());
            } catch (NumberFormatException ex){
                showMessage(R.string.error_can_not_continue);
                Timber.w(ex);
                return;
            }
            ((GuestTestActivity)requireActivity()).goToGuestSelectExpressionsFragment(lessonId);
        }
    }

    @Override
    protected void saveScheduledTest(Test scheduledTest) {
        super.saveScheduledTest(scheduledTest);
        viewModel.saveScheduledTest(GuestTestSetupFragment.this, scheduledTest);
    }

    @Override
    protected void generateTest() {
        super.generateTest();
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }
        viewModel.generateTest(GuestTestSetupFragment.this, sharedViewModel.getGeneratedTest());
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestTestSharedViewModel.class);
    }

    protected void navigateToTestFragment(int type, int testId){
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }

        ((GuestTestActivity)requireActivity()).goToActivateTestFragment(type, testId);
    }

    protected void navigateToGuestScheduledTestsFragment(){
        ((GuestTestActivity)requireActivity()).goToGuestScheduledTestsFragment();
    }
}