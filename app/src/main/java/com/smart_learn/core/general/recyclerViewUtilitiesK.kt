package com.smart_learn.core.general

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smart_learn.presenter.recycler_view.ItemTouchCallback
import com.smart_learn.presenter.recycler_view.adapters.BaseRVAdapterK
import com.smart_learn.presenter.recycler_view.adapters.DictionariesRVAdapterK
import com.smart_learn.presenter.recycler_view.adapters.EntrancesRVAdapterK
import com.smart_learn.presenter.view_models.ActivityViewModelUtilitiesK
import com.smart_learn.presenter.view_models.LessonRVViewModelK
import com.smart_learn.presenter.view_models.EntranceRVViewModelK

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
    activityViewModelUtilitiesK: ActivityViewModelUtilitiesK<*, *>,
    adapterType: Int
): BaseRVAdapterK<*>? {

    val baseAdaptorK: BaseRVAdapterK<*>
    // https://www.youtube.com/watch?v=Jo6Mtq7zkkg
    // create recycler view
    recyclerView.apply {
        layoutManager = LinearLayoutManager(activity)
        addItemDecoration(itemDecoration)
        when(adapterType){
            1 -> {
                baseAdaptorK =
                    DictionariesRVAdapterK(activityViewModelUtilitiesK as LessonRVViewModelK)
                adapter = baseAdaptorK
                return baseAdaptorK
            }
            2 -> {
                baseAdaptorK =
                    EntrancesRVAdapterK(activityViewModelUtilitiesK as EntranceRVViewModelK)
                adapter = baseAdaptorK
                return baseAdaptorK
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
    baseAdapterK: BaseRVAdapterK<*>
){

    val itemTouchHandler = object : ItemTouchCallback(baseAdapterK) {

        /** By default only delete and update buttons are added */
        override fun instantiateUnderlayButton(
            context: Context,
            baseAdaptorK: BaseRVAdapterK<*>,
            position: Int
        ): List<UnderlayButton> {
            return  listOf(deleteButton(context,baseAdaptorK,position),
                updateButton(context,baseAdaptorK,position))
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
    baseAdaptorK: BaseRVAdapterK<*>,
    position: Int
) : ItemTouchCallback.UnderlayButton {
    return ItemTouchCallback.UnderlayButton (
        context,
        "Delete",
        14.0f,
        android.R.color.holo_red_light,
        object : ItemTouchCallback.UnderlayButtonClickListener {
            override fun onClick() {
                baseAdaptorK.clickDeleteOnSwipe(position)
            }
        })
}


/**
 * This button will appear after swipe
 * */
fun updateButton(
    context: Context,
    baseAdaptorK: BaseRVAdapterK<*>,
    position: Int
) : ItemTouchCallback.UnderlayButton {

    return ItemTouchCallback.UnderlayButton(
        context,
        "Update",
        14.0f,
        android.R.color.holo_blue_light,
        object : ItemTouchCallback.UnderlayButtonClickListener {
            override fun onClick() {
                baseAdaptorK.clickUpdateOnSwipe(position)
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