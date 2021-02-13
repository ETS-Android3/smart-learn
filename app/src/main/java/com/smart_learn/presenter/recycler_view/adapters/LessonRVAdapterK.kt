package com.smart_learn.presenter.recycler_view.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smart_learn.R
import com.smart_learn.data.entities.LessonDetailsK
import com.smart_learn.core.general.SELECTED_LESSON_ID
import com.smart_learn.core.general.indexesOf
import com.smart_learn.core.general.showAddLessonDialog
import com.smart_learn.presenter.recycler_view.ActionModeCallbackK
import com.smart_learn.presenter.view_models.LessonRVViewModelK
import kotlinx.android.synthetic.main.activity_rv_lessons.*
import kotlinx.android.synthetic.main.layout_lesson_details.view.*
import java.util.*
import kotlin.collections.ArrayList

class LessonRVAdapterK(
    private val lessonRVViewModel: LessonRVViewModelK
) : BaseRVAdapterK<LessonDetailsK>(lessonRVViewModel), Filterable {


    /** override from RecyclerView.Adapter<RecyclerView.ViewHolder> */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LessonViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_lesson_details,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LessonViewHolder -> {
                holder.bind(items[position])
            }
        }
    }


    /** override from base adapter */
    override fun selectItem(item: LessonDetailsK) {
        item.isSelected = true
    }

    override fun deselectItem(item: LessonDetailsK) {
        item.isSelected = false
    }

    override fun deleteFromDatabase(item: LessonDetailsK) {
        lessonRVViewModel.getApplicationService().lessonServiceK
            .delete(item.lessonId)
    }

    override fun checkEmptyState(){

        if (items.isEmpty()) {
            activity.tvNoEntryLessonRV.visibility = View.VISIBLE
            activity.btnAddLessonRV.visibility = View.VISIBLE

            // clear all selected
            clearSelectedElements(true)
            return
        }

        activity.tvNoEntryLessonRV.visibility = View.INVISIBLE
        activity.btnAddLessonRV.visibility = View.INVISIBLE
    }

    override fun getContext(): Context {
        return lessonRVViewModel.getParentActivity()
    }

    override fun getRecyclerView(): RecyclerView {
        return activity.rvLessons
    }

    override fun getAdapter(): BaseRVAdapterK<LessonDetailsK> {
        return this
    }

    override fun clickUpdateOnSwipe(position: Int) {
        showAddLessonDialog(
            "LessonRVAdapter",
            activity,
            R.layout.dialog_new_lesson,
            lessonRVViewModel.getApplicationService(),
            updateLesson = true,
            currentLessonK = items[position],
            updateRecyclerView = true,
            lessonRVAdapter = this,
            position = position
        )
    }


    /** Filter used in search mode
     *  https://johncodeos.com/how-to-add-search-in-recyclerview-using-kotlin/
     *  https://www.youtube.com/watch?v=CTvzoVtKoJ8&list=WL&index=78&t=285s
     * */
    override fun getFilter(): Filter {
        return object : Filter() {

            /** run in background thread */
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchValue = constraint.toString()

                val filteredItems: List<LessonDetailsK>

                if (searchValue.isEmpty()) {
                    filteredItems = allItems
                } else {
                    filteredItems = allItems.filter {
                        it.title.toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT))
                    }

                    filteredItems.forEach {
                        it.searchIndexes = it.title.toLowerCase(Locale.ROOT).indexesOf(searchValue.toLowerCase(Locale.ROOT))
                    }

                }
                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            /** run on a UI thread */
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                items = (results?.values as List<LessonDetailsK>).toMutableList()
                this@LessonRVAdapterK.notifyDataSetChanged()
            }
        }
    }


    /**
     * Class to specific how an element from recycler view will be shown
     * */
    inner class LessonViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

        // itemView.tvLessonName refers to element with id tvLessonName
        // is equivalent to findViewById(R.layout.tvLessonName)
        private val tvLessonName: TextView = itemView.tvLessonName
        private val ivCheck: ImageView = itemView.ivCheckLesson
        private val viewHolderBackground: Drawable = itemView.background


        /**this constructor is for listener --> more info ( https://www.youtube.com/watch?v=ai9rSGcDhyQ )*/
        init{

            itemView.setOnClickListener {

                if(actionModeCallbackK != null) {

                    if(items[adapterPosition].isSelected){
                        items[adapterPosition].isSelected = false
                        selectedItems.remove(adapterPosition)
                        ivCheck.visibility = View.GONE
                    }
                    else{
                        items[adapterPosition].isSelected = true
                        selectedItems.add(adapterPosition)
                        ivCheck.visibility = View.VISIBLE
                    }
                    this@LessonRVAdapterK.notifyItemChanged(adapterPosition)
                    actionModeCallbackK?.refresh()
                    return@setOnClickListener
                }


                // adapterPosition is equivalent to this.adapterPosition
                // in this lambda function 'this' keyword is referred to itemView
                // here we get the current clicked position from list
                val position: Int = adapterPosition

                // choose what to do with that element
                // set selected lesson and launch LessonActivityK
                SELECTED_LESSON_ID = items[position].lessonId

                lessonRVViewModel.getParentActivity().startLessonActivityK()
            }

            itemView.setOnLongClickListener {

                if(actionModeCallbackK == null) {
                    // Start ActionMode
                    actionModeCallbackK = ActionModeCallbackK(this@LessonRVAdapterK)
                    actionModeCallbackK?.startActionMode(
                        lessonRVViewModel.getActivity()
                            .findViewById(R.id.rvLessons),
                        R.menu.action_mode_menu, "Selected", ""
                    )

                    // change background for items
                    // in bind function is made that change
                    this@LessonRVAdapterK.notifyDataSetChanged()
                }

                return@setOnLongClickListener true
            }

        }


        /** this is how single elements are displayed in recycler view */
        fun bind(lessonDetailsK: LessonDetailsK){

            var string: String = lessonDetailsK.title

            if(actionModeCallbackK != null) {
                if(lessonDetailsK.isSelected){
                    ivCheck.visibility = View.VISIBLE
                    // https://www.tutorialkart.com/kotlin-android/how-to-dynamically-change-button-background-in-kotlin-android/
                    itemView.setBackgroundResource(R.drawable.md_btn_selected)
                }
                else{
                    ivCheck.visibility = View.GONE
                    itemView.setBackgroundResource(R.color.colorToolbar)
                }
            }
            else{
                itemView.background = viewHolderBackground
                ivCheck.visibility = View.INVISIBLE
            }

            if(lessonDetailsK.searchIndexes.isNotEmpty()){
                //Log.e("mesaj","lalaala")

                string = string.subSequence(0,lessonDetailsK.searchIndexes[0].first).toString() +
                        "<span style=\"background-color:yellow\">" +
                        string.subSequence(lessonDetailsK.searchIndexes[0].first,lessonDetailsK.searchIndexes[0].last).toString() + "</span>" +
                        string.subSequence(lessonDetailsK.searchIndexes[0].last,string.length).toString()

                // TODO: check this deprecated function
                tvLessonName.text = Html.fromHtml(string)
                // remove indexes
                lessonDetailsK.searchIndexes = ArrayList()

                return
            }

            tvLessonName.text = lessonDetailsK.title

        }

    }

}