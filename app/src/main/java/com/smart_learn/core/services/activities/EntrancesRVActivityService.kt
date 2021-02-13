package com.smart_learn.core.services.activities

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.EntrancesRVActivity
import com.smart_learn.entities.DictionaryEntrance
import com.smart_learn.core.general.*
import com.smart_learn.recycler_view.adapters.EntrancesRVAdapter
import com.smart_learn.core.services.ApplicationService
import kotlinx.android.synthetic.main.activity_rv_entrances.*

class EntrancesRVActivityService(private var entrancesRVActivity: EntrancesRVActivity)
    : ActivityServiceUtilities<EntrancesRVActivityService, EntrancesRVActivity> {

    private var activity: Activity = entrancesRVActivity.getActivity()
    private lateinit var entrancesRVAdapter: EntrancesRVAdapter
    private var applicationService: ApplicationService = ApplicationService(this)

    init{

        activity.btnAddEntrance.setOnClickListener{
            showAddEntranceDialog(
                "EntranceRVActivityService",
                activity,
                R.layout.dialog_add_word,
                applicationService,
                updateRecyclerView = true,
                entranceRVAdapter = entrancesRVAdapter
            )
        }

        startActivityStateData()

    }

    private fun startActivityStateData(){
        // start recycler view
        entrancesRVAdapter = initRecyclerView(
            activity = activity,
            recyclerView = activity.rvEntrance,
            itemDecoration = ItemDecoration(10),
            activityServiceUtilities = this,
            adapterType = 2
        ) as EntrancesRVAdapter

        // add data in recycler view
        loadData()

        // init gestures
        initItemTouchCallback(activity.rvEntrance,entrancesRVAdapter)
    }


    /** load data from database into recycler view */
    private fun loadData() {
        val tmpList: MutableList<DictionaryEntrance> = ArrayList()
        applicationService.dictionaryService.getDictionaryEntries(SELECTED_DICTIONARY_ID).forEach {
            tmpList.add(it)
        }
        entrancesRVAdapter.submitList(tmpList)
    }


    /** When you modify data in another activity reload the entire list for recycler view */
    fun resetStateData(){
        startActivityStateData()
    }

    fun getAdapter(): EntrancesRVAdapter { return entrancesRVAdapter }

    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): EntrancesRVActivityService {
        return this
    }

    override fun getParentActivity(): EntrancesRVActivity {
        return entrancesRVActivity
    }

    override fun getApplicationService(): ApplicationService {
        return applicationService
    }

}