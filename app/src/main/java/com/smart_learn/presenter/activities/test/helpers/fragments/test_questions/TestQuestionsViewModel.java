package com.smart_learn.presenter.activities.test.helpers.fragments.test_questions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.data.entities.Question;
import com.smart_learn.presenter.helpers.adapters.test.questions.QuestionsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class TestQuestionsViewModel extends BasicViewModelForRecyclerView<QuestionsAdapter> {
    public TestQuestionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
