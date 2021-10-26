package com.smart_learn.presenter.guest.activities.test.fragments.select_expressions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.presenter.guest.activities.test.GuestTestActivity;
import com.smart_learn.presenter.guest.activities.test.GuestTestSharedViewModel;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.fragments.common.expressions.select.GuestBasicSelectExpressionsFragment;

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
    protected boolean isFragmentWithBottomNav() {
        return false;
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
    protected boolean onAdapterIsSelectedItemValid(@NonNull @NotNull Expression item) {
        if(item.getTranslations() == null || item.getTranslations().isEmpty()){
            showMessage(R.string.error_expression_has_no_translation);
            return false;
        }
        return true;
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) this.requireActivity(), getString(R.string.selected_point) + " " + value);
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