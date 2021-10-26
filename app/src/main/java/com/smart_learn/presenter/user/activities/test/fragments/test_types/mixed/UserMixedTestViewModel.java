package com.smart_learn.presenter.user.activities.test.fragments.test_types.mixed;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.presenter.common.fragments.test.test_types.BasicTestTypeFragment;
import com.smart_learn.presenter.common.fragments.test.test_types.mixed.MixedTestViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class UserMixedTestViewModel extends MixedTestViewModel {

    private DocumentSnapshot extractedTestSnapshot;

    public UserMixedTestViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    @Override
    protected void extractTest(@NonNull @NotNull BasicTestTypeFragment<?> fragment) {
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
                            fragment.requireActivity().runOnUiThread(fragment::goBack);
                            return;
                        }
                        extractedTestSnapshot = task.getResult();
                        TestDocument test = extractedTestSnapshot.toObject(TestDocument.class);
                        if(test == null){
                            fragment.requireActivity().runOnUiThread(fragment::goBack);
                            Timber.w("test is null");
                            return;
                        }
                        fragment.requireActivity().runOnUiThread(() -> UserMixedTestViewModel.super.setExtractedTest(fragment, test, false));
                    }
                });

    }

    @Override
    protected void updateTest(@NonNull @NotNull Test test, @NonNull @NotNull DataCallbacks.General callback) {
        TestService.getInstance().updateTest((TestDocument) test, extractedTestSnapshot, callback);
    }
}
