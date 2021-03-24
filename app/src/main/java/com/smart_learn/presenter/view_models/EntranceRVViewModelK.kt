package com.smart_learn.presenter.view_models

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.EntranceRVActivityK
import com.smart_learn.data.entities.LessonEntranceK
import com.smart_learn.core.general.*
import com.smart_learn.presenter.recycler_view.adapters.EntrancesRVAdapterK
import com.smart_learn.core.services.ApplicationServiceK
import kotlinx.android.synthetic.main.activity_rv_entrances.*

class EntranceRVViewModelK(private var entranceRVActivityK: EntranceRVActivityK)
    : ActivityViewModelUtilitiesK<EntranceRVViewModelK, EntranceRVActivityK> {

    private var activity: Activity = entranceRVActivityK.getActivity()
    private lateinit var entrancesRVAdapter: EntrancesRVAdapterK
    private var applicationServiceK: ApplicationServiceK = ApplicationServiceK(this)

    init{

        activity.btnAddEntrance.setOnClickListener{
            showAddEntranceDialog(
                "EntranceRVActivityService",
                activity,
                R.layout.dialog_add_lesson_word,
                applicationServiceK,
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
            activityViewModelUtilitiesK = this,
            adapterType = 2
        ) as EntrancesRVAdapterK

        // add data in recycler view
        loadData()

        // init gestures
        initItemTouchCallback(activity.rvEntrance,entrancesRVAdapter)
    }


    /** load data from database into recycler view */
    private fun loadData() {
        val tmpList: MutableList<LessonEntranceK> = ArrayList()
        applicationServiceK.lessonServiceK.getFullLiveLessonInfo(SELECTED_LESSON_ID).forEach {
            tmpList.add(it)
        }
        entrancesRVAdapter.submitList(tmpList)
    }


    /** When you modify data in another activity reload the entire list for recycler view */
    fun resetStateData(){
        startActivityStateData()
    }

    fun getAdapter(): EntrancesRVAdapterK { return entrancesRVAdapter }

    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): EntranceRVViewModelK {
        return this
    }

    override fun getParentActivity(): EntranceRVActivityK {
        return entranceRVActivityK
    }

    override fun getApplicationService(): ApplicationServiceK {
        return applicationServiceK
    }

}