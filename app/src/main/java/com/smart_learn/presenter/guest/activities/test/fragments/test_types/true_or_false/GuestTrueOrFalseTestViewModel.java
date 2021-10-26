package com.smart_learn.presenter.guest.activities.test.fragments.test_types.true_or_false;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.common.services.ThreadExecutorService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.guest.room.entitites.RoomTest;
import com.smart_learn.presenter.common.fragments.test.test_types.BasicTestTypeFragment;
import com.smart_learn.presenter.common.fragments.test.test_types.true_or_false.TrueOrFalseTestViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class GuestTrueOrFalseTestViewModel extends TrueOrFalseTestViewModel {

    public GuestTrueOrFalseTestViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    @Override
    protected void extractTest(@NonNull @NotNull BasicTestTypeFragment<?> fragment) {
        ThreadExecutorService.getInstance().execute(() -> {
            RoomTest test = TestService.getInstance().getTest(Test.getTestIdInteger(getTestId()));
            if(test == null){
                Timber.w("test is null");
                fragment.requireActivity().runOnUiThread(fragment::goBack);
                return;
            }
            fragment.requireActivity().runOnUiThread(() -> super.setExtractedTest(fragment, test, false));
        });
    }

    @Override
    protected void updateTest(@NonNull @NotNull Test test, @NonNull @NotNull DataCallbacks.General callback) {
        TestService.getInstance().update((RoomTest) test, callback);
    }
}