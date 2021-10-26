package com.smart_learn.presenter.guest.activities.test.fragments.test_types.true_or_false;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.presenter.guest.activities.test.GuestTestActivity;
import com.smart_learn.presenter.common.fragments.test.test_types.true_or_false.TrueOrFalseTestFragment;

import org.jetbrains.annotations.NotNull;


public class GuestTrueOrFalseTestFragment extends TrueOrFalseTestFragment<GuestTrueOrFalseTestViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestTrueOrFalseTestViewModel> getModelClassForViewModel() {
        return GuestTrueOrFalseTestViewModel.class;
    }

    @Override
    protected void goToTestFinalizeFragment(String testId, int testType, int correctQuestions, int totalQuestions) {
        int testIdInteger = Test.getTestIdInteger(testId);
        if(testIdInteger == Test.NO_INTEGER_TEST_ID){
            showMessage(R.string.error_can_not_open_results);
            customGoBack();
            return;
        }
        ((GuestTestActivity)requireActivity()).goToGuestFinalizeTestFragment(testIdInteger, testType, correctQuestions, totalQuestions);
    }

    @Override
    protected void customGoBack() {
        ((GuestTestActivity)requireActivity()).goToGuestTestHistoryFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GuestTestActivity)requireActivity()).hideBottomNavigationMenu();
    }
}