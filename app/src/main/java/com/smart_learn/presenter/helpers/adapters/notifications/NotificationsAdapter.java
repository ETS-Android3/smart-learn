package com.smart_learn.presenter.helpers.adapters.notifications;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.NotificationService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewNotificationBinding;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationsAdapter extends BasicFirestoreRecyclerAdapter<NotificationDocument, NotificationsAdapter.NotificationViewHolder, NotificationsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    public NotificationsAdapter(@NonNull @NotNull NotificationsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
    }

    @NonNull
    @NotNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewNotificationBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_notification, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // link data binding layout with view holder
        NotificationViewHolder viewHolder = new NotificationViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void loadMoreData() {
        Query query = NotificationService.getInstance().getQueryForAllVisibleNotifications(currentLoad + loadingStep);
        super.loadData(query, NotificationDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<NotificationDocument> getInitialAdapterOptions(@NonNull @NotNull Fragment fragment) {
        Query query = NotificationService.getInstance().getQueryForAllVisibleNotifications(NotificationsAdapter.INITIAL_ADAPTER_CAPACITY);
        return new FirestoreRecyclerOptions.Builder<NotificationDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, NotificationDocument.class)
                .build();
    }

    public final class NotificationViewHolder extends BasicViewHolder<NotificationDocument, LayoutCardViewNotificationBinding> {

        private final MutableLiveData<String> liveDateDifferenceDescription;
        private final AtomicBoolean isHideActive;

        public NotificationViewHolder(@NonNull @NotNull LayoutCardViewNotificationBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveDateDifferenceDescription = new MutableLiveData<>("");
            isHideActive = new AtomicBoolean(false);
            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewNotification, viewHolderBinding.cvLayoutCardViewNotification);
            setListeners();
        }

        public LiveData<String> getLiveDateDifferenceDescription() {
            return liveDateDifferenceDescription;
        }

        private void setListeners(){
            if(adapterCallback.showToolbar()){
                setToolbarListeners();
            }

            // simple click action
            viewHolderBinding.cvLayoutCardViewNotification.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }
                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
                    NotificationDocument notification = getItem(position);
                    if(!notification.getMarkedAsRead()){
                        // when is clicked notification is marked as read if was unread
                        NotificationService.getInstance().markAsRead(getSnapshots().getSnapshot(position),null);
                    }
                }
            });
        }

        private void setToolbarListeners(){
            viewHolderBinding.toolbarLayoutCardViewNotification.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    int id = item.getItemId();
                    if (id == R.id.action_hide_notification_menu_card_view_notification) {
                        if(isHideActive.get()){
                            return true;
                        }
                        isHideActive.set(true);
                        NotificationService.getInstance().markAsHidden(getSnapshots().getSnapshot(position), new DataCallbacks.General() {
                            @Override
                            public void onSuccess() {
                                isHideActive.set(false);
                            }
                            @Override
                            public void onFailure() {
                                isHideActive.set(false);
                            }
                        });
                        return true;
                    }
                    return true;
                }
            });
        }

        @Override
        protected NotificationDocument getEmptyLiveItemInfo() {
            return new NotificationDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull NotificationDocument notification, int position){

            viewHolderBinding.cvLayoutCardViewNotification.setChecked(!notification.getMarkedAsRead());

            notification.setTitle(adapterCallback.getFragment()
                    .getString(NotificationDocument.generateNotificationTitle(notification.getType())));

            notification.setDescription(NotificationDocument.generateNotificationDescription(notification.getType(),
                    notification.getExtraInfo()));

            liveItemInfo.setValue(notification);

            String dateDifferenceDescription = " - " + CoreUtilities.General.getFormattedTimeDifferenceFromPastToPresent(notification.getDocumentMetadata().getCreatedAt());
            liveDateDifferenceDescription.setValue(dateDifferenceDescription);
        }
    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback {
    }

}



