package com.smart_learn.presenter.common.fragments.expressions;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class BasicExpressionsFragment <T, VM extends BasicExpressionsViewModel<?>> extends BasicFragmentForRecyclerView<VM> {

    public static final String SELECTED_LESSON_KEY = "SELECTED_LESSON_KEY";

    protected void onAdapterSimpleClick(@NonNull @NotNull T item){}
    protected void onAdapterLongClick(@NonNull @NotNull T item){}
    protected boolean onAdapterIsSelectedItemValid(@NonNull @NotNull T item){
        return true;
    }

    @Override
    protected boolean useToolbarMenu() {
        return true;
    }

    @Override
    protected boolean useSearchOnMenu() {
        return true;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_expressions;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.expressions;
    }

}