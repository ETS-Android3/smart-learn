package com.smart_learn.presenter.helpers.fragments.tests;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;


public abstract class BasicTestViewModel<AD extends RecyclerView.Adapter<?> & PresenterHelpers.AdapterHelper> extends BasicViewModelForRecyclerView<AD> {
    public BasicTestViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}