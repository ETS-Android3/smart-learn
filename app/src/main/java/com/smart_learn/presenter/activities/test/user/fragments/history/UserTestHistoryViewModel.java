package com.smart_learn.presenter.activities.test.user.fragments.history;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.helpers.fragments.tests.history.user.standard.UserStandardTestHistoryViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class UserTestHistoryViewModel extends UserStandardTestHistoryViewModel {

    public UserTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void onContinueWithOnlineTest(@NonNull @NotNull UserTestHistoryFragment fragment, DocumentSnapshot testContainerDocument){
        if(testContainerDocument == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_page_can_not_be_opened));
            return;
        }

        // extract specific test and check if is finished
        final String testContainerDocumentId = testContainerDocument.getId();
        if(TextUtils.isEmpty(testContainerDocumentId)){
            Timber.w("test id can not be null or empty");
            return;
        }

        TestService.getInstance()
                .getOnlineTestParticipantsCollectionReference(testContainerDocumentId)
                .document(UserService.getInstance().getUserUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            liveToastMessage.postValue(fragment.getString(R.string.error_page_can_not_be_opened));
                            Timber.w("result is not valid");
                            return;
                        }

                        TestDocument test = task.getResult().toObject(TestDocument.class);
                        if(test == null){
                            liveToastMessage.postValue(fragment.getString(R.string.error_page_can_not_be_opened));
                            Timber.w("test is null");
                            return;
                        }

                        fragment.requireActivity().runOnUiThread(() -> fragment.goToUserOnlineTestContainerFragment(test.getType(),
                                testContainerDocumentId, test.isFinished()));
                    }
                });
    }

}

