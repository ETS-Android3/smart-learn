package com.smart_learn.presenter.activities.test.user.fragments.history;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.helpers.fragments.tests.history.user.standard.UserStandardTestHistoryViewModel;

import org.jetbrains.annotations.NotNull;

public class UserTestHistoryViewModel extends UserStandardTestHistoryViewModel {

    public UserTestHistoryViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void continueTest(@NonNull @NotNull UserTestHistoryFragment fragment, DocumentSnapshot item){
        if(item == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            return;
        }

        TestDocument test = item.toObject(TestDocument.class);
        if(test == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            return;
        }

        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> fragment.goToContinueTestFragment(test.getType(), item.getId()));
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
                // no action needed here
            }
        }).check();
    }

}

