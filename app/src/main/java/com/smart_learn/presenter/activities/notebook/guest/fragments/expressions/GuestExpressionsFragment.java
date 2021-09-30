package com.smart_learn.presenter.activities.notebook.guest.fragments.expressions;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.expressions.guest.standard.GuestStandardExpressionsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class GuestExpressionsFragment extends GuestStandardExpressionsFragment<GuestExpressionsViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestExpressionsViewModel> getModelClassForViewModel() {
        return GuestExpressionsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull Expression item) {
        super.onAdapterSimpleClick(item);
        goToGuestHomeExpressionFragment(item);
    }

    @Override
    protected void onActionModeCreate() {
        super.onActionModeCreate();
        ((GuestNotebookActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void onActionModeDestroy() {
        super.onActionModeDestroy();
        ((GuestNotebookActivity)requireActivity()).showBottomNavigationMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedExpressionId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);
    }

    public void goToGuestHomeExpressionFragment(Expression expression){
        if(expression == null || expression.getExpressionId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_expression_can_not_be_opened));
            return;
        }

        // first set current expression id (expression which is clicked) and specific url`s on the shared view model
        sharedViewModel.setSelectedExpressionId(expression.getExpressionId());
        // and then navigate
        ((GuestNotebookActivity)requireActivity()).goToGuestHomeExpressionFragment();
    }

}