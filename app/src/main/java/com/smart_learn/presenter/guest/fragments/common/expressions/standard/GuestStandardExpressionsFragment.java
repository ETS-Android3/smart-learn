package com.smart_learn.presenter.guest.fragments.common.expressions.standard;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.data.common.entities.Translation;
import com.smart_learn.presenter.common.fragments.expressions.helpers.ExpressionDialog;
import com.smart_learn.presenter.common.helpers.PresenterCallbacks;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.fragments.common.expressions.GuestBasicExpressionsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public abstract class GuestStandardExpressionsFragment <VM extends GuestStandardExpressionsViewModel> extends GuestBasicExpressionsFragment<VM> {

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
        return R.layout.layout_action_mode_fragment_guest_expressions;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_fragment_guest_expressions;
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
    protected void onAdapterLongClick(@NonNull @NotNull Expression item) {
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
        Button btnSelectAll = requireActivity().findViewById(R.id.btn_select_include_layout_action_mode_fragment_guest_expressions);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllItemsAreSelected(!viewModel.isAllItemsAreSelected());
                PresenterUtilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });

        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_fragment_guest_expressions);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteSelectedExpressions();
                viewModel.setAllItemsAreSelected(false);
                PresenterUtilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
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
        super.startActionMode(new PresenterCallbacks.ActionModeCustomCallback() {
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
                viewModel.addExpression(expressionValue, notes, translations);
            }
        });
        dialog.show(requireActivity().getSupportFragmentManager(), "ExpressionsFragment");
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