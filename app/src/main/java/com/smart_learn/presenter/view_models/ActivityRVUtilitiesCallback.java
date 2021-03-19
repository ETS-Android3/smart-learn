package com.smart_learn.presenter.view_models;

import android.view.ActionMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.exceptions.TODO;
import com.smart_learn.core.utilities.Logs;

import java.util.List;

public interface ActivityRVUtilitiesCallback<T> extends ActivityGeneralUtilitiesCallback {

    /*- -------------------------------------------------------------------------------------------
     *                          Usually used by Recycler View Adapter
     * ------------------------------------------------------------------------------------------ */


    /** Override in order to return a List<T> of db items which is not wrapped by LiveData. */
    @NonNull
    default List<T> getAllItemsFromDatabase() {
        throw new TODO(Logs.NOT_IMPLEMENTED);
    }

    /** Override in order to add specific behaviour when an item is updated. */
    default void update(T item) { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific behaviour when an item is deleted. */
    default void delete(T item) { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific behaviour when action mode is started. */
    default void startActionMode() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific behaviour when action mode is destroyed. */
    default void destroyActionMode() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to have a getter for ActionMode.Callback. */
    @Nullable
    default ActionMode.Callback getActionModeCallback() { throw new TODO(Logs.NOT_IMPLEMENTED); }



    /*- -------------------------------------------------------------------------------------------
     *                  Usually used by Action Mode when multi selection is enabled
     * ------------------------------------------------------------------------------------------ */
    /** Override in order to add specific behaviour when all items must be marked for a specific action. */
    default void selectAll() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific behaviour when all items must be unmarked. */
    default void deselectAll() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific behaviour when all items which are marked for deletion
     * in database (isSelected is true) must be deleted. */
    default void deleteSelectedItems() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific behaviour when all items from database must be marked for deletion
     * or not.
     *
     * @param isSelected true if all items must be marked for deletion, false otherwise */
    default void updateSelectAll(boolean isSelected) { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to get number of selected items for deletion. */
    @NonNull
    default LiveData<Integer> getLiveSelectedItemsCount() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to get number of all items from db which are valid for adapter list. */
    @NonNull
    default LiveData<Integer> getLiveItemsNumber() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific action if data set was changed. */
    default void notifyToChange() { throw new TODO(Logs.NOT_IMPLEMENTED); }


    /*- -------------------------------------------------------------------------------------------
     *                   Usually used by Item Touch Helper
     * ------------------------------------------------------------------------------------------ */

    /** Override in order to add specific behaviour when swipe for update is active. */
    default void onSwipeForUpdate(T item) { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to add specific behaviour when swipe for delete is active. */
    default void onSwipeForDelete(T item) { throw new TODO(Logs.NOT_IMPLEMENTED); }
}
