package com.smart_learn.presenter.activities.test.user.fragments.select_expressions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.presenter.activities.test.user.fragments.select_words.UserSelectWordsFragment;
import com.smart_learn.presenter.helpers.fragments.expressions.user.select.UserBasicSelectExpressionsViewModel;

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

        ArrayList<ExpressionDocument> selectedExpressions = new ArrayList<>();
        for(DocumentSnapshot snapshot : selectedExpressionsSnapshots){
            if(snapshot == null){
                continue;
            }
            ExpressionDocument expression = snapshot.toObject(ExpressionDocument.class);
            if (expression == null) {
                Timber.w("expression is null");
                liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
                return;
            }
            selectedExpressions.add(expression);
        }

        if(selectedExpressions.isEmpty()){
            Timber.w("selectedExpressions is empty");
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.generating_test));

        TestService.getInstance().generateUserExpressionTest(selectedExpressions, test, new TestService.TestGenerationCallback() {
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