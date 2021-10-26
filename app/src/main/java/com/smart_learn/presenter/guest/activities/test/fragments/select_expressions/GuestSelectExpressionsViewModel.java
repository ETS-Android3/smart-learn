package com.smart_learn.presenter.guest.activities.test.fragments.select_expressions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.activities.test.fragments.select_expressions.GuestSelectExpressionsFragment;
import com.smart_learn.presenter.guest.fragments.common.expressions.select.GuestBasicSelectExpressionsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class GuestSelectExpressionsViewModel extends GuestBasicSelectExpressionsViewModel {

    public GuestSelectExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void generateTest(@NonNull @NotNull GuestSelectExpressionsFragment fragment, Test test){
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

        ArrayList<Expression> selectedExpressions = adapter.getSelectedValues();
        if(selectedExpressions.isEmpty()){
            liveToastMessage.setValue(fragment.getString(R.string.no_selected_expression));
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.generating_test));

        TestService.getInstance().generateGuestExpressionTest(selectedExpressions, test, new TestService.TestGenerationCallback() {
            @Override
            public void onSuccess(@NonNull @NotNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();

                    // if test is schedule job is finished
                    if(test.isScheduled()){
                        PresenterUtilities.General.showShortToastMessage(fragment.requireContext(), fragment.getString(R.string.success_test_generated));
                        fragment.navigateToGuestScheduledTestsFragment();
                        return;
                    }

                    // otherwise go to test fragment
                    int testIdInteger;
                    try {
                        testIdInteger = Integer.parseInt(testId);
                    }
                    catch (NumberFormatException ex){
                        Timber.w(ex);
                        liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
                        return;
                    }
                    fragment.navigateToTestFragment(test.getType(), testIdInteger);
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