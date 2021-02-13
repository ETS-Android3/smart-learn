package com.smart_learn.presenter.recycler_view

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import com.smart_learn.R
import com.smart_learn.presenter.recycler_view.adapters.BaseRVAdapterK

/**
 * Here is the action mode which will be started on recycler view lists
 * (for dictionaries and entries)
 */
class ActionModeCallbackK(private var adapterK: BaseRVAdapterK<*>) : ActionMode.Callback {

    private var actionMode: ActionMode? = null

    @MenuRes
    private var menuResId: Int = 0
    private var title: String? = null
    private var subtitle: String? = null

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        actionMode = mode
        mode.menuInflater.inflate(R.menu.action_mode_menu, menu)
        mode.title = title
        mode.subtitle = subtitle
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        mode.finish()
        adapterK.resetActionMode()
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.selectAll -> {
                adapterK.selectAll()
            }
            R.id.deselect -> {
                adapterK.deselectAll()
            }
            R.id.delete -> {
                adapterK.deleteSelected()
            }
        }
        return true
    }

    fun startActionMode(view: View, @MenuRes menuResId: Int, title: String? = null, subtitle: String? = null) {
        if(actionMode == null) {
            this.menuResId = menuResId
            this.title = title + " ${adapterK.getSelectedItemsSize()}"
            this.subtitle = subtitle
            view.startActionMode(this)
        }
    }

    fun refresh(){
        actionMode?.title = "Selected" + " ${adapterK.getSelectedItemsSize()}"
    }

    fun finishActionMode() {
        actionMode?.finish()
    }
}