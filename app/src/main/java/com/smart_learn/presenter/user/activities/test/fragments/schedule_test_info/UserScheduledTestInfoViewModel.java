package com.smart_learn.presenter.user.activities.test.fragments.schedule_test_info;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.presenter.common.activities.test.fragments.scheduled_test_info.ScheduledTestInfoViewModel;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.presenter.user.activities.test.fragments.schedule_test_info.UserScheduledTestInfoFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class UserScheduledTestInfoViewModel extends ScheduledTestInfoViewModel {

    private DocumentSnapshot extractedTestSnapshot;

    public UserScheduledTestInfoViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void setUpdatedTest(@NonNull @NotNull UserScheduledTestInfoFragment fragment){
        final String testId = super.getTestId();
        if(TextUtils.isEmpty(testId)){
            Timber.w("test id can not be null or empty");
            return;
        }

        TestService.getInstance()
                .getLocalTestsCollection()
                .document(testId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w("result is not valid");
                            return;
                        }
                        extractedTestSnapshot = task.getResult();
                        TestDocument test = extractedTestSnapshot.toObject(TestDocument.class);
                        if(test == null){
                            Timber.w("test is null");
                            return;
                        }
                        fragment.requireActivity().runOnUiThread(() -> setTestValues(test));
                    }
                });

    }

    private void setTestValues(TestDocument testDocument){
        super.setUpdatedTest(testDocument);
    }

    protected void updateTest(@NonNull @NotNull UserScheduledTestInfoFragment fragment, Test newTest){
        if(newTest == null){
            Timber.w("extractedTestSnapshot is null");
            liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_test_updated));
            return;
        }

        if(!(newTest instanceof TestDocument)){
            liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_test_updated));
            return;
        }

        if(extractedTestSnapshot == null){
            Timber.w("extractedTestSnapshot is null");
            liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_test_updated));
            return;
        }

        TestService.getInstance().updateDocument(TestDocument.convertDocumentToHashMap((TestDocument) newTest), extractedTestSnapshot,
                new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_test_updated));
                        fragment.requireActivity().runOnUiThread(() -> fragment.requireActivity().onBackPressed());
                    }

                    @Override
                    public void onFailure() {
                        liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_test_updated));
                    }
        });
    }

}