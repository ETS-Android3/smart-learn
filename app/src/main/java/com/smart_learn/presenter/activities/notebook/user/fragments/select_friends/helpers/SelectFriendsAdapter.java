package com.smart_learn.presenter.activities.notebook.user.fragments.select_friends.helpers;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.FriendService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.databinding.LayoutCardViewSelectFriendBinding;
import com.smart_learn.presenter.activities.notebook.user.fragments.select_friends.SelectFriendsFragment;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;

public class SelectFriendsAdapter extends BasicFirestoreRecyclerAdapter<FriendDocument, SelectFriendsAdapter.FriendViewHolder> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    protected final SelectFriendsAdapter.Callback<SelectFriendsFragment> adapterCallback;

    @Getter
    private final ArrayList<DocumentSnapshot> selectedFriends;

    // if filter is on
    private boolean isFiltering;
    // current filter value if filter is on
    private String filteringValue;

    public SelectFriendsAdapter(@NonNull @NotNull SelectFriendsAdapter.Callback<SelectFriendsFragment> adapterCallback) {
        super(adapterCallback.getFragment(), getInitialAdapterOptions(adapterCallback.getFragment()),
                INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.adapterCallback = adapterCallback;

        // set initial values
        this.selectedFriends = new ArrayList<>();
        this.adapterCallback.getFragment().showSelectedItems(selectedFriends.size());
        this.isFiltering = false;
        this.filteringValue = "";
    }

    @NonNull
    @NotNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewSelectFriendBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_select_friend, parent, false);
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

    private static FirestoreRecyclerOptions<FriendDocument> getInitialAdapterOptions(@NonNull @NotNull SelectFriendsFragment fragment) {
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
    public void setInitialOption(@NonNull @NotNull SelectFriendsFragment fragment){
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
    public void setFilterOption(@NonNull @NotNull SelectFriendsFragment fragment, @NonNull @NotNull String value){
        filteringValue = value.toLowerCase();
        isFiltering = true;
        Query query = FriendService.getInstance().getQueryForFilter(INITIAL_ADAPTER_CAPACITY, filteringValue);
        FirestoreRecyclerOptions<FriendDocument> newOptions = new FirestoreRecyclerOptions.Builder<FriendDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, FriendDocument.class)
                .build();

        updateOptions(newOptions);
    }

    public final class FriendViewHolder extends BasicViewHolder<FriendDocument, LayoutCardViewSelectFriendBinding> {

        public FriendViewHolder(@NonNull @NotNull LayoutCardViewSelectFriendBinding viewHolderBinding) {
            super(viewHolderBinding);

            viewHolderBinding.toolbarLayoutCardViewSelectFriend.setVisibility(View.GONE);

            setListeners();
        }

        private void setListeners(){
            // simple click action on recycler view item
            viewHolderBinding.cvLayoutCardViewSelectFriend.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    String currentDocId = getSnapshots().getSnapshot(position).getId();

                    // if is checked, remove item and then uncheck it
                    if(viewHolderBinding.cvLayoutCardViewSelectFriend.isChecked()){
                        // mark as unchecked
                        viewHolderBinding.cvLayoutCardViewSelectFriend.setChecked(false);

                        // and remove checked item from list
                        int lim = selectedFriends.size();
                        for(int i = 0; i < lim; i++){
                            if(selectedFriends.get(i).getId().equals(currentDocId)){
                                selectedFriends.remove(i);
                                break;
                            }
                        }

                        adapterCallback.getFragment().showSelectedItems(selectedFriends.size());
                        return;
                    }

                    // if is unchecked the mark as checked
                    viewHolderBinding.cvLayoutCardViewSelectFriend.setChecked(true);

                    // and add item only if does not exists
                    boolean exists = false;
                    for(DocumentSnapshot item : selectedFriends){
                        if(item.getId().equals(currentDocId)){
                            exists = true;
                            break;
                        }
                    }

                    if(!exists){
                        selectedFriends.add(getSnapshots().getSnapshot(position));
                    }

                    adapterCallback.getFragment().showSelectedItems(selectedFriends.size());
                }
            });

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

            // set the existent value
            liveItemInfo.setValue(friendDocument);
        }
    }

    public interface Callback <T> {
        T getFragment();
    }

}