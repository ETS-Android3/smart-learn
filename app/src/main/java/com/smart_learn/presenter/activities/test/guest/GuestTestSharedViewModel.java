package com.smart_learn.presenter.activities.test.guest;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.activities.test.TestSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

@Getter
@Setter
public class GuestTestSharedViewModel extends TestSharedViewModel {

    public static int NO_ITEM_SELECTED = -1;

    private int selectedTestHistoryId;
    private int selectedTestScheduledId;

    public GuestTestSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void processScheduledTestNotification(GuestTestActivity activity, String scheduledTestId){
        int idInteger;
        try {
          idInteger = Integer.parseInt(scheduledTestId);
        }
        catch (NumberFormatException ex){
            Timber.w(ex);
            liveToastMessage.setValue(activity.getString(R.string.error_can_not_continue));
            return;
        }

        activity.showProgressDialog("", activity.getString(R.string.preparing_test));
        ThreadExecutorService.getInstance().execute(() -> continueWithProcessingRequest(activity, idInteger));
    }

    private void continueWithProcessingRequest(GuestTestActivity activity, int scheduledTestId){

        // extract scheduled test
        RoomTest scheduledTest = TestService.getInstance().getTest(scheduledTestId);
        if(scheduledTest == null){
            Timber.w("scheduledTest is null");
            liveToastMessage.postValue(activity.getString(R.string.error_can_not_continue));
            activity.runOnUiThread(activity::closeProgressDialog);
            return;
        }

        // and create new local test
        TestService.getInstance().createTestFromScheduledTest(scheduledTest, false, new TestService.TestGenerationCallback() {
            @Override
            public void onSuccess(@NonNull @NotNull String testId) {
                activity.runOnUiThread(() -> {
                    activity.closeProgressDialog();

                    int testIdInteger;
                    try {
                        testIdInteger = Integer.parseInt(testId);
                    }
                    catch (NumberFormatException ex){
                        Timber.w(ex);
                        liveToastMessage.setValue(activity.getString(R.string.error_can_not_continue));
                        return;
                    }
                    activity.goToActivateTestFragment(scheduledTest.getType(), testIdInteger);
                });
            }

            @Override
            public void onFailure(@NonNull @NotNull String error) {
                activity.runOnUiThread(() -> {
                    activity.closeProgressDialog();
                    liveToastMessage.setValue(error);
                });
            }
        });
    }
}
