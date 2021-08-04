package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.participants;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.test.online.participants.TestParticipantsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;


public class OnlineTestParticipantsViewModel extends BasicViewModelForRecyclerView<TestParticipantsAdapter> {

    public OnlineTestParticipantsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}