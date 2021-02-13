package com.smart_learn.core.services.activities

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.DictionariesRVActivity
import com.smart_learn.data.entities.DictionaryDetails
import com.smart_learn.core.general.*
import com.smart_learn.recycler_view.adapters.DictionariesRVAdapter
import com.smart_learn.core.services.ApplicationService
import kotlinx.android.synthetic.main.activity_rv_dictionaries.*

class DictionariesRVActivityService(private var dictionariesRVActivity: DictionariesRVActivity)
    : ActivityServiceUtilities<DictionariesRVActivityService, DictionariesRVActivity> {

    private var activity: Activity = dictionariesRVActivity.getActivity()
    private lateinit var dictionariesRVAdapter: DictionariesRVAdapter
    private var applicationService: ApplicationService = ApplicationService(this)

    init{

        activity.btnAddDictRV.setOnClickListener{
            showAddDictionaryDialog(
                "DictionariesRVActivityService",
                activity,
                R.layout.dialog_new_dictionary,
                applicationService,
                updateRecyclerView = true,
                dictionariesRVAdapter = dictionariesRVAdapter as DictionariesRVAdapter
            )
        }

        startActivityStateData()
    }

    private fun startActivityStateData(){
        // start recycler view
        dictionariesRVAdapter = initRecyclerView(
            activity = activity,
            recyclerView = activity.rvDictionaries,
            itemDecoration = ItemDecoration(10),
            activityServiceUtilities = this,
            adapterType = 1
        ) as DictionariesRVAdapter

        // add data in recycler view
        loadData()

        // init gestures
        initItemTouchCallback(activity.rvDictionaries,dictionariesRVAdapter)
    }


    /** load data from database into recycler view */
    private fun loadData(){
        val tmpList: MutableList<DictionaryDetails> = ArrayList()
        applicationService.dictionaryService.getDictionaries().forEach {
            tmpList.add(it)
        }
        dictionariesRVAdapter.submitList(tmpList)
    }


    /** When you modify data in another activity reload the entire list for recycler view */
    fun resetStateData(){
        startActivityStateData()
    }


    fun getAdapter(): DictionariesRVAdapter { return dictionariesRVAdapter }


    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): DictionariesRVActivityService {
        return this
    }

    override fun getParentActivity(): DictionariesRVActivity {
        return dictionariesRVActivity
    }

    override fun getApplicationService(): ApplicationService {
        return applicationService
    }

}