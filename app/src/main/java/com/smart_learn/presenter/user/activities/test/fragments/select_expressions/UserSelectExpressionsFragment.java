package com.smart_learn.presenter.user.activities.test.fragments.select_expressions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.user.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.common.entities.Translation;
import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.user.activities.test.UserTestSharedViewModel;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.user.fragments.common.expressions.select.UserBasicSelectExpressionsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class UserSelectExpressionsFragment extends UserBasicSelectExpressionsFragment<UserSelectExpressionsViewModel> {

    private UserTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserSelectExpressionsViewModel> getModelClassForViewModel() {
        return UserSelectExpressionsViewModel.class;
    }

    @Override
    protected int getFloatingActionButtonIconResourceId() {
        return R.drawable.ic_baseline_navigate_next_24;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected void onFloatingActionButtonPress() {
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }
        viewModel.generateTest(UserSelectExpressionsFragment.this, sharedViewModel.getGeneratedTest());
    }

    @Override
    protected boolean onAdapterIsSelectedItemValid(@NonNull @NotNull DocumentSnapshot item) {
        ExpressionDocument expression = item.toObject(ExpressionDocument.class);
        if(expression == null){
            showMessage(R.string.error_expression_can_not_be_selected);
            return false;
        }
        ArrayList<Translation> translations = Translation.fromJsonToList(expression.getTranslations());
        if(translations.isEmpty()){
            showMessage(R.string.error_expression_has_no_translation);
            return false;
        }
        return true;
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) this.requireActivity(), getString(R.string.selected_point) + " " + value);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    protected void navigateToTestFragment(int type, String testId, boolean isOnline){
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }

        ((UserTestActivity)requireActivity()).goToActivateTestFragment(type, testId, isOnline);
    }

    protected void navigateToUserScheduledTestsFragment(){
        ((UserTestActivity)requireActivity()).goToUserScheduledTestsFragment();
    }
}