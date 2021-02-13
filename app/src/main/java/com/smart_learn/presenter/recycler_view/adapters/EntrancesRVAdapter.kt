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
import com.smart_learn.data.entities.DictionaryEntrance
import com.smart_learn.core.general.indexesOf
import com.smart_learn.core.general.showAddEntranceDialog
import com.smart_learn.presenter.recycler_view.ActionModeCallback
import com.smart_learn.core.services.activities.EntrancesRVActivityService
import kotlinx.android.synthetic.main.activity_rv_entrances.*
import kotlinx.android.synthetic.main.layout_dictionary_entrance_details.view.*
import java.util.*

class EntrancesRVAdapter(
    private val entrancesRVActivityService: EntrancesRVActivityService
) : BaseRVAdapter<DictionaryEntrance>(entrancesRVActivityService), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EntrancesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_dictionary_entrance_details,
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


    override fun selectItem(item: DictionaryEntrance) {
        item.isSelected = true
    }

    override fun deselectItem(item: DictionaryEntrance) {
        item.isSelected = false
    }

    override fun deleteFromDatabase(item: DictionaryEntrance) {
        entrancesRVActivityService.getApplicationService().lessonServiceK
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
        return entrancesRVActivityService.getParentActivity()
    }

    override fun getRecyclerView(): RecyclerView {
        return activity.rvEntrance
    }

    override fun getAdapter(): BaseRVAdapter<DictionaryEntrance> {
        return this
    }

    override fun clickUpdateOnSwipe(position: Int) {
        showAddEntranceDialog(
            "EntrancesRVAdapter",
            activity,
            R.layout.dialog_add_word,
            entrancesRVActivityService.getApplicationService(),
            updateEntrance = true,
            entrance = items[position],
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

                val filteredItems: List<DictionaryEntrance>

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
                items = (results?.values as List<DictionaryEntrance>).toMutableList()
                this@EntrancesRVAdapter.notifyDataSetChanged()
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

                if(actionModeCallback != null) {

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
                    this@EntrancesRVAdapter.notifyItemChanged(adapterPosition)
                    actionModeCallback?.refresh()
                }
            }

            itemView.setOnLongClickListener {

                if(actionModeCallback == null) {
                    // Start ActionMode
                    actionModeCallback = ActionModeCallback(this@EntrancesRVAdapter)
                    actionModeCallback?.startActionMode(
                        entrancesRVActivityService.getActivity()
                            .findViewById(R.id.rvEntrance),
                        R.menu.action_mode_menu, "Selected", ""
                    )

                    // change background for items
                    // in bind function is made that change
                    this@EntrancesRVAdapter.notifyDataSetChanged()
                }

                return@setOnLongClickListener true
            }

        }

        /** This updates the info for elements from recycler view
         *
         * Using this function you decide how elements are shown in the recycler view list
         * */
        fun bind(dictionaryEntrance: DictionaryEntrance){

            tvWord.text = dictionaryEntrance.word
            tvTranslation.text = dictionaryEntrance.translation
            tvPhonetic.text = dictionaryEntrance.phonetic

            if(actionModeCallback != null) {
                itemView.setBackgroundResource(R.color.colorToolbar)

                if (dictionaryEntrance.isSelected) {
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