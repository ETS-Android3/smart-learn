package com.smart_learn.presenter.activities.notebook.guest.fragments.expressions;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.expressions.helpers.ExpressionsAdapter;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.expressions.ExpressionsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;


public class GuestExpressionsFragment extends ExpressionsFragment<GuestExpressionsViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestExpressionsViewModel> getModelClassForViewModel() {
        return GuestExpressionsViewModel.class;
    }

    @Override
    protected int getBottomSheetLayout() {
        return R.layout.include_layout_action_mode_guest;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_guest;
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().getFilter().filter(newText);
        }
    }

    @Override
    protected void onActionModeCreate() {
        ((GuestNotebookActivity)requireActivity()).hideBottomNavigationMenu();

        // mark that action mode started
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setLiveActionMode(true);
        }

        // Use this to prevent any previous selection. If an error occurred and
        // action mode could not be closed then items could not be disabled and will
        // hang as selected.  FIXME: try yo find a better way to do that
        GuestExpressionService.getInstance().updateSelectAll(false, sharedViewModel.getSelectedLessonId());
    }

    @Override
    protected void onActionModeDestroy() {
        ((GuestNotebookActivity)requireActivity()).showLessonGroupBottomNavigation();

        // use this to disable all selection
        GuestExpressionService.getInstance().updateSelectAll(false, sharedViewModel.getSelectedLessonId());

        // mark that action mode finished
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setLiveActionMode(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.setSelectedExpressionId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // this fragment does not need refreshing
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        // set bottom sheet listeners
        Button btnSelectAll = requireActivity().findViewById(R.id.btn_select_include_layout_action_mode_guest);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllItemsAreSelected(!viewModel.isAllItemsAreSelected());
                GuestExpressionService.getInstance().updateSelectAll(viewModel.isAllItemsAreSelected(), sharedViewModel.getSelectedLessonId());
                Utilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });

        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_guest);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestExpressionService.getInstance().deleteSelectedItems(sharedViewModel.getSelectedLessonId());
                viewModel.setAllItemsAreSelected(false);
                Utilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });
    }


    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);

        // set current lesson on view model for further operations inside view model
        viewModel.setCurrentLessonId(sharedViewModel.getSelectedLessonId());

        // set fragment view model adapter
        viewModel.setAdapter(new ExpressionsAdapter(new Callbacks.FragmentGeneralCallback<GuestExpressionsFragment>() {
            @Override
            public GuestExpressionsFragment getFragment() {
                return GuestExpressionsFragment.this;
            }
        }));

        // set observers
        GuestExpressionService.getInstance().getLiveSelectedItemsCount(sharedViewModel.getSelectedLessonId()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionMode != null){
                    actionMode.setTitle(getString(R.string.selected) + " " + integer);
                }
            }
        });

        GuestExpressionService.getInstance().getCurrentLessonLiveExpressions(sharedViewModel.getSelectedLessonId()).observe(this, new Observer<List<Expression>>() {
            @Override
            public void onChanged(List<Expression> expressions) {
                Utilities.Activities.changeTextViewStatus(expressions.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(expressions);
                }
            }
        });

    }

    public void goToGuestHomeExpressionFragment(Expression expression){
        if(expression == null || expression.getExpressionId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_expression_can_not_be_opened));
            return;
        }

        // first set current expression id (expression which is clicked) and specific url`s on the shared view model
        sharedViewModel.setSelectedExpressionId(expression.getExpressionId());
        // and then navigate
        ((GuestNotebookActivity)requireActivity()).goToGuestHomeExpressionFragment();
    }

}