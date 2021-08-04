package com.smart_learn.presenter.activities.test.user;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
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

@Getter
@Setter
public class UserTestSharedViewModel extends TestSharedViewModel {

    private String selectedTestHistoryId;
    private String selectedTestScheduledId;

    private String selectedOnlineContainerTestId;

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

}