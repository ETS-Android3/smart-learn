package com.smart_learn.presenter.guest.fragments.common.words.select;

import com.smart_learn.R;
import com.smart_learn.presenter.guest.fragments.common.words.GuestBasicWordsFragment;

public abstract class GuestBasicSelectWordsFragment<VM extends GuestBasicSelectWordsViewModel> extends GuestBasicWordsFragment<VM> {

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
    protected void setViewModel() {
        super.setViewModel();

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