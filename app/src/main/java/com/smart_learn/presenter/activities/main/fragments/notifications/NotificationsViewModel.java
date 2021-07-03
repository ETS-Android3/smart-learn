package com.smart_learn.presenter.activities.main.fragments.notifications;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.NotificationService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.activities.main.fragments.notifications.helpers.NotificationsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class NotificationsViewModel extends BasicViewModelForRecyclerView<NotificationsAdapter> {

    @Getter
    private final NotificationService notificationService;

    public NotificationsViewModel(@NonNull @NotNull Application application) {
        super(application);
        notificationService = new NotificationService(CoreUtilities.Auth.getUserUid());
    }
}
