package com.smart_learn.presenter.common.fragments.test.tests_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.presenter.common.helpers.PresenterHelpers;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;


public abstract class BasicTestViewModel<AD extends RecyclerView.Adapter<?> & PresenterHelpers.AdapterHelper> extends BasicViewModelForRecyclerView<AD> {
    public BasicTestViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
