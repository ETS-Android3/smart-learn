package com.smart_learn.presenter.activities.test.user.fragments.test_setup;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.presenter.activities.test.helpers.fragments.local_test_setup.LocalTestSetupViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public class UserTestSetupViewModel extends LocalTestSetupViewModel {

    public UserTestSetupViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void generateTest(@NonNull @NotNull UserTestSetupFragment fragment, Test test){
        if(test == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("test is null");
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.generating_test));

        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                continueWithGeneratingTest(fragment, test);
            }
            @Override
            public void networkDisabled() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_network));
            }
            @Override
            public void internetNotAvailable() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_internet_connection));
            }
            @Override
            public void notConnected() {
                fragment.requireActivity().runOnUiThread(fragment::closeProgressDialog);
            }
        }).check();

    }

    private void continueWithGeneratingTest(UserTestSetupFragment fragment, Test test){
        TestService.getInstance().generateUserTest(test, test.getNrOfValuesForGenerating(), new TestService.TestGenerationCallback() {
            @Override
            public void onComplete(@NotNull @NonNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();

                    if(testId.equals(TestService.NO_TEST_ID)){
                        liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
                        return;
                    }

                    fragment.navigateToTestFragment(test.getType(), testId);

                });
            }
        });
    }

    protected void saveScheduledTest(@NonNull @NotNull UserTestSetupFragment fragment, Test test){
        if(test == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("test is null");
            return;
        }

        TestService.getInstance().saveSimpleUserScheduledTest(test, new TestService.TestGenerationCallback() {
            @Override
            public void onComplete(@NonNull @NotNull String testId) {
                if(testId.equals(TestService.NO_TEST_ID)){
                    liveToastMessage.postValue(fragment.getString(R.string.error_can_not_save_test));
                    return;
                }

                fragment.requireActivity().runOnUiThread(() -> {
                    GeneralUtilities.showShortToastMessage(fragment.requireContext(), fragment.getString(R.string.test_saved));
                    fragment.navigateToUserScheduledTestsFragment();
                });
            }
        });

    }

}
