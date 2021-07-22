package com.smart_learn.presenter.helpers.adapters.friends;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.FriendService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.databinding.LayoutCardViewFriendBinding;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

public class FriendsAdapter extends BasicFirestoreRecyclerAdapter<FriendDocument, FriendsAdapter.FriendViewHolder, FriendsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    public FriendsAdapter(@NonNull @NotNull FriendsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
    }

    @NonNull
    @NotNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewFriendBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_friend, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // link data binding layout with view holder
        FriendViewHolder viewHolder = new FriendViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void loadMoreData() {
        Query query;
        if(isFiltering){
            query = FriendService.getInstance().getQueryForFilter(currentLoad + loadingStep, filteringValue);
        }
        else{
            query = FriendService.getInstance().getQueryForAllAcceptedFriends(currentLoad + loadingStep);
        }
        super.loadData(query, FriendDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<FriendDocument> getInitialAdapterOptions(@NonNull @NotNull Fragment fragment) {
        Query query = FriendService.getInstance().getQueryForAllAcceptedFriends(INITIAL_ADAPTER_CAPACITY);
        return new FirestoreRecyclerOptions.Builder<FriendDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, FriendDocument.class)
                .build();
    }

    /**
     * Use to set initial query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * */
    public void setInitialOption(@NonNull @NotNull Fragment fragment){
        filteringValue = "";
        isFiltering = false;
        updateOptions(getInitialAdapterOptions(fragment));
    }


    /**
     * Use to set filtering query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * @param value Value to be search.
     * */
    public void setFilterOption(@NonNull @NotNull Fragment fragment, @NonNull @NotNull String value){
        filteringValue = value.toLowerCase();
        isFiltering = true;
        Query query = FriendService.getInstance().getQueryForFilter(INITIAL_ADAPTER_CAPACITY, filteringValue);
        FirestoreRecyclerOptions<FriendDocument> newOptions = new FirestoreRecyclerOptions.Builder<FriendDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, FriendDocument.class)
                .build();

        updateOptions(newOptions);
    }

    public final class FriendViewHolder extends BasicViewHolder<FriendDocument, LayoutCardViewFriendBinding> {

        public FriendViewHolder(@NonNull @NotNull LayoutCardViewFriendBinding viewHolderBinding) {
            super(viewHolderBinding);
            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewFriend, viewHolderBinding.cvLayoutCardViewFriend);
            setListeners();
        }

        @Override
        protected FriendDocument getEmptyLiveItemInfo() {
            return new FriendDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull FriendDocument friendDocument, int position){

            if(isFiltering){
                friendDocument.setSpannedEmail(Utilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(friendDocument.getEmail().toLowerCase(), filteringValue), friendDocument.getEmail()));

                friendDocument.setSpannedDisplayName(Utilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(friendDocument.getDisplayName().toLowerCase(), filteringValue), friendDocument.getDisplayName()));
            }
            else {
                friendDocument.setSpannedEmail(new SpannableString(friendDocument.getEmail()));
                friendDocument.setSpannedDisplayName(new SpannableString(friendDocument.getDisplayName()));
            }

            if (isSelectionModeActive()) {
                liveItemInfo.setValue(friendDocument);
                return;
            }

            setSelectionModeActive(false);
            viewHolderBinding.cvLayoutCardViewFriend.setChecked(false);

            // set the existent value
            liveItemInfo.setValue(friendDocument);

            // try to get updated data from the friend user profile
            FriendService.getInstance().syncFriendDocument(getSnapshots().getSnapshot(position), null);
        }

        private void setListeners(){
            if(adapterCallback.showToolbar()){
                setToolbarListener();
            }

            // simple click action on recycler view item
            viewHolderBinding.cvLayoutCardViewFriend.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    if(isSelectionModeActive()){
                        markItem(new Pair<>(getSnapshots().getSnapshot(position), new Pair<>(viewHolderBinding.cvLayoutCardViewFriend, new MutableLiveData<>())));
                        return;
                    }

                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
                }
            });

        }

        private void setToolbarListener(){
            viewHolderBinding.toolbarLayoutCardViewFriend.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_remove_friend_menu_card_view_friend){
                        String title = getString(R.string.remove_friend);
                        String description = getString(R.string.remove_friend_description);
                        String positiveButtonDescription = getString(R.string.remove);
                        Utilities.Activities.showStandardAlertDialog(adapterCallback.getFragment().requireContext(),
                                title, description, positiveButtonDescription, new Callbacks.StandardAlertDialogCallback() {
                                    @Override
                                    public void onPositiveButtonPress() {
                                        adapterCallback.onRemoveFriend(getSnapshots().getSnapshot(position));
                                    }
                                });
                        return true;
                    }
                    return true;
                }
            });
        }
    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback {
        void onRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot);
    }

}

