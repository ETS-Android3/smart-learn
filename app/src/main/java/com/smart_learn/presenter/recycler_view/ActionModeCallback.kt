package com.smart_learn.presenter.recycler_view

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import com.smart_learn.R
import com.smart_learn.presenter.recycler_view.adapters.BaseRVAdapter

/**
 * Here is the action mode which will be started on recycler view lists
 * (for dictionaries and entries)
 */
class ActionModeCallback(private var adapter: BaseRVAdapter<*>) : ActionMode.Callback {

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
        adapter.resetActionMode()
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.selectAll -> {
                adapter.selectAll()
            }
            R.id.deselect -> {
                adapter.deselectAll()
            }
            R.id.delete -> {
                adapter.deleteSelected()
            }
        }
        return true
    }

    fun startActionMode(view: View, @MenuRes menuResId: Int, title: String? = null, subtitle: String? = null) {
        if(actionMode == null) {
            this.menuResId = menuResId
            this.title = title + " ${adapter.getSelectedItemsSize()}"
            this.subtitle = subtitle
            view.startActionMode(this)
        }
    }

    fun refresh(){
        actionMode?.title = "Selected" + " ${adapter.getSelectedItemsSize()}"
    }

    fun finishActionMode() {
        actionMode?.finish()
    }
}