package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.test_types.true_or_false;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.true_or_false.TrueOrFalseTestViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public class UserTrueOrFalseOnlineTestViewModel extends TrueOrFalseTestViewModel {

    private DocumentSnapshot extractedTestSnapshot;

    public UserTrueOrFalseOnlineTestViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    @Override
    protected void extractTest(@NonNull @NotNull BasicTestTypeFragment<?> fragment) {
        final String containerTestId = super.getTestId();
        if(TextUtils.isEmpty(containerTestId)){
            Timber.w("containerTestId can not be null or empty");
            return;
        }

        // only current user can make his own test
        String participantTestId = UserService.getInstance().getUserUid();
        if(TextUtils.isEmpty(participantTestId)){
            Timber.w("participantTestId can not be null or empty");
            return;
        }

        // set super.test id as participant test id
        super.setTestId(participantTestId);

        TestService.getInstance()
                .getOnlineTestParticipantsCollectionReference(containerTestId)
                .document(participantTestId)
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
                        fragment.requireActivity().runOnUiThread(() -> UserTrueOrFalseOnlineTestViewModel.super.setExtractedTest(fragment, test, true));
                    }
                });

    }

    @Override
    protected void updateTest(@NonNull @NotNull Test test, @NonNull @NotNull DataCallbacks.General callback) {
        TestService.getInstance().updateTest((TestDocument) test, extractedTestSnapshot, callback);
    }
}
