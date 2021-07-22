package com.smart_learn.presenter.helpers.adapters.helpers;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    protected final ArrayList<Pair<DocumentSnapshot, Pair<MaterialCardView, MutableLiveData<Boolean>>>> selectedValues;

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
        this.selectedValues = new ArrayList<>();
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

        // When loading new data, onDataChanged() is called twice. First with getItemCount() == 0, and
        // after that with the correct list values. I presume that first values will be deleted, and
        // after that will be added (if any will exists). So I must make check just after second call.
        //FIXME: this check can produce error. You can`t know every time if secondCall is the secondCall
        // for the loaded data, or some another action comes between.
        if(isLoadingNewData && !secondCall && totalItems == 0){
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
            return true;
        }

        // Here new items were added  because of a user refresh (on loadMoreData(...) was called),
        // then scroll down to the previous bottom, in order to preserve last position, because
        // adapter will go to the top after reloading. After that set new limit also.

        // scroll only if are elements in list
        if(currentLoad > 0){
            // 'currentLoad - 1' will be the last item from previous list
            helper.scrollToPosition(Math.toIntExact(currentLoad - 1));
        }

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

        // if user deleted items without refreshing, just reset currentLoad
        if(!isLoadingNewData){
            currentLoad = totalItems;
            return true;
        }

        // Here items were deleted because of a user refresh (on loadMoreData(...) was called),
        // then scroll down to the previous bottom, in order to preserve last position, because
        // adapter will go to the top after reloading. After that set new limit also.

        // set new list values
        currentLoad = totalItems;

        // scroll only if are elements in list
        if(currentLoad > 0){
            // 'currentLoad - 1' will be the last item from previous list
            helper.scrollToPosition(Math.toIntExact(currentLoad - 1));
        }

        // data was loaded
        isLoadingNewData = false;
        // stop refreshing icon
        helper.stopRefreshing();

        return true;
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
        ArrayList<DocumentSnapshot> tmp = new ArrayList<>();
        for(Pair<DocumentSnapshot, Pair<MaterialCardView, MutableLiveData<Boolean>>> item : selectedValues){
            tmp.add(item.first);
        }
        return tmp;
    }

    public void resetSelectedItems(){
        for(Pair<DocumentSnapshot, Pair<MaterialCardView, MutableLiveData<Boolean>>> item : selectedValues){
            item.second.first.setChecked(false);
            item.second.second.postValue(false);
        }
        selectedValues.clear();
        adapterCallback.updateSelectedItemsCounter(selectedValues.size());
    }

    public void setSelectionModeActive(boolean value) {
        resetSelectedItems();
        isSelectionModeActive = value;
        liveIsSelectionModeActive.setValue(isSelectionModeActive);
    }

    protected void makeStandardSetup(Toolbar toolbar, MaterialCardView cardView){
        if(toolbar != null){
            toolbar.setVisibility(adapterCallback.showToolbar() ? View.VISIBLE : View.GONE);
        }

        if(cardView != null && !adapterCallback.showCheckedIcon()){
            cardView.setCheckedIcon(null);
        }
    }

    protected void markItem(Pair<DocumentSnapshot, Pair<MaterialCardView, MutableLiveData<Boolean>>> item){
        String currentDocId = item.first.getId();
        MaterialCardView cardView = item.second.first;
        MutableLiveData<Boolean> liveSelected = item.second.second;

        // if is checked, remove item and then uncheck it
        if(cardView.isChecked()){
            // mark as unchecked
            cardView.setChecked(false);
            liveSelected.setValue(false);

            // and remove checked item from list
            int lim = selectedValues.size();
            for(int i = 0; i < lim; i++){
                if(selectedValues.get(i).first.getId().equals(currentDocId)){
                    selectedValues.remove(i);
                    break;
                }
            }

            adapterCallback.updateSelectedItemsCounter(selectedValues.size());
            return;
        }

        // if is unchecked the mark as checked
        cardView.setChecked(true);
        liveSelected.setValue(true);

        // and add item only if does not exists
        boolean exists = false;
        for(Pair<DocumentSnapshot, Pair<MaterialCardView, MutableLiveData<Boolean>>> value : selectedValues){
            if(value.first.getId().equals(currentDocId)){
                exists = true;
                break;
            }
        }

        if(!exists){
            selectedValues.add(item);
        }

        adapterCallback.updateSelectedItemsCounter(selectedValues.size());
    }

    protected String getString(int id){
        return adapterCallback.getFragment().getString(id);
    }

    protected void showMessage(int id){
        adapterCallback.getFragment().requireActivity().runOnUiThread(() ->
                GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(), getString(id)));
    }

    public interface Callback extends Callbacks.StandardAdapterCallback <DocumentSnapshot> {

    }

}
