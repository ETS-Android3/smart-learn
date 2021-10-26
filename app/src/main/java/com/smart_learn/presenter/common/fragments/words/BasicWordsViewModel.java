package com.smart_learn.presenter.common.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.presenter.common.helpers.PresenterHelpers;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class BasicWordsViewModel <AD extends RecyclerView.Adapter<?> & PresenterHelpers.AdapterHelper> extends BasicViewModelForRecyclerView<AD> {
    public BasicWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
