package com.smart_learn.presenter.activities.notebook.guest.fragments.home_expression;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_expression.HomeExpressionFragment;

import org.jetbrains.annotations.NotNull;


public class GuestHomeExpressionFragment extends HomeExpressionFragment<GuestHomeExpressionViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestHomeExpressionViewModel> getModelClassForViewModel() {
        return GuestHomeExpressionViewModel.class;
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