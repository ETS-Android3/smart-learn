package com.smart_learn.presenter.helpers.fragments.expressions;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class BasicExpressionsFragment <T, VM extends BasicExpressionsViewModel<?>> extends BasicFragmentForRecyclerView<VM> {

    public static final String SELECTED_LESSON_KEY = "SELECTED_LESSON_KEY";

    protected void onAdapterSimpleClick(@NonNull @NotNull T item){}
    protected void onAdapterLongClick(@NonNull @NotNull T item){}

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