package com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.participants;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.adapters.test.online.TestParticipantsAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;


public class OnlineTestParticipantsViewModel extends BasicViewModelForRecyclerView<TestParticipantsAdapter> {

    public OnlineTestParticipantsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}