package com.smart_learn.presenter.guest.activities.test.fragments.finalize_test;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.presenter.guest.activities.test.GuestTestActivity;
import com.smart_learn.presenter.common.fragments.test.test_finalize.FinalizeTestFragment;

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