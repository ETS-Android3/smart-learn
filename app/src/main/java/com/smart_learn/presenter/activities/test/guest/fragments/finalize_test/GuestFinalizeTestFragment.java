package com.smart_learn.presenter.activities.test.guest.fragments.finalize_test;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.data.entities.Test;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.helpers.fragments.test_finalize.FinalizeTestFragment;

import org.jetbrains.annotations.NotNull;


public class GuestFinalizeTestFragment extends FinalizeTestFragment<GuestFinalizeTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestFinalizeTestViewModel> getModelClassForViewModel() {
        return GuestFinalizeTestViewModel.class;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GuestTestActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void goBackOnLoadingError() {
        ((GuestTestActivity)requireActivity()).goToGuestTestHistoryFragment();
    }

    @Override
    protected void goBackOnHomeButtonPressed() {
        ((GuestTestActivity)requireActivity()).goToGuestTestHistoryFragment();
    }

    @Override
    protected void onSeeResultPress(@NonNull @NotNull String testId, int testType) {
        int testIdInteger = Test.getTestIdInteger(testId);
        if(testIdInteger == Test.NO_INTEGER_TEST_ID){
            showMessage(R.string.error_can_not_open_results);
            goBackOnLoadingError();
            return;
        }
        ((GuestTestActivity)requireActivity()).goToGuestTestResultsFragment(testIdInteger, testType);
    }
}