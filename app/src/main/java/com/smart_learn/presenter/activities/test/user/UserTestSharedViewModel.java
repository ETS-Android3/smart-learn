package com.smart_learn.presenter.activities.test.user;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.presenter.activities.test.TestSharedViewModel;
import com.smart_learn.presenter.helpers.dialogs.SingleLineEditableLayoutDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

@Getter
@Setter
public class UserTestSharedViewModel extends TestSharedViewModel {

    private String selectedTestHistoryId;
    private String selectedTestScheduledId;

    private String selectedOnlineContainerTestId;
    // for online test container fragment
    private boolean isOnlineTestContainerFragmentActive;
    private int selectedOnlineTestType;
    private boolean isSelectedOnlineTestFinished;

    public UserTestSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void addNewOnlineTest(UserTestActivity activity, String customName, TextInputLayout textInputLayout,
                                    SingleLineEditableLayoutDialog.Listener listener){
        if(customName == null || customName.isEmpty()){
            textInputLayout.setError(activity.getString(R.string.error_required));
            return;
        }

        if(customName.length() > DataUtilities.Limits.MAX_TEST_CUSTOM_NAME){
            textInputLayout.setError(activity.getString(R.string.error_test_custom_name_too_long));
            return;
        }

        textInputLayout.setError(null);
        listener.onSuccessCheck();

        // also for an online test connexion is needed
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                // prepare a new empty test and set test as online
                TestDocument newTest = new TestDocument(new DocumentMetadata(UserService.getInstance().getUserUid(),
                        System.currentTimeMillis(), new ArrayList<>()));
                // for online test name is same as custom name
                newTest.setTestName(customName);
                newTest.setCustomTestName(customName);
                newTest.setOnline(true);
                setGeneratedTest(newTest);

                activity.runOnUiThread(activity::goToUserSelectFriendsFragment);
            }
            @Override
            public void networkDisabled() {
                liveToastMessage.postValue(activity.getString(R.string.error_no_network));
            }
            @Override
            public void internetNotAvailable() {
                liveToastMessage.postValue(activity.getString(R.string.error_no_internet_connection));
            }
            @Override
            public void notConnected() {
                // no action needed here
            }
        }).check();
    }

    protected void processScheduledTestNotification(UserTestActivity activity, String scheduledTestId){
        activity.showProgressDialog("", activity.getString(R.string.preparing_test));
        ThreadExecutorService.getInstance().execute(() -> continueWithProcessingRequest(activity, scheduledTestId));
    }

    private void continueWithProcessingRequest(UserTestActivity activity, String scheduledTestId){
        // extract test
        TestService.getInstance().getLocalTestsCollection()
                .document(scheduledTestId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w("result is not valid");
                            Timber.w(task.getException());
                            liveToastMessage.postValue(activity.getString(R.string.error_can_not_continue));
                            activity.runOnUiThread(activity::closeProgressDialog);
                            return;
                        }

                        TestDocument scheduledTest = task.getResult().toObject(TestDocument.class);
                        if(scheduledTest == null){
                            Timber.w("scheduledTest is null");
                            liveToastMessage.postValue(activity.getString(R.string.error_can_not_continue));
                            activity.runOnUiThread(activity::closeProgressDialog);
                            return;
                        }

                        if(scheduledTest.isGenerated()) {
                            createTestFromScheduledTest(activity, scheduledTest);
                            return;
                        }

                        // here test is not generated so internet must be available in order to generate it
                        new ConnexionChecker(new ConnexionChecker.Callback() {
                            @Override
                            public void isConnected() {
                                createTestFromScheduledTest(activity, scheduledTest);
                            }

                            @Override
                            public void networkDisabled() {
                                liveToastMessage.postValue(activity.getString(R.string.error_no_network));
                            }

                            @Override
                            public void internetNotAvailable() {
                                liveToastMessage.postValue(activity.getString(R.string.error_no_internet_connection));
                            }
                            @Override
                            public void notConnected() {
                                activity.runOnUiThread(activity::closeProgressDialog);
                            }
                        }).check();
                    }
                });

    }

    private void createTestFromScheduledTest(UserTestActivity activity, TestDocument scheduledTest){
        TestService.getInstance().createTestFromScheduledTest(scheduledTest, true, new TestService.TestGenerationCallback() {
            @Override
            public void onComplete(@NonNull @NotNull String testId) {
                activity.runOnUiThread(() -> {
                    activity.closeProgressDialog();

                    if(testId.equals(TestService.NO_TEST_ID)){
                        liveToastMessage.setValue(activity.getString(R.string.error_can_not_continue));
                        return;
                    }
                    activity.goToActivateTestFragment(scheduledTest.getType(), testId, false);
                });
            }
        });
    }


}