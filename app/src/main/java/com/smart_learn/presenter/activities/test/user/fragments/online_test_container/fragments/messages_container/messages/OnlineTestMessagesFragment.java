package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.messages_container.messages;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.helpers.adapters.test.online.chat_message.GroupChatMessagesAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;


public class OnlineTestMessagesFragment extends BasicFragmentForRecyclerView<OnlineTestMessagesViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<OnlineTestMessagesViewModel> getModelClassForViewModel() {
        return OnlineTestMessagesViewModel.class;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_messages;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected boolean startFromEnd() {
        return true;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.online_test;
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        UserTestSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);

        if (sharedViewModel.getSelectedOnlineContainerTestId() == null || sharedViewModel.getSelectedOnlineContainerTestId().isEmpty()) {
            showMessage(R.string.error_can_not_continue);
            requireActivity().onBackPressed();
            return;
        }

        viewModel.setAdapter(new GroupChatMessagesAdapter(sharedViewModel.getSelectedOnlineContainerTestId(), new GroupChatMessagesAdapter.Callback() {
            @Override
            public boolean scrollDirectlyToLastItem() {
                return true;
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                // no action needed here
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {
                // no action needed here
            }

            @Override
            public boolean showCheckedIcon() {
                return false;
            }

            @Override
            public boolean showToolbar() {
                return false;
            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                // no action needed here
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return OnlineTestMessagesFragment.this;
            }
        }));

    }


}