package com.smart_learn.presenter.activities.test.user.fragments.test_setup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.entities.Test;
import com.smart_learn.presenter.activities.test.helpers.fragments.local_test_setup.LocalTestSetupFragment;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;

import org.jetbrains.annotations.NotNull;


public class UserTestSetupFragment extends LocalTestSetupFragment<UserTestSetupViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserTestSetupViewModel> getModelClassForViewModel() {
        return UserTestSetupViewModel.class;
    }

    @Override
    protected void navigateToSelectWordsFragment() {
        super.navigateToSelectWordsFragment();
        if(sharedViewModel.getGeneratedTest() == null) {
            showMessage(R.string.error_can_not_continue);
            return;
        }
        ((UserTestActivity)requireActivity()).goToUserSelectWordsFragment(sharedViewModel.getGeneratedTest().getLessonId());
    }

    @Override
    protected void navigateToSelectExpressionsFragment() {
        super.navigateToSelectExpressionsFragment();
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }
        ((UserTestActivity)requireActivity()).goToUserSelectExpressionsFragment(sharedViewModel.getGeneratedTest().getLessonId());
    }

    @Override
    protected void saveScheduledTest(Test scheduledTest) {
        super.saveScheduledTest(scheduledTest);
        viewModel.saveScheduledTest(UserTestSetupFragment.this, scheduledTest);
    }

    @Override
    protected void generateTest() {
        super.generateTest();
        if(sharedViewModel.getGeneratedTest() == null) {
            showMessage(R.string.error_can_not_continue);
            return;
        }
        viewModel.generateTest(UserTestSetupFragment.this, sharedViewModel.getGeneratedTest());
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    protected void navigateToTestFragment(int type, String testId){
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }

        ((UserTestActivity)requireActivity()).goToActivateTestFragment(type, testId);
    }

    protected void navigateToUserScheduledTestsFragment(){
        ((UserTestActivity)requireActivity()).goToUserScheduledTestsFragment();
    }
}