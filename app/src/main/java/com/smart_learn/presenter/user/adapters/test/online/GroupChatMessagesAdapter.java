package com.smart_learn.presenter.user.adapters.test.online;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.user.firebase.firestore.entities.GroupChatMessageDocument;
import com.smart_learn.databinding.LayoutCardViewChatMessageBinding;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.user.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.common.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;


public class GroupChatMessagesAdapter extends BasicFirestoreRecyclerAdapter<GroupChatMessageDocument, GroupChatMessagesAdapter.ChatMessageViewHolder, GroupChatMessagesAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    private final String currentTestId;

    public GroupChatMessagesAdapter(@NonNull @NotNull String currentTestId,
                                    @NonNull @NotNull GroupChatMessagesAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(currentTestId, adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.currentTestId = currentTestId;
    }

    @NonNull
    @NotNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewChatMessageBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_chat_message, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // link data binding layout with view holder
        ChatMessageViewHolder viewHolder = new ChatMessageViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void loadMoreData() {
        Query query = TestService.getInstance().getQueryForOnlineTestChatMessages(currentTestId,currentLoad + loadingStep);
        super.loadData(query, GroupChatMessageDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<GroupChatMessageDocument> getInitialAdapterOptions(@NonNull @NotNull String currentTestId,
                                                                                               @NonNull @NotNull Fragment fragment) {
        Query query = TestService.getInstance().getQueryForOnlineTestChatMessages(currentTestId, INITIAL_ADAPTER_CAPACITY);
        return new FirestoreRecyclerOptions.Builder<GroupChatMessageDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, GroupChatMessageDocument.class)
                .build();
    }

    public final class ChatMessageViewHolder extends BasicViewHolder<GroupChatMessageDocument, LayoutCardViewChatMessageBinding> {

        private final MutableLiveData<String> liveTimeDescription;
        private final MutableLiveData<Boolean> liveIsMessageOwner;
        private final MutableLiveData<Boolean> liveIsMessageArrivedToServer;
        private ListenerRegistration listenerRegistration;


        public ChatMessageViewHolder(@NonNull @NotNull LayoutCardViewChatMessageBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveTimeDescription = new MutableLiveData<>("");
            liveIsMessageOwner = new MutableLiveData<>(false);
            liveIsMessageArrivedToServer = new MutableLiveData<>(false);
            listenerRegistration = null;
        }

        public LiveData<String> getLiveTimeDescription() {
            return liveTimeDescription;
        }

        public LiveData<Boolean> getLiveIsMessageOwner() {
            return liveIsMessageOwner;
        }

        public LiveData<Boolean> getLiveIsMessageArrivedToServer() {
            return liveIsMessageArrivedToServer;
        }

        @Override
        public GroupChatMessageDocument getEmptyLiveItemInfo() {
            return new GroupChatMessageDocument();
        }

        @Override
        public void bind(@NonNull @NotNull GroupChatMessageDocument item, int position){
            PresenterUtilities.Activities.loadProfileImage(item.getFromUserProfilePhotoUrl(), viewHolderBinding.ivProfileLayoutCardViewChatMessage);

            liveItemInfo.setValue(item);

            final boolean isMessageOwner = item.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid());
            liveIsMessageOwner.setValue(isMessageOwner);
            liveTimeDescription.setValue(CoreUtilities.General.longToDateTime(item.getDocumentMetadata().getCreatedAt()));

            // This is used to show successfully send icon in view layout if message arrived to server,
            // or pending send if message is only in cache.
            DocumentSnapshot messageSnapshot = getSnapshots().getSnapshot(position);
            final boolean hasPendingWrites = messageSnapshot.getMetadata().hasPendingWrites();
            liveIsMessageArrivedToServer.setValue(!hasPendingWrites);
            if(hasPendingWrites){
                // If pending writes exist means that message does not arrived to server yet
                // (message is only in cache), so listen for metadata changes in order to update message
                // icon after the message arrived to server.
                // https://stackoverflow.com/questions/51463299/how-to-check-if-firebase-firestore-transactions-have-updated-to-server/60930004#60930004
                listenMessageMetadata(messageSnapshot);
            }
        }

        private void listenMessageMetadata(DocumentSnapshot messageSnapshot){
            // https://firebase.google.com/docs/firestore/query-data/listen#java_4
            listenerRegistration = messageSnapshot.getReference()
                    .addSnapshotListener(
                            adapterCallback.getFragment().requireActivity(),
                            MetadataChanges.INCLUDE,
                            new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value,
                                                    @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                    if(value != null && !value.getMetadata().hasPendingWrites()){
                                        // If no pending writes exist means that message arrived to
                                        // server, so update liveData value in order to change message
                                        // icon, and remove listener because is no need to listen for
                                        // further changes.
                                        liveIsMessageArrivedToServer.postValue(true);
                                        removeListener();
                                    }
                                }
                            });
        }

        private void removeListener(){
            if (listenerRegistration != null){
                listenerRegistration.remove();
                listenerRegistration = null;
            }
        }

    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback {

    }

}