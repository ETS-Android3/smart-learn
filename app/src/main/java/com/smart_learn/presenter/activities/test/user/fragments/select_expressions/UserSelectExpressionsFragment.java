package com.smart_learn.presenter.activities.test.user.fragments.select_expressions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.expressions.user.select.UserBasicSelectExpressionsFragment;

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
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) this.requireActivity(), getString(R.string.selected_point) + " " + value);
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