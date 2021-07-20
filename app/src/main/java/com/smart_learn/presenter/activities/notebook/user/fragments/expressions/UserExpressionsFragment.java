package com.smart_learn.presenter.activities.notebook.user.fragments.expressions;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.expressions.ExpressionsFragment;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.user.fragments.expressions.helpers.ExpressionsAdapter;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class UserExpressionsFragment extends ExpressionsFragment<UserExpressionsViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserExpressionsViewModel> getModelClassForViewModel() {
        return UserExpressionsViewModel.class;
    }

    @Override
    protected int getBottomSheetLayout() {
        return R.layout.layout_action_mode_fragment_user_expressions;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_fragment_user_expressions;
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(TextUtils.isEmpty(newText)){
            viewModel.getAdapter().setInitialOption(UserExpressionsFragment.this);
        }
        else {
            viewModel.getAdapter().setFilterOption(UserExpressionsFragment.this, newText);
        }
    }

    @Override
    protected void onActionModeCreate() {
        if(viewModel.getAdapter() != null){
            ((UserNotebookActivity)requireActivity()).hideBottomNavigationMenu();
            viewModel.getAdapter().resetSelectedItems();
            viewModel.getAdapter().setLiveActionMode(true);
        }
    }

    @Override
    protected void onActionModeDestroy() {
        if(viewModel.getAdapter() != null){
            ((UserNotebookActivity)requireActivity()).showBottomNavigationMenu();
            viewModel.getAdapter().resetSelectedItems();
            viewModel.getAdapter().setLiveActionMode(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.setSelectedExpression(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // set bottom sheet listeners
        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_fragment_user_expressions);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteSelectedExpressions();
            }
        });
    }


    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);

        // set current lesson on view model for further operations inside view model
        viewModel.setCurrentLessonSnapshot(sharedViewModel.getSelectedLesson());

        // set fragment view model adapter
        viewModel.setAdapter(new ExpressionsAdapter(sharedViewModel.getSelectedLesson(), new ExpressionsAdapter.Callback<UserExpressionsFragment>() {
            @Override
            public UserExpressionsFragment getFragment() {
                return UserExpressionsFragment.this;
            }
        }));

    }

    public void goToUserHomeExpressionFragment(DocumentSnapshot expressionSnapshot){
        // when navigation is made a valid expression must be set on shared view model
        if(expressionSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_expression_can_not_be_opened));
            return;
        }

        ExpressionDocument expression = expressionSnapshot.toObject(ExpressionDocument.class);
        if(expression == null){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_expression_can_not_be_opened));
            return;
        }

        // First set current expression snapshot on the shared view model and specific url`s on the
        // shared view model then you can navigate.
        sharedViewModel.setSelectedExpression(expressionSnapshot);
        ((UserNotebookActivity)requireActivity()).goToUserHomeExpressionFragment();
    }

    public void showSelectedItems(int value){
        this.requireActivity().runOnUiThread(() -> {
            if(actionMode != null) {
                actionMode.setTitle(getString(R.string.selected_point) + " " + value);
            }
        });
    }
}