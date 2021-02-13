package com.smart_learn.presenter.view_models

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.LessonRVActivityK
import com.smart_learn.data.entities.LessonDetailsK
import com.smart_learn.core.general.*
import com.smart_learn.presenter.recycler_view.adapters.LessonRVAdapterK
import com.smart_learn.core.services.ApplicationServiceK
import kotlinx.android.synthetic.main.activity_rv_lessons.*

class LessonRVViewModelK(private var lessonRVActivityK: LessonRVActivityK)
    : ActivityViewModelUtilitiesK<LessonRVViewModelK, LessonRVActivityK> {

    private var activity: Activity = lessonRVActivityK.getActivity()
    private lateinit var lessonRVAdapter: LessonRVAdapterK
    private var applicationServiceK: ApplicationServiceK = ApplicationServiceK(this)

    init{

        activity.btnAddLessonRV.setOnClickListener{
            showAddLessonDialog(
                "LessonRVViewModelK",
                activity,
                R.layout.dialog_new_lesson,
                applicationServiceK,
                updateRecyclerView = true,
                lessonRVAdapter = lessonRVAdapter as LessonRVAdapterK
            )
        }

        startActivityStateData()
    }

    private fun startActivityStateData(){
        // start recycler view
        lessonRVAdapter = initRecyclerView(
            activity = activity,
            recyclerView = activity.rvLessons,
            itemDecoration = ItemDecoration(10),
            activityViewModelUtilitiesK = this,
            adapterType = 1
        ) as LessonRVAdapterK

        // add data in recycler view
        loadData()

        // init gestures
        initItemTouchCallback(activity.rvLessons,lessonRVAdapter)
    }


    /** load data from database into recycler view */
    private fun loadData(){
        val tmpList: MutableList<LessonDetailsK> = ArrayList()
        applicationServiceK.lessonServiceK.getAllLiveSampleLessons().forEach {
            tmpList.add(it)
        }
        lessonRVAdapter.submitList(tmpList)
    }


    /** When you modify data in another activity reload the entire list for recycler view */
    fun resetStateData(){
        startActivityStateData()
    }


    fun getAdapter(): LessonRVAdapterK { return lessonRVAdapter }


    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): LessonRVViewModelK {
        return this
    }

    override fun getParentActivity(): LessonRVActivityK {
        return lessonRVActivityK
    }

    override fun getApplicationService(): ApplicationServiceK {
        return applicationServiceK
    }

}