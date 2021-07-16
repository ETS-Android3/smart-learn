package com.smart_learn.presenter.activities.notebook.helpers.fragments.expressions;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class ExpressionsViewModel <AD extends RecyclerView.Adapter<?> & PresenterHelpers.AdapterHelper> extends BasicViewModelForRecyclerView<AD> {

    public ExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public abstract void addExpression(String expressionValue, String notes, ArrayList<Translation> translations);
}