package com.smart_learn.presenter.user.activities.notebook.fragments.expressions;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.data.user.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.presenter.common.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.user.activities.notebook.UserNotebookActivity;
import com.smart_learn.presenter.user.activities.notebook.UserNotebookSharedViewModel;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.user.fragments.common.expressions.standard.UserStandardExpressionsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class UserExpressionsFragment extends UserStandardExpressionsFragment<UserExpressionsViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserExpressionsViewModel> getModelClassForViewModel() {
        return UserExpressionsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
        super.onAdapterSimpleClick(item);
        goToUserHomeExpressionFragment(item);
    }

    @Override
    protected void onActionModeCreate() {
        super.onActionModeCreate();
        ((UserNotebookActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void onActionModeDestroy() {
        super.onActionModeDestroy();
        ((UserNotebookActivity)requireActivity()).showBottomNavigationMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedExpression(null);
    }


    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);
    }

    public void goToUserHomeExpressionFragment(DocumentSnapshot expressionSnapshot){
        // when navigation is made a valid expression must be set on shared view model
        if(expressionSnapshot == null){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_expression_can_not_be_opened));
            return;
        }

        ExpressionDocument expression = expressionSnapshot.toObject(ExpressionDocument.class);
        if(expression == null){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_expression_can_not_be_opened));
            return;
        }

        // First set current expression snapshot on the shared view model and specific url`s on the
        // shared view model then you can navigate.
        sharedViewModel.setSelectedExpression(expressionSnapshot);

        final boolean isExpressionOwner = expression.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid());
        ((UserNotebookActivity)requireActivity()).goToUserHomeExpressionFragment(isExpressionOwner);
    }

}