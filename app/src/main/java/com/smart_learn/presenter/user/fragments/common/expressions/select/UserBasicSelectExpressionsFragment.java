package com.smart_learn.presenter.user.fragments.common.expressions.select;

import com.smart_learn.R;
import com.smart_learn.presenter.user.fragments.common.expressions.UserBasicExpressionsFragment;


public abstract class UserBasicSelectExpressionsFragment<VM extends UserBasicSelectExpressionsViewModel> extends UserBasicExpressionsFragment<VM> {

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.selected_point;
    }

    @Override
    protected boolean onAdapterShowCheckedIcon() {
        return true;
    }

    @Override
    protected void afterAdapterIsSet() {
        super.afterAdapterIsSet();
        // here will be used only selection mode
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(viewModel.getAdapter() != null){
            onAdapterUpdateSelectedItemsCounter(viewModel.getAdapter().getSelectedValues().size());
        }
    }

}