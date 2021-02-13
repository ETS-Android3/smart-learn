package com.smart_learn.core.general

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart_learn.recycler_view.ItemTouchCallback
import com.smart_learn.recycler_view.adapters.BaseRVAdapter
import com.smart_learn.recycler_view.adapters.DictionariesRVAdapter
import com.smart_learn.recycler_view.adapters.EntrancesRVAdapter
import com.smart_learn.core.services.activities.DictionariesRVActivityService
import com.smart_learn.core.services.activities.EntrancesRVActivityService

/** helper for decoration */
class ItemDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = padding
    }
}


/** Init the recycler view layout */
fun initRecyclerView(
    activity: Activity,
    recyclerView: RecyclerView,
    itemDecoration: RecyclerView.ItemDecoration,
    activityServiceUtilities: ActivityServiceUtilities<*,*>,
    adapterType: Int
): BaseRVAdapter<*>? {

    val baseAdaptor: BaseRVAdapter<*>
    // https://www.youtube.com/watch?v=Jo6Mtq7zkkg
    // create recycler view
    recyclerView.apply {
        layoutManager = LinearLayoutManager(activity)
        addItemDecoration(itemDecoration)
        when(adapterType){
            1 -> {
                baseAdaptor =
                    DictionariesRVAdapter(activityServiceUtilities as DictionariesRVActivityService)
                adapter = baseAdaptor
                return baseAdaptor
            }
            2 -> {
                baseAdaptor =
                    EntrancesRVAdapter(activityServiceUtilities as EntrancesRVActivityService)
                adapter = baseAdaptor
                return baseAdaptor
            }
            else -> {
                Log.e(UNEXPECTED_ERROR,"Type unknown for recycler view adapter in initRecyclerView")
            }

        }
    }

    return null
}


/** Used to enable some gesture on item touch */
fun initItemTouchCallback(
    recyclerView: RecyclerView,
    baseAdapter: BaseRVAdapter<*>
){

    val itemTouchHandler = object : ItemTouchCallback(baseAdapter) {

        /** By default only delete and update buttons are added */
        override fun instantiateUnderlayButton(
            context: Context,
            baseAdaptor: BaseRVAdapter<*>,
            position: Int
        ): List<UnderlayButton> {
            return  listOf(deleteButton(context,baseAdaptor,position),
                updateButton(context,baseAdaptor,position))
        }
    }

    // attach itemTouchHandler to recycler view
    val itemTouchHelper = ItemTouchHelper(itemTouchHandler)
    itemTouchHelper.attachToRecyclerView(recyclerView)
}


/**
 * This button will appear after swipe
 * */
fun deleteButton(
    context: Context,
    baseAdaptor: BaseRVAdapter<*>,
    position: Int
) : ItemTouchCallback.UnderlayButton {
    return ItemTouchCallback.UnderlayButton (
        context,
        "Delete",
        14.0f,
        android.R.color.holo_red_light,
        object : ItemTouchCallback.UnderlayButtonClickListener {
            override fun onClick() {
                baseAdaptor.clickDeleteOnSwipe(position)
            }
        })
}


/**
 * This button will appear after swipe
 * */
fun updateButton(
    context: Context,
    baseAdaptor: BaseRVAdapter<*>,
    position: Int
) : ItemTouchCallback.UnderlayButton {

    return ItemTouchCallback.UnderlayButton(
        context,
        "Update",
        14.0f,
        android.R.color.holo_blue_light,
        object : ItemTouchCallback.UnderlayButtonClickListener {
            override fun onClick() {
                baseAdaptor.clickUpdateOnSwipe(position)
            }
        })
}


/**
 * https://stackoverflow.com/questions/62189457/get-indexes-of-substrings-contained-in-a-string-in-kotlin-way
 * */
fun String?.indexesOf(substring: String, ignoreCase: Boolean = true): List<IntRange> {
    return this?.let {
        val regex = if (ignoreCase) Regex(substring, RegexOption.IGNORE_CASE) else Regex(substring)
        regex.findAll(this).map {
            it.range }.toList()
    } ?: emptyList()
}