package com.smart_learn.presenter.helpers.adapters.helpers;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.material.card.MaterialCardView;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;

/**
 * The main Adapter class from which all adapters of the application which extends from
 * ListAdapter <T,VH>, must be extended.
 *
 * @param <T>  Type of the Entity which this adapter will hold which will extend SelectionHelper
 *             and DiffUtilCallbackHelper<T> .
 * @param <VH> A ViewHolder class that extends BasicViewHolder<T,?> that will be used by the
 *             adapter to show Entity details.
 * @param <CBK> Adapter callback which will extend BasicListAdapter.Callback<T> .
 *
 *
 *  For ListAdapter https://www.youtube.com/watch?v=xPPMygGxiEo
 * */
public abstract class BasicListAdapter <T extends PresenterHelpers.SelectionHelper & PresenterHelpers.DiffUtilCallbackHelper<T>,
        VH extends BasicViewHolder<T, ?>, CBK extends BasicListAdapter.Callback<T>>
        extends ListAdapter<T, VH> implements PresenterHelpers.AdapterHelper {

    // main callback for handling different operations
    @NonNull
    @NotNull
    protected final CBK adapterCallback;

    // used to add values when selection mode is active
    @NonNull
    @NotNull
    protected final HashMap<Integer, T> selectedValues;

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
        this.selectedValues = new HashMap<>();
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

    public ArrayList<T> getSelectedValues(){
        return new ArrayList<>(selectedValues.values());
    }

    protected boolean isSelected(T item){
        if(item == null){
            return false;
        }
        return selectedValues.containsKey(item.getId());
    }

    public void resetSelectedItems(){
        HashSet<Integer> selectedPositions = new HashSet<>();
        if(!selectedValues.isEmpty() && !isSelectionModeActive){
            // If selected values existed and selected mode is disabled, then extract updated positions.
            int lim = getItemCount();
            for(int i = 0; i < lim; i++){
                if(isSelected(getItem(i))){
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

    protected void markItem(int position, T item){

        if(isSelected(item)){
            // if is selected, remove it from selected list
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
                GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(), getString(id)));
    }


    public interface Callback <K> extends Callbacks.StandardAdapterCallback <K> {

    }
}
