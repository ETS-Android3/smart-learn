package com.smart_learn.presenter.activities.main.fragments.notifications;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.presenter.activities.main.fragments.notifications.helpers.NotificationDialog;
import com.smart_learn.presenter.helpers.adapters.notifications.NotificationsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

public class NotificationsFragment extends BasicFragmentForRecyclerView<NotificationsViewModel> {

    @Override
    @NonNull
    @NotNull
    protected Class<NotificationsViewModel> getModelClassForViewModel() {
        return NotificationsViewModel.class;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_notifications;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.notifications;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set fragment view model adapter
        viewModel.setAdapter(new NotificationsAdapter(new NotificationsAdapter.Callback() {
            @Override
            public boolean showCheckedIcon() {
                return false;
            }

            @Override
            public boolean showToolbar() {
                return true;
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                showNotificationDialog(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {

            }

            @Override
            public void updateSelectedItemsCounter(int value) {

            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return NotificationsFragment.this;
            }
        }));

    }

    public void showNotificationDialog(@NonNull @NotNull DocumentSnapshot notificationSnapshot){
        DialogFragment dialogFragment = new NotificationDialog(notificationSnapshot.toObject(NotificationDocument.class),
                new NotificationDialog.Callback() {
                    @Override
                    public void onAcceptPress(@NonNull @NotNull NotificationDialog.Listener listener) {
                        viewModel.acceptFriendRequest(NotificationsFragment.this, notificationSnapshot, listener);
                    }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "NotificationsFragment");
    }

}