package com.smart_learn.presenter.activities.main.fragments.notifications;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.activities.main.fragments.notifications.helpers.NotificationsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public class NotificationsViewModel extends BasicViewModelForRecyclerView<NotificationsAdapter> {

    public NotificationsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
