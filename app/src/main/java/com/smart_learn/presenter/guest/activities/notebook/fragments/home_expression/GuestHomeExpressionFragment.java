package com.smart_learn.presenter.guest.activities.notebook.fragments.home_expression;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.core.guest.services.GuestExpressionService;
import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.presenter.guest.activities.notebook.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.common.activities.notebook.fragments.home_expression.HomeExpressionFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class GuestHomeExpressionFragment extends HomeExpressionFragment<GuestHomeExpressionViewModel> {

    @Getter
    protected GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestHomeExpressionViewModel> getModelClassForViewModel() {
        return GuestHomeExpressionViewModel.class;
    }

    @Override
    protected boolean isExpressionOwner() {
        return true;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);

        GuestExpressionService.getInstance().getSampleLiveExpression(sharedViewModel.getSelectedExpressionId()).observe(this, new Observer<Expression>() {
            @Override
            public void onChanged(Expression expression) {
                viewModel.setLiveExpression(expression);
            }
        });
    }
}