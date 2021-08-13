package com.smart_learn.presenter.activities.test.guest.fragments.test_setup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.entities.Test;
import com.smart_learn.presenter.activities.test.helpers.fragments.local_test_setup.LocalTestSetupViewModel;
import com.smart_learn.presenter.helpers.PresenterUtilities;

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
            public void onSuccess(@NonNull @NotNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();

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

            @Override
            public void onFailure(@NonNull @NotNull String error) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(error);
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
            public void onSuccess(@NonNull @NotNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    PresenterUtilities.General.showShortToastMessage(fragment.requireContext(), fragment.getString(R.string.test_saved));
                    fragment.navigateToGuestScheduledTestsFragment();
                });
            }

            @Override
            public void onFailure(@NonNull @NotNull String error) {
                liveToastMessage.postValue(error);
            }
        });
    }
}
