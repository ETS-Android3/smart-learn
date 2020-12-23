package com.smart_learn.recycler_view.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.smart_learn.general.ActivityServiceUtilities
import com.smart_learn.general.DEBUG_MODE
import com.smart_learn.recycler_view.ActionModeCallback

abstract class BaseRVAdapter <T> (
    private val activityServiceUtilities: ActivityServiceUtilities<*, *>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var items: MutableList<T> = ArrayList()
    protected var allItems: MutableList<T> = ArrayList()
    protected var selectedItems: MutableSet<Int> = mutableSetOf()
    protected var actionModeCallback: ActionModeCallback? = null
    protected var activity: Activity = activityServiceUtilities.getActivity()


    /** override from RecyclerView.Adapter<RecyclerView.ViewHolder> */
    override fun getItemCount(): Int {
        return items.size
    }




    /** My abstract functions */
    abstract fun getContext(): Context
    abstract fun getRecyclerView(): RecyclerView
    abstract fun getAdapter(): BaseRVAdapter<T>
    abstract fun selectItem(item: T)
    abstract fun deselectItem(item: T)
    abstract fun deleteFromDatabase(item: T)
    abstract fun clickUpdateOnSwipe(position: Int)

    /** This is a helper to show specific element when recycler view list is empty and other
     *  elements when recycler view list is NOT empty */
    abstract fun checkEmptyState()




    /** My default implemented functions */

    /** Load initial data in recycler view */
    fun submitList(itemList: MutableList<T>){
        items = itemList
        allItems.addAll(itemList)
        checkEmptyState()
    }


    /** Insert one item in recycler view, notify recycler view
     * and check to see if list is empty */
    fun insertItem(item: T){
        items.add(item)
        notifyItemInserted(items.indexOf(item))
        checkEmptyState()
    }


    /** Update one item  from recycler view, notify recycler view
     * and check to see if list is empty */
    fun updateItem(position: Int, newItem: T){
        items[position] = newItem
        notifyItemChanged(position)
        checkEmptyState()
    }


    /** Update multiple items from recycler view, notify recycler view
     *  and check to see if list is empty
     *
     * Old item list is removed and newList is added in recycler view
     * */
    private fun updateItems(newList: MutableList<T>){
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
        checkEmptyState()
    }


    /** Remove all items from selectedItems array:
     *  - remove from recycler view
     *  - remove from database
     *  - notify recycler view
     *  - check to see if list is empty
     *  - clear the selectedElements array
     *  - checkEmptyState() is made in updateItems
     * */
    private fun removeSelectedItems(){
        val tmpList: MutableList<T> = ArrayList()

        // create a new list which will contains all items without the selected ones
        for((index, item) in items.withIndex()){
            if (index in selectedItems){
                continue
            }
            tmpList.add(item)
        }

        // delete from database
        selectedItems.forEach{
            deleteFromDatabase(items[it])
        }

        // delete from ram memory
        clearSelectedElements(true)
        updateItems(tmpList)
    }


    /** Clear selectedElements array and reset isSelected value for elements to false */
    fun clearSelectedElements(deletedItems: Boolean){
        if(!deletedItems && items.isNotEmpty()) {
            selectedItems.forEach {
                deselectItem(items[it])
            }
        }

        selectedItems.clear()
    }






    // TODO: check how to implement this
    //  Link with function swipe from itemTouchCallback
    fun onItemSwiped(position: Int) {
        //        items.removeAt(position)
        //        notifyItemRemoved(position)
        //        checkEmptyState()
    }

    fun getSelectedItemsSize(): Int {
        return selectedItems.size
    }


    fun resetActionMode() {
        actionModeCallback = null
        deselectAll()

        notifyDataSetChanged()
        checkEmptyState()
    }

    fun selectAll() {
        items.forEach {
            selectItem(it)
            selectedItems.add(items.indexOf(it))
        }
        notifyDataSetChanged()
        checkEmptyState()
        actionModeCallback?.refresh()
    }

    fun deselectAll() {
        clearSelectedElements(false)
        notifyDataSetChanged()
        checkEmptyState()
        actionModeCallback?.refresh()
    }

    fun deleteSelected() {
        if(selectedItems.isNotEmpty()) {
            if (DEBUG_MODE) {
                Log.d(
                    "[DictionariesRVAdapter]",
                    "Deleted ${selectedItems.size} elements:\n"
                )
                selectedItems.forEach {
                    println(items[it].toString())
                }
            }
            removeSelectedItems()
        }
        else{
            Toast.makeText(activity,"No item selected", Toast.LENGTH_SHORT).show()
        }
        actionModeCallback?.refresh()
    }


    fun clickDeleteOnSwipe(position: Int){
        selectedItems.clear()
        selectedItems.add(position)
        removeSelectedItems()
    }

}