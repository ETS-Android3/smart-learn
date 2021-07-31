package com.smart_learn.presenter.activities.test.guest.fragments.select_expressions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestSharedViewModel;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.expressions.guest.select.GuestBasicSelectExpressionsFragment;

import org.jetbrains.annotations.NotNull;


public class GuestSelectExpressionsFragment extends GuestBasicSelectExpressionsFragment<GuestSelectExpressionsViewModel> {

    private GuestTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestSelectExpressionsViewModel> getModelClassForViewModel() {
        return GuestSelectExpressionsViewModel.class;
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
        viewModel.generateTest(GuestSelectExpressionsFragment.this, sharedViewModel.getGeneratedTest());
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) this.requireActivity(), getString(R.string.selected_point) + " " + value);
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