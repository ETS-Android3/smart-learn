package com.smart_learn.presenter.helpers.adapters.helpers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.presenter.helpers.PresenterCallbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.PresenterUtilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import lombok.Getter;
import timber.log.Timber;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * The main Adapter class from which all adapters of the application which extends from
 * FirestoreRecyclerAdapter <T,VH>, must be extended.
 *
 * @param <T>  Type of the Entity which this adapter will hold.
 * @param <VH> A ViewHolder class that extends BasicViewHolder<T,?> that will be used by the
 *             adapter to show Entity details.
 * */
public abstract class BasicFirestoreRecyclerAdapter <T, VH extends BasicViewHolder<T, ?>, CBK extends BasicFirestoreRecyclerAdapter.Callback>
        extends FirestoreRecyclerAdapter<T, VH> implements PresenterHelpers.AdapterHelper {

    // main callback for handling different operations
    @NonNull
    @NotNull
    protected final CBK adapterCallback;

    // used to add values when selection mode is active
    @NonNull
    @NotNull
    protected final HashMap<String, DocumentSnapshot> selectedValues;

    // live value will be used for view holder binding in order to show/hide views
    @Getter
    private final MutableLiveData<Boolean> liveIsSelectionModeActive;
    // normal value will be used for fast access at value
    @Getter
    private boolean isSelectionModeActive;

    // used for filtering purpose
    protected boolean isFiltering;
    protected String filteringValue;

    // helper for 'currentLoad'
    private static final int UNSET = -1;

    // How many items will be loaded in the adapter when adapter is initialized.
    protected final long initialAdapterCapacity;

    // How many items will be added after user scrolls down and list is finished.
    // This will be used by subclasses to paginate FirestoreRecyclerAdapter, by setting new limit
    // for query`s. Check implementations for loadMoreData(...) from PresenterHelpers.AdapterHelper
    // in subclasses.
    // https://stackoverflow.com/questions/49680469/firestore-pagination-with-firestorerecycleradapter-android
    // https://stackoverflow.com/questions/50741958/how-to-paginate-firestore-with-android
    protected final long loadingStep;

    // Check if adapter loads data (avoid to make multiple loads at the same time).
    protected boolean isLoadingNewData;

    // How many items are in the current list and also after list is changed this will give the
    // number of the previous list,
    protected long currentLoad;

    // When loading new data, onDataChanged() is called twice. First with getItemCount() == 0, and
    // after that with the correct list values. I presume that first values will be deleted, and after
    // that will be added (if any will exists). So I must make check just after second call.
    private boolean secondCall;

    // used manage different actions
    private final PresenterHelpers.FragmentRecyclerViewHelper helper;


    /**
     * @param options Options needed by the FirestoreRecyclerAdapter in order to load data.
     * @param initialAdapterCapacity How many items will be loaded in the adapter when adapter is
     *                               initialized.
     * @param loadingStep How many items will be added after user scrolls down and list is finished.
     * */
    public BasicFirestoreRecyclerAdapter(@NonNull @NotNull CBK adapterCallback,
                                         @NonNull @NotNull FirestoreRecyclerOptions<T> options,
                                         long initialAdapterCapacity, long loadingStep) {
        super(options);
        this.adapterCallback = adapterCallback;
        this.helper = adapterCallback.getFragment();
        this.initialAdapterCapacity = initialAdapterCapacity;
        this.loadingStep = loadingStep;

        // specific initial setup
        this.isFiltering = false;
        this.filteringValue = "";
        this.isSelectionModeActive = false;
        this.liveIsSelectionModeActive = new MutableLiveData<>(false);
        this.selectedValues = new HashMap<>();
        this.isLoadingNewData = false;
        this.secondCall = false;
        this.currentLoad = UNSET;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull VH holder, int position, @NonNull @NotNull T model) {
        if(position == NO_POSITION){
            Timber.w("position is set to NO_POSITION");
            return;
        }

        holder.bind(model, position);
    }

    @Override
    public void onDataChanged() {
        // how many items are in the current list
        int totalItems = getItemCount();

        // When loading new data or filtering, onDataChanged() is called twice. First with getItemCount() == 0,
        // and after that with the correct list values. I presume that first values will be deleted, and
        // after that will be added (if any will exists). So I must make check just after second call.
        //FIXME: this check can produce error. You can`t know every time if secondCall is the secondCall
        // for the loaded data, or some another action comes between.
        if((isFiltering || isLoadingNewData) && !secondCall && totalItems == 0){
            // next time will be second call
            secondCall = true;
            return;
        }
        secondCall = false;

        // Leave this line as it is. I need to execute these functions, and if one returns 'true' then
        // others does not to be executed. I need to call showOrHideEmptyLabel(...) after every
        // execution, but to simplify I use this syntax.
        boolean value = isInitialSetup(totalItems) || wereItemsAdded(totalItems) || wereItemsDeleted(totalItems);
        helper.showOrHideEmptyLabel(totalItems < 1);
    }

    private boolean isInitialSetup(int totalItems){
        // this will be executed only at the first loading
        if(currentLoad == UNSET){
            currentLoad = totalItems;
            helper.stopRefreshing();
            return true;
        }
        return false;
    }

    private boolean wereItemsAdded(int totalItems){
        // '<=' is used for covering the final case when no items were added after user refresh
        if(!(currentLoad <= totalItems)) {
            return false;
        }

        // here new items were added

        // is user added items without refreshing, just reset currentLoad
        if(!isLoadingNewData){
            currentLoad = totalItems;
            // also if scroll to last position is valid then scroll to last position
            if(adapterCallback.scrollDirectlyToLastItem()){
                scrollToNewPosition(totalItems, currentLoad);
            }
            return true;
        }

        // Here new items were added  because of a user refresh (on loadMoreData(...) was called),
        // then scroll down to the previous bottom, in order to preserve last position, because
        // adapter will go to the top after reloading. After that set new limit also.

        // reset recycler view to new position
        scrollToNewPosition(totalItems, currentLoad);

        // set new list values
        currentLoad = totalItems;
        // data was loaded
        isLoadingNewData = false;
        // stop refreshing icon
        helper.stopRefreshing();

        return true;
    }

    private boolean wereItemsDeleted(int totalItems){
        if(!(currentLoad > totalItems)) {
            return false;
        }

        // here new items were deleted

        // If items were deleted update selectedValues because is possible that a value which is
        // marked for deletion to already deleted.
        // FIXME: For filtering, this update is ignored because while filtering list size can alternate
        //  between sizes, so selection will not persist, because when current size is smaller then
        //  previous size, app will presume that items were deleted (but here items are only filtered).
        //  Still, problem can appear if, while filtering, a value from database is deleted in real time,
        //  because value it will disappear in real time (this is an expected behaviour), but if that
        //  value was selected by user, will not be eliminated from selected values, so selected values
        //  will not be consistent.
        if(isSelectionModeActive && !isFiltering){
            ArrayList<DocumentSnapshot> tmp = new ArrayList<>();
            // totalItems is same as getSnapshots().size()
            for(int i = 0; i < totalItems; i++){
                tmp.add(getSnapshots().getSnapshot(i));
            }
            updateSelectedValues(tmp);
        }

        // if user deleted items without refreshing, just reset currentLoad
        if(!isLoadingNewData){
            currentLoad = totalItems;
            // also if scroll to last position is valid then scroll to last position
            if(adapterCallback.scrollDirectlyToLastItem()){
                scrollToNewPosition(totalItems, currentLoad);
            }
            return true;
        }

        // Here items were deleted because of a user refresh (on loadMoreData(...) was called),
        // then scroll down to the previous bottom, in order to preserve last position, because
        // adapter will go to the top after reloading. After that set new limit also.

        // set new list values
        currentLoad = totalItems;

        // reset recycler view to new position
        scrollToNewPosition(totalItems, currentLoad);

        // data was loaded
        isLoadingNewData = false;
        // stop refreshing icon
        helper.stopRefreshing();

        return true;
    }

    private void scrollToNewPosition(int totalItems, long currentLoad){
        // if recycler view must scroll directly to last item set last position including all items
        if(adapterCallback.scrollDirectlyToLastItem()){
            // scroll only if are elements in list
            if(totalItems > 0){
                // 'totalItems - 1' will be the last item from entire list
                helper.scrollToPosition(totalItems - 1);
                return;
            }
            return;
        }

        // scroll to last previous position (scroll only if are elements in list)
        if(currentLoad > 0){
            // 'currentLoad - 1' will be the last item from previous list
            helper.scrollToPosition(Math.toIntExact(currentLoad - 1));
        }
    }

    /**
     * Used to load data in adapter, if query is changed.
     *
     * @param query What query must be loaded by FirestoreRecyclerAdapter.
     * @param modelClass EntityClassName.class
     * @param lifecycleOwner The lifecycle owner for the FirestoreRecyclerOptions<T> which will be
     *                       build.
     * */
    protected void loadData(Query query, Class <T> modelClass, LifecycleOwner lifecycleOwner) {

        helper.startRefreshing();

        // load more data only if is not another loading process in progress
        if(isLoadingNewData){
            helper.stopRefreshing();
            return;
        }

        if(query == null){
            Timber.w("query is null");
            helper.stopRefreshing();
            return;
        }

        if(modelClass == null){
            Timber.w("modelClass is null");
            helper.stopRefreshing();
            return;
        }

        if(lifecycleOwner == null){
            Timber.w("lifecycleOwner is null");
            helper.stopRefreshing();
            return;
        }

        isLoadingNewData = true;

        FirestoreRecyclerOptions<T> newOptions = new FirestoreRecyclerOptions.Builder<T>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, modelClass)
                .build();

        updateOptions(newOptions);

        // helper.stopRefreshing() will be called on onDataChanged() after load is complete

    }

    @NonNull
    @NotNull
    public ArrayList<DocumentSnapshot> getSelectedValues(){
        return new ArrayList<>(selectedValues.values());
    }

    protected void updateSelectedValues(ArrayList<DocumentSnapshot> newSnapshotList){
        if(newSnapshotList == null || newSnapshotList.isEmpty()){
            resetSelectedItems();
            return;
        }

        ArrayList<DocumentSnapshot> removedList = new ArrayList<>();
        for(DocumentSnapshot selectedItem : selectedValues.values()){
            boolean exist = false;
            for(DocumentSnapshot snapshot : newSnapshotList){
                if(selectedItem.getId().equals(snapshot.getId())){
                    exist = true;
                    break;
                }
            }

            if(!exist){
                // Here item exist in selectedValues but was removed from newSnapshotList ==>
                // must be removed from selectedValues.
                removedList.add(selectedItem);
            }
        }

        for(DocumentSnapshot item : removedList){
            selectedValues.remove(item.getId());
        }
        adapterCallback.updateSelectedItemsCounter(selectedValues.size());
    }

    protected boolean isSelected(DocumentSnapshot item){
        if(item == null){
            return false;
        }
        return selectedValues.containsKey(item.getId());
    }

    public void resetSelectedItems(){
        HashSet<Integer> selectedPositions = new HashSet<>();
        if(!selectedValues.isEmpty() && !isSelectionModeActive){
            // If selected values existed and selected mode is disabled, then extract updated positions.
            int lim = getSnapshots().size();
            for(int i = 0; i < lim; i++){
                if(isSelected(getSnapshots().getSnapshot(i))){
                    selectedPositions.add(i);
                }
            }
        }

        selectedValues.clear();
        adapterCallback.updateSelectedItemsCounter(selectedValues.size());

        if(!selectedPositions.isEmpty() && !isSelectionModeActive){
            // Notify all previous selected positions which will call onBindViewHolder() for every position.
            // This must be after selectedValues.clear() because in binding call, a check for isSelected(...)
            // will be made. And if selectedValues is not clear, then execution will not be correct.
            for(Integer position : selectedPositions){
                notifyItemChanged(position);
            }
        }
    }

    public void setSelectionModeActive(boolean value) {
        isSelectionModeActive = value;
        liveIsSelectionModeActive.setValue(isSelectionModeActive);
        // reset must be done here because value from isSelectionModeActive will be used.
        resetSelectedItems();
    }

    protected void makeStandardSetup(Toolbar toolbar, MaterialCardView cardView){
        if(toolbar != null){
            toolbar.setVisibility(adapterCallback.showToolbar() ? View.VISIBLE : View.GONE);
        }

        if(cardView != null && !adapterCallback.showCheckedIcon()){
            cardView.setCheckedIcon(null);
        }
    }

    protected void markItem(int position, DocumentSnapshot item){

        if(isSelected(item)){
            selectedValues.remove(item.getId());
        }
        else {
            selectedValues.put(item.getId(), item);
        }

        adapterCallback.updateSelectedItemsCounter(selectedValues.size());
        // this is necessary in order to call bind method
        notifyItemChanged(position);
    }

    protected String getString(int id){
        return adapterCallback.getFragment().getString(id);
    }

    protected void showMessage(int id){
        adapterCallback.getFragment().requireActivity().runOnUiThread(() ->
                PresenterUtilities.General.showShortToastMessage(adapterCallback.getFragment().requireContext(), getString(id)));
    }

    protected void showMessage(String message){
        adapterCallback.getFragment().requireActivity().runOnUiThread(() ->
                PresenterUtilities.General.showShortToastMessage(adapterCallback.getFragment().requireContext(), message));
    }

    public interface Callback extends PresenterCallbacks.StandardAdapterCallback <DocumentSnapshot> {

    }

}
