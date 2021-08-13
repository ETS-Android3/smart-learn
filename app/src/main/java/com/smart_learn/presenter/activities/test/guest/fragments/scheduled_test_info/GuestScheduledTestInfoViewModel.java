package com.smart_learn.presenter.activities.test.guest.fragments.scheduled_test_info;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.helpers.ThreadExecutorService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.activities.test.helpers.fragments.scheduled_test_info.ScheduledTestInfoViewModel;
import com.smart_learn.core.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class GuestScheduledTestInfoViewModel extends ScheduledTestInfoViewModel {

    protected static final int NO_TEST_ID = -1;

    public GuestScheduledTestInfoViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected int getTestIdInteger(){
        int testIdInteger;
        try{
            testIdInteger = Integer.parseInt(super.getTestId());
        } catch (NumberFormatException ex){
            Timber.w(ex);
            return NO_TEST_ID;
        }
        return testIdInteger;
    }

    protected void setUpdatedTest(@NonNull @NotNull GuestScheduledTestInfoFragment fragment, int testIdInteger){
        ThreadExecutorService.getInstance().execute(() -> {
            RoomTest test = TestService.getInstance().getTest(testIdInteger);
            if(test == null){
                Timber.w("test is null");
                return;
            }
            fragment.requireActivity().runOnUiThread(() -> setTestValues(test));
        });
    }

    private void setTestValues(RoomTest roomTest){
        super.setUpdatedTest(roomTest);
    }

    protected void updateTest(@NonNull @NotNull GuestScheduledTestInfoFragment fragment, Test newTest){
        if(newTest == null){
            Timber.w("newTest is null");
            return;
        }

        if(!(newTest instanceof RoomTest)){
            liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_test_updated));
            return;
        }

        TestService.getInstance().update((RoomTest) newTest, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_test_updated));
                fragment.requireActivity().runOnUiThread(() -> fragment.requireActivity().onBackPressed());
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_test_updated));
            }
        });
    }

}
