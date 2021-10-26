package com.smart_learn.presenter.common.activities.test.fragments.test_questions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.common.adapters.QuestionsAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class TestQuestionsViewModel extends BasicViewModelForRecyclerView<QuestionsAdapter> {
    public TestQuestionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
