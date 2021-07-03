package com.smart_learn.presenter.helpers.adapters;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * Main class for the RecyclerView ViewHolders. Every class from this applications which is used as
 * a ViewHolder must be extended from this class.
 *
 * @param <T> Type of the Entity which this adapter will hold.
 * @param <BI> The generated BindingLayout class which extends ViewDataBinding, and will be used by
 *             holder.
 * */
public abstract class BasicViewHolder<T, BI extends ViewDataBinding> extends RecyclerView.ViewHolder {

    // binding class for holder
    protected final BI viewHolderBinding;
    // here info`s about T entity will be hold in order to be shown by holder
    protected final MutableLiveData<T> liveItemInfo;

    public BasicViewHolder(@NonNull @NotNull BI viewHolderBinding) {
        // this will set itemView in ViewHolder class
        super(viewHolderBinding.getRoot());

        // set binding for holder
        this.viewHolderBinding = viewHolderBinding;

        // avoid a null exception for liveItemInfo.getValue()
        liveItemInfo = new MutableLiveData<>(getEmptyLiveItemInfo());
    }

    /**
     * Used by holder layout to get item info`s.
     *
     * @return A reference to the liveItemInfo wrapped by LiveData<T>.
     * */
    public LiveData<T> getLiveItemInfo(){ return liveItemInfo; }

    /**
     * Used to obtain an empty object of type <T>. Is used to avoid a null exception for
     * liveItemInfo.getValue().
     *
     * @return A reference to an empty object of type <T>.
     * */
    protected abstract T getEmptyLiveItemInfo();

    /**
     * This function is called in onBindViewHolder(...) and here must be specified the actions which
     * must be done related to the current item view on the holder.
     *
     * @param item An object of type T, which contains the information for which the display will
     *             be made.
     * */
    protected abstract void bind(@NonNull @NotNull T item);
}