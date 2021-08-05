package com.smart_learn.presenter.activities.notebook.user.fragments.participants;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.lessons.shared_lesson.SharedLessonParticipantsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;


public class SharedLessonParticipantsViewModel extends BasicViewModelForRecyclerView<SharedLessonParticipantsAdapter> {

    public SharedLessonParticipantsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}