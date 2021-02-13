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
import com.smart_learn.data.entities.DictionaryDetailsK
import com.smart_learn.core.general.SELECTED_DICTIONARY_ID
import com.smart_learn.core.general.indexesOf
import com.smart_learn.core.general.showAddDictionaryDialog
import com.smart_learn.presenter.recycler_view.ActionModeCallbackK
import com.smart_learn.presenter.view_models.LessonRVViewModelK
import kotlinx.android.synthetic.main.activity_rv_dictionaries.*
import kotlinx.android.synthetic.main.layout_dictionary_details.view.*
import java.util.*
import kotlin.collections.ArrayList

class DictionariesRVAdapterK(
    private val lessonRVViewModel: LessonRVViewModelK
) : BaseRVAdapterK<DictionaryDetailsK>(lessonRVViewModel), Filterable {


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
    override fun selectItem(item: DictionaryDetailsK) {
        item.isSelected = true
    }

    override fun deselectItem(item: DictionaryDetailsK) {
        item.isSelected = false
    }

    override fun deleteFromDatabase(item: DictionaryDetailsK) {
        lessonRVViewModel.getApplicationService().lessonServiceK
            .delete(item.dictionaryId)
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
        return lessonRVViewModel.getParentActivity()
    }

    override fun getRecyclerView(): RecyclerView {
        return activity.rvDictionaries
    }

    override fun getAdapter(): BaseRVAdapterK<DictionaryDetailsK> {
        return this
    }

    override fun clickUpdateOnSwipe(position: Int) {
        showAddDictionaryDialog(
            "DictionaryRVAdapter",
            activity,
            R.layout.dialog_new_dictionary,
            lessonRVViewModel.getApplicationService(),
            updateDictionary = true,
            currentDictionaryK = items[position],
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

                val filteredItems: List<DictionaryDetailsK>

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
                items = (results?.values as List<DictionaryDetailsK>).toMutableList()
                this@DictionariesRVAdapterK.notifyDataSetChanged()
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
                    this@DictionariesRVAdapterK.notifyItemChanged(adapterPosition)
                    actionModeCallbackK?.refresh()
                    return@setOnClickListener
                }


                // adapterPosition is equivalent to this.adapterPosition
                // in this lambda function 'this' keyword is referred to itemView
                // here we get the current clicked position from list
                val position: Int = adapterPosition

                // choose what to do with that element
                // set selected dictionary and launch LessonActivityK
                SELECTED_DICTIONARY_ID = items[position].dictionaryId

                lessonRVViewModel.getParentActivity().startDictionaryActivity()
            }

            itemView.setOnLongClickListener {

                if(actionModeCallbackK == null) {
                    // Start ActionMode
                    actionModeCallbackK = ActionModeCallbackK(this@DictionariesRVAdapterK)
                    actionModeCallbackK?.startActionMode(
                        lessonRVViewModel.getActivity()
                            .findViewById(R.id.rvDictionaries),
                        R.menu.action_mode_menu, "Selected", ""
                    )

                    // change background for items
                    // in bind function is made that change
                    this@DictionariesRVAdapterK.notifyDataSetChanged()
                }

                return@setOnLongClickListener true
            }

        }


        /** this is how single elements are displayed in recycler view */
        fun bind(dictionaryDetailsK: DictionaryDetailsK){

            var string: String = dictionaryDetailsK.title

            if(actionModeCallbackK != null) {
                if(dictionaryDetailsK.isSelected){
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

            if(dictionaryDetailsK.searchIndexes.isNotEmpty()){
                //Log.e("mesaj","lalaala")

                string = string.subSequence(0,dictionaryDetailsK.searchIndexes[0].first).toString() +
                        "<span style=\"background-color:yellow\">" +
                        string.subSequence(dictionaryDetailsK.searchIndexes[0].first,dictionaryDetailsK.searchIndexes[0].last).toString() + "</span>" +
                        string.subSequence(dictionaryDetailsK.searchIndexes[0].last,string.length).toString()

                // TODO: check this deprecated function
                dictionaryNameTextView.text = Html.fromHtml(string)
                // remove indexes
                dictionaryDetailsK.searchIndexes = ArrayList()

                return
            }

            dictionaryNameTextView.text = dictionaryDetailsK.title

        }

    }

}