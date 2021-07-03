package com.smart_learn.presenter.activities.main.fragments.notifications;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.presenter.activities.main.fragments.notifications.helpers.NotificationDialog;
import com.smart_learn.presenter.activities.main.fragments.notifications.helpers.NotificationsAdapter;
import com.smart_learn.presenter.helpers.Callbacks;
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
        viewModel.setAdapter(new NotificationsAdapter(new Callbacks.FragmentGeneralCallback<NotificationsFragment>() {
            @Override
            public NotificationsFragment getFragment() {
                return NotificationsFragment.this;
            }
        }, NotificationsAdapter.getInitialAdapterOptions(NotificationsFragment.this)));

    }

    public void showNotificationDialog(NotificationDocument notification){
        DialogFragment dialogFragment = new NotificationDialog(notification);
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "NotificationsFragment");
    }

}