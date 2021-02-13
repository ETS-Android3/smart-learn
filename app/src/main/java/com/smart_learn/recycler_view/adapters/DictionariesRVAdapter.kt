package com.smart_learn.recycler_view.adapters

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
import com.smart_learn.data.entities.DictionaryDetails
import com.smart_learn.core.general.SELECTED_DICTIONARY_ID
import com.smart_learn.core.general.indexesOf
import com.smart_learn.core.general.showAddDictionaryDialog
import com.smart_learn.recycler_view.ActionModeCallback
import com.smart_learn.core.services.activities.DictionariesRVActivityService
import kotlinx.android.synthetic.main.activity_rv_dictionaries.*
import kotlinx.android.synthetic.main.layout_dictionary_details.view.*
import java.util.*
import kotlin.collections.ArrayList

class DictionariesRVAdapter(
    private val dictionariesRVActivityService: DictionariesRVActivityService
) : BaseRVAdapter<DictionaryDetails>(dictionariesRVActivityService), Filterable {


    /** override from RecyclerView.Adapter<RecyclerView.ViewHolder> */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DictionaryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_dictionary_details,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is DictionaryViewHolder -> {
                holder.bind(items[position])
            }
        }
    }


    /** override from base adapter */
    override fun selectItem(item: DictionaryDetails) {
        item.isSelected = true
    }

    override fun deselectItem(item: DictionaryDetails) {
        item.isSelected = false
    }

    override fun deleteFromDatabase(item: DictionaryDetails) {
        dictionariesRVActivityService.getApplicationService().dictionaryService
            .deleteDictionary(item.dictionaryId)
    }

    override fun checkEmptyState(){

        if (items.isEmpty()) {
            activity.tvNoEntryDictRV.visibility = View.VISIBLE
            activity.btnAddDictRV.visibility = View.VISIBLE

            // clear all selected
            clearSelectedElements(true)
            return
        }

        activity.tvNoEntryDictRV.visibility = View.INVISIBLE
        activity.btnAddDictRV.visibility = View.INVISIBLE
    }

    override fun getContext(): Context {
        return dictionariesRVActivityService.getParentActivity()
    }

    override fun getRecyclerView(): RecyclerView {
        return activity.rvDictionaries
    }

    override fun getAdapter(): BaseRVAdapter<DictionaryDetails> {
        return this
    }

    override fun clickUpdateOnSwipe(position: Int) {
        showAddDictionaryDialog(
            "DictionaryRVAdapter",
            activity,
            R.layout.dialog_new_dictionary,
            dictionariesRVActivityService.getApplicationService(),
            updateDictionary = true,
            currentDictionary = items[position],
            updateRecyclerView = true,
            dictionariesRVAdapter = this,
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

                val filteredItems: List<DictionaryDetails>

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
                items = (results?.values as List<DictionaryDetails>).toMutableList()
                this@DictionariesRVAdapter.notifyDataSetChanged()
            }
        }
    }


    /**
     * Class to specific how an element from recycler view will be shown
     * */
    inner class DictionaryViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

        // itemView.dictionaryNameTextView refers to element with id dictionaryNameTextView
        // is equivalent to findViewById(R.layout.dictionaryNameTextView)
        private val dictionaryNameTextView: TextView = itemView.dictionaryNameTextView
        private val ivCheck: ImageView = itemView.ivCheckDict
        private val viewHolderBackground: Drawable = itemView.background


        /**this constructor is for listener --> more info ( https://www.youtube.com/watch?v=ai9rSGcDhyQ )*/
        init{

            itemView.setOnClickListener {

                if(actionModeCallback != null) {

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
                    this@DictionariesRVAdapter.notifyItemChanged(adapterPosition)
                    actionModeCallback?.refresh()
                    return@setOnClickListener
                }


                // adapterPosition is equivalent to this.adapterPosition
                // in this lambda function 'this' keyword is referred to itemView
                // here we get the current clicked position from list
                val position: Int = adapterPosition

                // choose what to do with that element
                // set selected dictionary and launch DictionaryActivity
                SELECTED_DICTIONARY_ID = items[position].dictionaryId

                dictionariesRVActivityService.getParentActivity().startDictionaryActivity()
            }

            itemView.setOnLongClickListener {

                if(actionModeCallback == null) {
                    // Start ActionMode
                    actionModeCallback = ActionModeCallback(this@DictionariesRVAdapter)
                    actionModeCallback?.startActionMode(
                        dictionariesRVActivityService.getActivity()
                            .findViewById(R.id.rvDictionaries),
                        R.menu.action_mode_menu, "Selected", ""
                    )

                    // change background for items
                    // in bind function is made that change
                    this@DictionariesRVAdapter.notifyDataSetChanged()
                }

                return@setOnLongClickListener true
            }

        }


        /** this is how single elements are displayed in recycler view */
        fun bind(dictionaryDetails: DictionaryDetails){

            var string: String = dictionaryDetails.title

            if(actionModeCallback != null) {
                if(dictionaryDetails.isSelected){
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

            if(dictionaryDetails.searchIndexes.isNotEmpty()){
                //Log.e("mesaj","lalaala")

                string = string.subSequence(0,dictionaryDetails.searchIndexes[0].first).toString() +
                        "<span style=\"background-color:yellow\">" +
                        string.subSequence(dictionaryDetails.searchIndexes[0].first,dictionaryDetails.searchIndexes[0].last).toString() + "</span>" +
                        string.subSequence(dictionaryDetails.searchIndexes[0].last,string.length).toString()

                // TODO: check this deprecated function
                dictionaryNameTextView.text = Html.fromHtml(string)
                // remove indexes
                dictionaryDetails.searchIndexes = ArrayList()

                return
            }

            dictionaryNameTextView.text = dictionaryDetails.title

        }

    }

}