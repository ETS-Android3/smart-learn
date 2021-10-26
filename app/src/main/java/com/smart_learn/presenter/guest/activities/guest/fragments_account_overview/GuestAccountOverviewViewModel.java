package com.smart_learn.presenter.guest.activities.guest.fragments_account_overview;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.guest.services.GuestExpressionService;
import com.smart_learn.core.guest.services.GuestLessonService;
import com.smart_learn.core.guest.services.GuestWordService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.presenter.common.fragments.account_overview.AccountOverviewViewModel;

import org.jetbrains.annotations.NotNull;

public class GuestAccountOverviewViewModel extends AccountOverviewViewModel {

    public GuestAccountOverviewViewModel(@NonNull @NotNull Application application) {
        super(application);

        // Is enough to set this values once because will be linked with db using live data, so any
        // change in db will be propagated to the layout.
        liveNumberOfLessons = GuestLessonService.getInstance().getLiveNumberOfLessons();
        liveNumberOfWords = GuestWordService.getInstance().getLiveNumberOfWords();
        liveNumberOfExpressions = GuestExpressionService.getInstance().getLiveNumberOfExpressions();
        liveNumberOfLocalActiveTests = TestService.getInstance().getLiveNumberOfInProgressTests();
        liveNumberOfLocalFinishedTests = TestService.getInstance().getLiveNumberOfFinishedTests();
        liveSuccessRate = TestService.getInstance().getLiveSuccessRate();

        userHelloMessage = application.getResources().getString(R.string.hi) + "!";
    }
}
