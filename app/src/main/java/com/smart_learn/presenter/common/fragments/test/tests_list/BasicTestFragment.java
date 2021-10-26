package com.smart_learn.presenter.common.fragments.test.tests_list;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;


public abstract class BasicTestFragment<T, VM extends BasicTestViewModel<?>> extends BasicFragmentForRecyclerView<VM> {

    protected void onAdapterSimpleClick(@NonNull @NotNull T item){}
    protected void onAdapterLongClick(@NonNull @NotNull T item){}

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_tests;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.tests;
    }

}