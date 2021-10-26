package com.smart_learn.presenter.user.activities.notebook.fragments.participants;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.adapters.lessons.SharedLessonParticipantsAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;


public class SharedLessonParticipantsViewModel extends BasicViewModelForRecyclerView<SharedLessonParticipantsAdapter> {

    public SharedLessonParticipantsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}