package com.smart_learn.presenter.activities.test.user.fragments.select_words;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.words.user.select.UserBasicSelectWordsFragment;

import org.jetbrains.annotations.NotNull;


public class UserSelectWordsFragment extends UserBasicSelectWordsFragment<UserSelectWordsViewModel> {

    private UserTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserSelectWordsViewModel> getModelClassForViewModel() {
        return UserSelectWordsViewModel.class;
    }

    @Override
    protected int getFloatingActionButtonIconResourceId() {
        return R.drawable.ic_baseline_navigate_next_24;
    }

    @Override
    protected void onFloatingActionButtonPress() {
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }
        viewModel.generateTest(UserSelectWordsFragment.this, sharedViewModel.getGeneratedTest());
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) this.requireActivity(), getString(R.string.selected_point) + " " + value);
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