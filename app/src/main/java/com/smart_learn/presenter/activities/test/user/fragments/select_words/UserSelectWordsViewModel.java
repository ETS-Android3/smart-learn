package com.smart_learn.presenter.activities.test.user.fragments.select_words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.presenter.activities.test.user.fragments.test_types.mixed.UserMixedTestViewModel;
import com.smart_learn.presenter.helpers.fragments.words.user.select.UserBasicSelectWordsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class UserSelectWordsViewModel extends UserBasicSelectWordsViewModel {

    public UserSelectWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void generateTest(@NonNull @NotNull UserSelectWordsFragment fragment, Test test){
        if(adapter == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("adapter is null");
            return;
        }

        if(test == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("test is null");
            return;
        }

        ArrayList<DocumentSnapshot> selectedWordSnapshots = adapter.getSelectedValues();
        if(selectedWordSnapshots.isEmpty()){
            liveToastMessage.setValue(fragment.getString(R.string.no_selected_word));
            return;
        }

        ArrayList<WordDocument> selectedWords = new ArrayList<>();
        for(DocumentSnapshot snapshot : selectedWordSnapshots){
            if(snapshot == null){
                continue;
            }
            WordDocument word = snapshot.toObject(WordDocument.class);
            if(word != null){
                selectedWords.add(word);
            }
        }

        if(selectedWords.isEmpty()){
            Timber.w("selectedWords is empty");
            liveToastMessage.setValue(fragment.getString(R.string.no_selected_word));
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.generating_test));

        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                continueWithGeneratingTest(fragment, test, selectedWords);
            }
            @Override
            public void networkDisabled() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_network));
            }
            @Override
            public void internetNotAvailable() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_internet_connection));
            }
            @Override
            public void notConnected() {
                fragment.requireActivity().runOnUiThread(fragment::closeProgressDialog);
            }
        }).check();

    }

    private void continueWithGeneratingTest(UserSelectWordsFragment fragment, Test test, ArrayList<WordDocument> selectedWords){
        TestService.getInstance().generateUserWordTest(selectedWords, test, new TestService.TestGenerationCallback() {
            @Override
            public void onComplete(@NonNull @NotNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();

                    if(testId.equals(TestService.NO_TEST_ID)){
                        liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
                        return;
                    }

                    // if test is schedule job is finished
                    if(test.isScheduled()){
                        GeneralUtilities.showShortToastMessage(fragment.requireContext(), fragment.getString(R.string.success_test_generated));
                        fragment.navigateToUserScheduledTestsFragment();
                        return;
                    }

                    // otherwise go to test fragment
                    fragment.navigateToTestFragment(test.getType(), testId);
                });
            }
        });
    }
}
