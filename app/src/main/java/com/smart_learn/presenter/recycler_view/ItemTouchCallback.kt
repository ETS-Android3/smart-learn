package com.smart_learn.presenter.recycler_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.smart_learn.presenter.recycler_view.adapters.BaseRVAdapter
import java.util.*

/**
 * This class handles support gestures from recycler view
 *  FIXME: canvas not cleared
 * */
abstract class ItemTouchCallback(private val activityAdapterUtilities: BaseRVAdapter<*>) :
    ItemTouchHelper.Callback() {

    private val recyclerView: RecyclerView = activityAdapterUtilities.getRecyclerView()

    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    /** Choose what movements you want for ItemTouchHelper
     *  ( by default only swipe LEFT movement is supported )
     *
     * Override function in the new created object to add new proprieties
     * */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // ItemTouchHelper.START is equivalent to ItemTouchHelper.LEFT
        val swipeFlags = ItemTouchHelper.START

        // By default I don' want drag flags (0 means drag is disabled)
        return makeMovementFlags(0,swipeFlags)
    }


    /** This must be used when you want to drag items in list for changing the position
     * (by default drag is not supported)
     *
     * Override function in the new created object  to add new proprieties
     * */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

//    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//        super.clearView(recyclerView, viewHolder)
//        viewHolder.itemView.setBackgroundColor(
//            ContextCompat.getColor(viewHolder.itemView.context, R.color.white)
//        )
//    }

    /*
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            viewHolder?.itemView?.setBackgroundColor(
                ContextCompat.getColor(viewHolder.itemView.context, R.color.colorPrimary)
            )
        }
    }
     */

//    override fun onChildDraw(
//        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
//        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
//    ) {
//
//        val itemView = viewHolder.itemView
//        val itemHeight = itemView.bottom - itemView.top
//        val isCanceled = dX == 0f && !isCurrentlyActive
//
//        if (isCanceled) {
//            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//            return
//        }
//
//        // Draw the red delete background
//        background.color = backgroundColor
//        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
//        background.draw(c)
//
//        // Calculate position of delete icon
//        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
//        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
//        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
//        val deleteIconRight = itemView.right - deleteIconMargin
//        val deleteIconBottom = deleteIconTop + intrinsicHeight
//
//        // Draw the delete icon
//        deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
//        deleteIcon?.draw(c)
//
//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }


    /**
     * TODO: Understand better how the below code works
     *   - https://stackoverflow.com/questions/44965278/recyclerview-itemtouchhelper-buttons-on-swipe
     *   - code was taken from here https://github.com/ntnhon/RecyclerViewRowOptionsDemo
     * */
    private var swipePosition: Int  = -3
    private val buttonsBuffer: MutableMap<Int, List<UnderlayButton>> = mutableMapOf()


    private val recoverQueue = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            if (contains(element)) return false
            return super.add(element)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { _, event ->
        Log.e("swipe","swipe position = $swipePosition")
        if (swipePosition < 0) return@OnTouchListener false
        buttonsBuffer[swipePosition]?.forEach {
            it.handle(event)
        }
        recoverQueue.add(swipePosition)
        swipePosition = -1
        recoverSwipedItem()
        true
    }


    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val position = recoverQueue.poll() ?: return
            recyclerView?.adapter?.notifyItemChanged(position)
        }
    }

    private fun drawButtons(
        canvas: Canvas,
        buttons: List<UnderlayButton>,
        itemView: View,
        dX: Float
    ) {
        var right = itemView.right
        buttons.forEach {  button ->
            val width = button.intrinsicWidth / buttons.intrinsicWidth() * kotlin.math.abs(dX)
            val left = right - width
            button.draw(
                canvas,
                RectF(left, itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat())
            )

            right = left.toInt()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val position = viewHolder.adapterPosition
        var maxDX = dX
        val itemView = viewHolder.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                if (!buttonsBuffer.containsKey(position)) {
                    buttonsBuffer[position] =
                        instantiateUnderlayButton(context = activityAdapterUtilities.getContext(),
                            baseAdaptor = activityAdapterUtilities.getAdapter(),position = position)
                }

                val buttons = buttonsBuffer[position] ?: return
                if (buttons.isEmpty()) return
                maxDX = (-buttons.intrinsicWidth()).coerceAtLeast(dX)
                drawButtons(c, buttons, itemView, maxDX)
            }
        }

        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            maxDX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //activityAdapterUtilities.getAdapter().markPosition(viewHolder.adapterPosition)

        val position = viewHolder.adapterPosition
        if (swipePosition != position) {
            recoverQueue.add(swipePosition)
        }

        swipePosition = position
        recoverSwipedItem()
        recyclerView?.setOnTouchListener(touchListener)
    }


    abstract fun instantiateUnderlayButton(
        context: Context,
        baseAdaptor: BaseRVAdapter<*>,
        position: Int
    ): List<UnderlayButton>

    //region UnderlayButton
    interface UnderlayButtonClickListener {
        fun onClick()
    }


    class UnderlayButton(
        private val context: Context,
        private val title: String,
        textSize: Float,
        @ColorRes private val colorRes: Int,
        private val clickListener: UnderlayButtonClickListener
    ) {
        private var clickableRegion: RectF? = null
        private val textSizeInPixel: Float = textSize * context.resources.displayMetrics.density // dp to px
        private val horizontalPadding = 50.0f
        val intrinsicWidth: Float

        init {
            val paint = Paint()
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.LEFT
            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)
            intrinsicWidth = titleBounds.width() + 2 * horizontalPadding
        }

        fun draw(canvas: Canvas, rect: RectF) {
            val paint = Paint()

            // Draw background
            paint.color = ContextCompat.getColor(context, colorRes)
            canvas.drawRect(rect, paint)

            // Draw title
            paint.color = ContextCompat.getColor(context, android.R.color.white)
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.LEFT

            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)

            val y = rect.height() / 2 + titleBounds.height() / 2 - titleBounds.bottom
            canvas.drawText(title, rect.left + horizontalPadding, rect.top + y, paint)

            clickableRegion = rect
        }

        fun handle(event: MotionEvent) {
            clickableRegion?.let {
                if (it.contains(event.x, event.y)) {
                    clickListener.onClick()
                }
            }
        }
    }


}

private fun List<ItemTouchCallback.UnderlayButton>.intrinsicWidth(): Float {
    if (isEmpty()) return 0.0f
    return map { it.intrinsicWidth }.reduce { acc, fl -> acc + fl }
}