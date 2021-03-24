package com.smart_learn.presenter.recycler_view.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smart_learn.R
import com.smart_learn.data.entities.LessonEntranceK
import com.smart_learn.core.general.indexesOf
import com.smart_learn.core.general.showAddEntranceDialog
import com.smart_learn.presenter.recycler_view.ActionModeCallbackK
import com.smart_learn.presenter.view_models.EntranceRVViewModelK
import kotlinx.android.synthetic.main.activity_rv_entrances.*
import kotlinx.android.synthetic.main.layout_lesson_entrance_details.view.*
import java.util.*

class EntrancesRVAdapterK(
    private val entranceRVViewModel: EntranceRVViewModelK
) : BaseRVAdapterK<LessonEntranceK>(entranceRVViewModel), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EntrancesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_lesson_entrance_details,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is EntrancesViewHolder -> {
                holder.bind(items[position])
            }
        }
    }


    override fun selectItem(item: LessonEntranceK) {
        item.isSelected = true
    }

    override fun deselectItem(item: LessonEntranceK) {
        item.isSelected = false
    }

    override fun deleteFromDatabase(item: LessonEntranceK) {
        entranceRVViewModel.getApplicationService().lessonServiceK
            .deleteWord(item.entranceId)
    }

    override fun checkEmptyState(){

        if (items.isEmpty()) {
            activity.tvNoEntry.visibility = View.VISIBLE
            activity.btnAddEntrance.visibility = View.VISIBLE

            // clear all selected
            clearSelectedElements(true)
            return
        }

        activity.tvNoEntry.visibility = View.INVISIBLE
        activity.btnAddEntrance.visibility = View.INVISIBLE
    }

    override fun getContext(): Context {
        return entranceRVViewModel.getParentActivity()
    }

    override fun getRecyclerView(): RecyclerView {
        return activity.rvEntrance
    }

    override fun getAdapter(): BaseRVAdapterK<LessonEntranceK> {
        return this
    }

    override fun clickUpdateOnSwipe(position: Int) {
        showAddEntranceDialog(
            "EntrancesRVAdapterK",
            activity,
            R.layout.dialog_add_lesson_word,
            entranceRVViewModel.getApplicationService(),
            updateEntrance = true,
            entranceK = items[position],
            updateRecyclerView = true,
            entranceRVAdapter = this,
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

                val filteredItems: List<LessonEntranceK>

                if (searchValue.isEmpty()) {
                    filteredItems = allItems
                } else {
                    filteredItems = allItems.filter {
                        it.word.toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT))
                    }

                    filteredItems.forEach {
                        it.searchIndexes = it.word.toLowerCase(Locale.ROOT).indexesOf(searchValue.toLowerCase(
                            Locale.ROOT))
                    }

                }
                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            /** run on a UI thread */
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                items = (results?.values as List<LessonEntranceK>).toMutableList()
                this@EntrancesRVAdapterK.notifyDataSetChanged()
            }
        }
    }


    /**
     * Class to specific how an element from recycler view will be shown
     * */
    inner class EntrancesViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){

        // itemView.tvEWord refers to element with id tvWord
        // is equivalent to findViewById(R.layout.tvWord)
        private val tvWord: TextView = itemView.tvEntranceWord
        private val tvTranslation: TextView = itemView.tvEntranceTranslation
        private val tvPhonetic: TextView = itemView.tvEntrancePhonetic
        private val ivCheck: ImageView = itemView.ivCheck
        private val viewHolderBackground: Drawable = itemView.background

        /** this constructor is for listener --> more info ( https://www.youtube.com/watch?v=ai9rSGcDhyQ ) */
        init{

            // item view is the recyclerView element
            itemView.setOnClickListener {

                if(actionModeCallbackK != null) {

                    // if element is selected --> deselect element
                    if (items[adapterPosition].isSelected) {
                        items[adapterPosition].isSelected = false
                        selectedItems.remove(adapterPosition)
                        ivCheck.visibility = View.GONE
                    }
                    else { // if element is deselected --> select element
                        items[adapterPosition].isSelected = true
                        selectedItems.add(adapterPosition)
                        ivCheck.visibility = View.VISIBLE

                    }
                    this@EntrancesRVAdapterK.notifyItemChanged(adapterPosition)
                    actionModeCallbackK?.refresh()
                }
            }

            itemView.setOnLongClickListener {

                if(actionModeCallbackK == null) {
                    // Start ActionMode
                    actionModeCallbackK = ActionModeCallbackK(this@EntrancesRVAdapterK)
                    actionModeCallbackK?.startActionMode(
                        entranceRVViewModel.getActivity()
                            .findViewById(R.id.rvEntrance),
                        R.menu.action_mode_menu, "Selected", ""
                    )

                    // change background for items
                    // in bind function is made that change
                    this@EntrancesRVAdapterK.notifyDataSetChanged()
                }

                return@setOnLongClickListener true
            }

        }

        /** This updates the info for elements from recycler view
         *
         * Using this function you decide how elements are shown in the recycler view list
         * */
        fun bind(lessonEntranceK: LessonEntranceK){

            tvWord.text = lessonEntranceK.word
            tvTranslation.text = lessonEntranceK.translation
            tvPhonetic.text = lessonEntranceK.phonetic

            if(actionModeCallbackK != null) {
                itemView.setBackgroundResource(R.color.colorToolbar)

                if (lessonEntranceK.isSelected) {
                    ivCheck.visibility = View.VISIBLE
                    // https://www.tutorialkart.com/kotlin-android/how-to-dynamically-change-button-background-in-kotlin-android/
                    itemView.setBackgroundResource(R.drawable.md_btn_selected)

                    return
                }

                ivCheck.visibility = View.INVISIBLE
                return
            }

            ivCheck.visibility = View.INVISIBLE
            itemView.background = viewHolderBackground
        }
    }
}