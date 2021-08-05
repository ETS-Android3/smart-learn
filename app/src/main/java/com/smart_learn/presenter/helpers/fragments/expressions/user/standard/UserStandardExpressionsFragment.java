package com.smart_learn.presenter.helpers.fragments.expressions.user.standard;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.fragments.expressions.helpers.ExpressionDialog;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.fragments.expressions.user.UserBasicExpressionsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public abstract class UserStandardExpressionsFragment <VM extends UserStandardExpressionsViewModel> extends UserBasicExpressionsFragment<VM> {

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected void onFloatingActionButtonPress() {
        showAddExpressionDialog();
    }

    @Override
    protected boolean isBottomSheetUsed() {
        return true;
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
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }

    @Override
    protected boolean onAdapterShowCheckedIcon() {
        return true;
    }

    @Override
    protected void onAdapterLongClick(@NonNull @NotNull DocumentSnapshot item) {
        super.onAdapterLongClick(item);
        startFragmentActionMode();
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        super.onAdapterUpdateSelectedItemsCounter(value);
        showSelectedItems(value);
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


    private void showSelectedItems(int value){
        this.requireActivity().runOnUiThread(() -> {
            if(actionMode != null) {
                actionMode.setTitle(getString(R.string.selected_point) + " " + value);
            }
        });
    }

    private void startFragmentActionMode() {
        super.startActionMode(new Callbacks.ActionModeCustomCallback() {
            @Override
            public void onCreateActionMode() {
                onActionModeCreate();
            }
            @Override
            public void onDestroyActionMode() {
                onActionModeDestroy();
            }
        });
    }

    private void showAddExpressionDialog(){
        ExpressionDialog dialog = new ExpressionDialog(new ExpressionDialog.Callback() {
            @Override
            public void onAddExpression(@NonNull @NotNull String expressionValue, @NonNull @NotNull String notes,
                                        @NonNull @NotNull ArrayList<Translation> translations) {
                boolean isSharedLessonSelected = getArguments() != null && getArguments().getBoolean(IS_SHARED_LESSON_SELECTED);
                viewModel.addExpression(isSharedLessonSelected, expressionValue, notes, translations);
            }
        });
        dialog.show(requireActivity().getSupportFragmentManager(), "UserStandardExpressionsFragment");
    }

    protected void onActionModeCreate() {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(true);
        }
    }

    protected void onActionModeDestroy() {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(false);
        }
    }
}