package com.smart_learn.presenter.activities.test.user.fragments.schedule;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.presenter.helpers.fragments.tests.schedule.user.standard.UserStandardScheduledTestsViewModel;

import org.jetbrains.annotations.NotNull;


public class UserScheduledTestsViewModel extends UserStandardScheduledTestsViewModel {

    public UserScheduledTestsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void checkConnexion(@NonNull @NotNull UserScheduledTestsFragment fragment, @NonNull @NotNull Callback callback){

        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> callback.onFinish(true));
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
                fragment.requireActivity().runOnUiThread(() -> callback.onFinish(false));
            }
        }).check();
    }

    protected interface Callback {
        void onFinish(boolean isConnected);
    }

}
