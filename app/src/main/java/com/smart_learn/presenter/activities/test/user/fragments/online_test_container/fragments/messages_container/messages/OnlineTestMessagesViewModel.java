package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.messages_container.messages;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.test.online.chat_message.GroupChatMessagesAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public class OnlineTestMessagesViewModel extends BasicViewModelForRecyclerView<GroupChatMessagesAdapter> {

    public OnlineTestMessagesViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
