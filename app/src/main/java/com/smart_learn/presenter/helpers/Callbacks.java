package com.smart_learn.presenter.helpers;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * Main callbacka interface.
 * */
public interface Callbacks {

    interface FragmentGeneralCallback<T> {
        T getFragment();
    }

    /**
     * Used to manage actions when search is made
     * */
    interface SearchActionCallback {
        void onQueryTextChange(String newText);
    }

    /**
     * Used to manage actions in CustomEditableLayout used when a value must be shown as
     * disabled and if necessary this must be shown as enabled (see LessonHomeFragment,
     * LessonHomeWord ...  for details).
     * */
    interface CustomEditableLayoutCallback {

        /**
         * Use this in order to add specific actions when EditText value must be saved before
         * entering in edit mode in order to be able to restore it if necessary.
         * */
        void savePreviousValue();

        /**
         * Use this in order to to add specific actions when you need to restore EditText value to
         * value which was contained before entering in edit mode.
         * */
        void revertToPreviousValue();

        /**
         * Use this in order to add specific actions when you want to check if EditText value
         * follows some constraints.
         * */
        default boolean isCurrentValueOk() { return true; }

        /**
         * Use this in order to add specific actions when you want to save new EditText value.
         * */
        void saveCurrentValue();
    }


    /**
     * Used to manage actions when ActionMode is started
     * */
    interface ActionModeCustomCallback {
        void onCreateActionMode();
        void onDestroyActionMode();
    }


    /**
     * Used to manage filter options
     * */
    interface FragmentFilterOptionsCallback {
        void onAZFilter();
    }


    /**
     * Used to manage positive button press on a simple AlertDialog.
     * */
    interface StandardAlertDialogCallback {
        void onPositiveButtonPress();
    }

    /**
     * Used to manage standard actions on Recycler View adapters.
     * */
    interface StandardAdapterCallback <T> {
        void onSimpleClick(@NonNull @NotNull T item);
        void onLongClick(@NonNull @NotNull T item);
        boolean showCheckedIcon();
        boolean showToolbar();
        void updateSelectedItemsCounter(int value);
        @NonNull
        @NotNull
        BasicFragmentForRecyclerView<?> getFragment();
    }
}
