package com.smart_learn.presenter.activities.lesson.recycler_view;


import android.annotation.SuppressLint;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.smart_learn.R;
import com.smart_learn.presenter.view_models.ActivityRVUtilitiesCallback;

/**
 * Here is the action mode which will be started on recycler view lists
 * (for lessons and entries)
 *
 * https://www.youtube.com/watch?v=XhEhfmBJlLY
 */
public class ActionModeRVCallback<T> implements ActionMode.Callback {

    private final int menuResId;
    private final String title;
    private final String subtitle;

    private ActionMode actionMode;
    private final ActivityRVUtilitiesCallback<T> activityCallback;
    // just a counter to know how many items are selected in the adapter list
    private int nrItemsSelected;
    // just a counter to know how many items are in db (all items from db are or can be in the
    // adapter list)
    private int nrItems;

    public ActionModeRVCallback(ActivityRVUtilitiesCallback<T> activityCallback, LifecycleOwner lifecycleOwner,
                                View view, int menuResId, String title, String subtitle) {

        this.activityCallback = activityCallback;

        // set observer for live data
        this.activityCallback.getLiveSelectedItemsCount().observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                nrItemsSelected = integer;
                if(actionMode != null){
                    actionMode.setTitle("Selected: " + nrItemsSelected);
                }
            }
        });

        this.activityCallback.getLiveItemsNumber().observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                nrItems = integer;
            }
        });

        this.menuResId = menuResId;
        this.title = title;
        this.subtitle = subtitle;

        view.startActionMode(this);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
        mode.getMenuInflater().inflate(menuResId, menu);
        mode.setTitle(title);
        mode.setSubtitle(subtitle);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.selectAll:
                        if(nrItemsSelected < nrItems){
                            activityCallback.selectAll();
                        }
                        return true;
            case R.id.deselect:
                        if(nrItemsSelected > 0){
                            activityCallback.deselectAll();
                        }
                        return true;
            case R.id.delete:
                        if(nrItemsSelected <= 0) {
                            activityCallback.setToastMessage("No item selected");
                            return true;
                        }
                        activityCallback.deleteSelectedItems();
                        return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mode.finish();
        actionMode = null;
        activityCallback.destroyActionMode();

        activityCallback.deselectAll();
    }
}
