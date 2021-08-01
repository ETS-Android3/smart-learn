package com.smart_learn.presenter.activities.test.guest.fragments.test_types.test_full_write;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.full_write.FullWriteTestViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class GuestFullWriteTestViewModel extends FullWriteTestViewModel {

    public GuestFullWriteTestViewModel(@NonNull @NotNull Application application) {
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
            fragment.requireActivity().runOnUiThread(() -> super.setExtractedTest(fragment, test));
        });
    }

    @Override
    protected void updateTest(@NonNull @NotNull Test test, @NonNull @NotNull DataCallbacks.General callback) {
        TestService.getInstance().update((RoomTest) test, callback);
    }
}
