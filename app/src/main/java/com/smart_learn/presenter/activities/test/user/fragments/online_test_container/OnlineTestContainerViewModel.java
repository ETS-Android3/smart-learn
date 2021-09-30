package com.smart_learn.presenter.activities.test.user.fragments.online_test_container;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.helpers.ConnexionChecker;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

public class OnlineTestContainerViewModel extends BasicAndroidViewModel {

    public OnlineTestContainerViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void onSignalReceived(@NonNull @NotNull OnlineTestContainerFragment fragment){
            new ConnexionChecker(new ConnexionChecker.Callback() {
                @Override
                public void isConnected() {
                    fragment.disableSnackBar();
                }
                @Override
                public void networkDisabled() {
                    fragment.showSnackBar(R.string.error_no_internet_connection_snackbar_description);
                }
                @Override
                public void internetNotAvailable() {
                    fragment.showSnackBar(R.string.error_no_network_snackbar_description);

                }
                @Override
                public void notConnected() {
                    // no action needed here
                }
            }).check();
    }
}
