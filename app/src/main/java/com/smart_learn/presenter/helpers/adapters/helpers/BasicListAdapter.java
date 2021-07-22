package com.smart_learn.presenter.helpers.adapters.helpers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * The main Adapter class from which all adapters of the application which extends from
 * ListAdapter <T,VH>, must be extended.
 *
 * @param <T>  Type of the Entity which this adapter will hold which will extend
 *             PresenterHelpers.DiffUtilCallbackHelper<T> .
 * @param <VH> A ViewHolder class that extends BasicViewHolder<T,?> that will be used by the
 *             adapter to show Entity details.
 * @param <CBK> Adapter callback which will extend BasicListAdapter.Callback<T> .
 *
 *
 *  For ListAdapter https://www.youtube.com/watch?v=xPPMygGxiEo
 * */
public abstract class BasicListAdapter <T extends PresenterHelpers.DiffUtilCallbackHelper<T>, VH extends BasicViewHolder<T, ?>, CBK extends BasicListAdapter.Callback<T>>
        extends ListAdapter<T, VH> implements PresenterHelpers.AdapterHelper {

    // main callback for handling different operations
    @NonNull
    @NotNull
    protected final CBK adapterCallback;

    // used to add values when selection mode is active
    @NonNull
    @NotNull
    protected final ArrayList<Pair<T, Pair<MaterialCardView, MutableLiveData<Boolean>>>> selectedValues;

    // live value will be used for view holder binding in order to show/hide views
    @Getter
    private final MutableLiveData<Boolean> liveIsSelectionModeActive;
    // normal value will be used for fast access at value
    @Getter
    private boolean isSelectionModeActive;

    // used for filtering purpose
    protected boolean isFiltering;
    protected String filteringValue;

    protected BasicListAdapter(@NonNull @NotNull CBK adapterCallback) {
        super(new DiffUtil.ItemCallback<T>(){
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.areItemsTheSame(newItem);
            }
            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return oldItem.areContentsTheSame(newItem);
            }
        });

        this.adapterCallback = adapterCallback;

        // specific initial setup
        this.selectedValues = new ArrayList<>();
        this.isSelectionModeActive = false;
        this.liveIsSelectionModeActive = new MutableLiveData<>(false);
        this.isFiltering = false;
        this.filteringValue = "";
    }

    public void setItems(List<T> items) {
        submitList(items);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
            return;
        }

        T item = getItem(position);
        if(!CoreUtilities.General.isItemNotNull(item)){
            return;
        }

        holder.bind(item, position);
    }

    @Override
    public void loadMoreData() {
        // no action needed here
    }

    @NonNull
    @NotNull
    public ArrayList<T> getSelectedValues(){
        ArrayList<T> tmp = new ArrayList<>();
        for(Pair<T, Pair<MaterialCardView, MutableLiveData<Boolean>>> item : selectedValues){
            tmp.add(item.first);
        }
        return tmp;
    }

    public void resetSelectedItems(){
        for(Pair<T, Pair<MaterialCardView, MutableLiveData<Boolean>>> item : selectedValues){
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

    protected void markItem(Pair<T, Pair<MaterialCardView, MutableLiveData<Boolean>>> item){
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
                if(selectedValues.get(i).first.areItemsTheSame(item.first)){
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
        for(Pair<T, Pair<MaterialCardView, MutableLiveData<Boolean>>> value : selectedValues){
            if(value.first.areItemsTheSame(item.first)){
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


    public interface Callback <K> extends Callbacks.StandardAdapterCallback <K> {

    }
}
