package com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.messages_container.messages;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.adapters.test.online.GroupChatMessagesAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public class OnlineTestMessagesViewModel extends BasicViewModelForRecyclerView<GroupChatMessagesAdapter> {

    public OnlineTestMessagesViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
