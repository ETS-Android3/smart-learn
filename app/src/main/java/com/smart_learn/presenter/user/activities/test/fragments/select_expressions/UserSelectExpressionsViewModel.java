package com.smart_learn.presenter.user.activities.test.fragments.select_expressions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.core.common.helpers.ConnexionChecker;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.user.activities.test.fragments.select_expressions.UserSelectExpressionsFragment;
import com.smart_learn.presenter.user.fragments.common.expressions.select.UserBasicSelectExpressionsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;


public class UserSelectExpressionsViewModel extends UserBasicSelectExpressionsViewModel {

    public UserSelectExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void generateTest(@NonNull @NotNull UserSelectExpressionsFragment fragment, Test test){
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

        ArrayList<DocumentSnapshot> selectedExpressionsSnapshots = adapter.getSelectedValues();
        if(selectedExpressionsSnapshots.isEmpty()){
            liveToastMessage.setValue(fragment.getString(R.string.no_selected_expression));
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.generating_test));

        // for online tests must exists a connexion while generating tests because notifications
        // must be send to friends
        if((test instanceof TestDocument) && ((TestDocument)test).isOnline()){
            new ConnexionChecker(new ConnexionChecker.Callback() {
                @Override
                public void isConnected() {
                    continueWithGeneratingTest(fragment, test, selectedExpressionsSnapshots, true);
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
        else{
            // test is local so generate
            continueWithGeneratingTest(fragment, test, selectedExpressionsSnapshots, false);
        }
    }

    private void continueWithGeneratingTest(UserSelectExpressionsFragment fragment, Test test,
                                            ArrayList<DocumentSnapshot> selectedExpressionsSnapshots, boolean isOnline){
        TestService.getInstance().generateUserExpressionTest(selectedExpressionsSnapshots, test, new TestService.TestGenerationCallback() {
            @Override
            public void onSuccess(@NonNull @NotNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();

                    // if test is schedule job is finished
                    if(test.isScheduled()){
                        PresenterUtilities.General.showShortToastMessage(fragment.requireContext(), fragment.getString(R.string.success_test_generated));
                        fragment.navigateToUserScheduledTestsFragment();
                        return;
                    }

                    // otherwise go to test fragment
                    fragment.navigateToTestFragment(test.getType(), testId, isOnline);
                });
            }

            @Override
            public void onFailure(@NonNull @NotNull String error) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(error);
                });
            }
        });
    }

}