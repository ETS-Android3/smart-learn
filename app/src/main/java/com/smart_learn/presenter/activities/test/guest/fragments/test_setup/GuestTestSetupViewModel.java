package com.smart_learn.presenter.activities.test.guest.fragments.test_setup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.activities.test.helpers.fragments.local_test_setup.LocalTestSetupViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class GuestTestSetupViewModel extends LocalTestSetupViewModel {

    public GuestTestSetupViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void generateTest(@NonNull @NotNull GuestTestSetupFragment fragment, Test test){
        if(test == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("test is null");
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.generating_test));

        TestService.getInstance().generateGuestTest(test, test.getNrOfValuesForGenerating(), new TestService.TestGenerationCallback() {
            @Override
            public void onComplete(@NotNull @NonNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();

                    if(testId.equals(TestService.NO_TEST_ID)){
                        liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
                        return;
                    }

                    int testIdInteger;
                    try {
                        testIdInteger = Integer.parseInt(testId);
                    }
                    catch (NumberFormatException ex){
                        Timber.w(ex);
                        liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
                        return;
                    }
                    fragment.navigateToTestFragment(test.getType(), testIdInteger);

                });
            }
        });
    }

    protected void saveScheduledTest(@NonNull @NotNull GuestTestSetupFragment fragment, Test test){
        if(test == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("test is null");
            return;
        }

        TestService.getInstance().saveSimpleGuestScheduledTest(test, new TestService.TestGenerationCallback() {
            @Override
            public void onComplete(@NonNull @NotNull String testId) {
                if(testId.equals(TestService.NO_TEST_ID)){
                    liveToastMessage.postValue(fragment.getString(R.string.error_can_not_save_test));
                    return;
                }

                fragment.requireActivity().runOnUiThread(() -> {
                    GeneralUtilities.showShortToastMessage(fragment.requireContext(), fragment.getString(R.string.test_saved));
                    fragment.navigateToGuestScheduledTestsFragment();
                });
            }
        });
    }
}
