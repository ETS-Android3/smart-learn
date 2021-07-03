package com.smart_learn.presenter.helpers;

/**
 * Main helper class for presenter layer.
 * */
public interface PresenterHelpers {

    /**
     * Helper methods for fragments which have a recycler view layout.
     * */
    interface FragmentRecyclerViewHelper {

        /**
         * Used to start specific actions when recycler view data is loading.
         *
         * For example can be shown a refresh icon using SwipeRefreshLayout.
         * */
        void startRefreshing();

        /**
         * Used to stop specific actions after recycler view data was loaded.
         *
         * For example you can hide a refresh icon using SwipeRefreshLayout, if icon was shown when
         * data started to load.
         * */
        void stopRefreshing();

        /**
         * Used to show a description when are no items in the recycler view list.
         *
         * @param value true to show description (View.VISIBLE will be set) or false to hide it
         *              (View.GONE will be set).
         * */
        void showOrHideEmptyLabel(boolean value);

        /**
         * Used to scroll to a specific position from the recycler view list.
         *
         * @param position Position where scroll must go.
         * */
        void scrollToPosition(int position);
    }


    /**
     * Helper methods for the Adapters extended from RecyclerView.Adapter<>.
     * */
    interface AdapterHelper {

        /**
         * Use it to specific what actions should be done if user reaches the end of the recycler view
         * list and more data is necessary.
         * */
        void loadMoreData();
    }
}
