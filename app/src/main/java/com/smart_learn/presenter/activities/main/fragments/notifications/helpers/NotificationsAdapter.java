package com.smart_learn.presenter.activities.main.fragments.notifications.helpers;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.databinding.LayoutCardViewNotificationBinding;
import com.smart_learn.presenter.activities.main.fragments.notifications.NotificationsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.adapters.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

public class NotificationsAdapter extends BasicFirestoreRecyclerAdapter<NotificationDocument, NotificationsAdapter.NotificationViewHolder> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    protected final Callbacks.FragmentGeneralCallback<NotificationsFragment> fragmentCallback;

    public NotificationsAdapter(@NonNull @NotNull Callbacks.FragmentGeneralCallback<NotificationsFragment> fragmentCallback,
                                @NonNull @NotNull FirestoreRecyclerOptions<NotificationDocument> initialOptions) {
        super(fragmentCallback.getFragment(), initialOptions, INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.fragmentCallback = fragmentCallback;
    }


    @NonNull
    @NotNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewNotificationBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_notification, parent, false);
        viewHolderBinding.setLifecycleOwner(fragmentCallback.getFragment());

        // link data binding layout with view holder
        NotificationViewHolder viewHolder = new NotificationViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void loadMoreData() {
        Query query = fragmentCallback.getFragment().getViewModel().getNotificationService()
                .getQueryForAllVisibleNotifications(currentLoad + loadingStep);
        super.loadData(query, NotificationDocument.class, fragmentCallback.getFragment());
    }

    public static FirestoreRecyclerOptions<NotificationDocument> getInitialAdapterOptions(@NonNull @NotNull NotificationsFragment fragment) {
        Query query = fragment.getViewModel().getNotificationService()
                .getQueryForAllVisibleNotifications(NotificationsAdapter.INITIAL_ADAPTER_CAPACITY);
        return new FirestoreRecyclerOptions.Builder<NotificationDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, NotificationDocument.class)
                .build();
    }

    public final class NotificationViewHolder extends BasicViewHolder<NotificationDocument, LayoutCardViewNotificationBinding> {

        public NotificationViewHolder(@NonNull @NotNull LayoutCardViewNotificationBinding viewHolderBinding) {
            super(viewHolderBinding);

            // disable checked icon because is not necessary
            viewHolderBinding.cvLayoutCardViewNotification.setCheckedIcon(null);

            setListeners();
        }

        private void setListeners(){
            // menu listeners
            viewHolderBinding.toolbarLayoutCardViewNotification.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    int position = getAdapterPosition();
                    if (id == R.id.action_hide_notification_menu_card_view_notification) {
                        fragmentCallback.getFragment().getViewModel()
                                .getNotificationService().markAsHidden(getSnapshots().getSnapshot(position),null);
                        return true;
                    }
                    return true;
                }
            });

            // simple click action
            viewHolderBinding.cvLayoutCardViewNotification.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    NotificationDocument notification = getItem(position);
                    fragmentCallback.getFragment().showNotificationDialog(notification);
                    if(!notification.getMarkedAsRead()){
                        // when is clicked notification is marked as read if was unread
                        fragmentCallback.getFragment().getViewModel()
                                .getNotificationService().markAsRead(getSnapshots().getSnapshot(position),null);
                    }
                }
            });
        }

        @Override
        protected NotificationDocument getEmptyLiveItemInfo() {
            return new NotificationDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull NotificationDocument notification){

            viewHolderBinding.cvLayoutCardViewNotification.setChecked(!notification.getMarkedAsRead());

            notification.setTypeDescription(fragmentCallback.getFragment()
                    .getString(NotificationDocument.getNotificationTypeMessage(notification.getType())));

            liveItemInfo.setValue(notification);
        }
    }

}



