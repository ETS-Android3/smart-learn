package com.smart_learn.presenter.view_models

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.LessonRVActivityK
import com.smart_learn.data.entities.DictionaryDetailsK
import com.smart_learn.core.general.*
import com.smart_learn.presenter.recycler_view.adapters.DictionariesRVAdapterK
import com.smart_learn.core.services.ApplicationServiceK
import kotlinx.android.synthetic.main.activity_rv_dictionaries.*

class LessonRVViewModelK(private var lessonRVActivityK: LessonRVActivityK)
    : ActivityViewModelUtilitiesK<LessonRVViewModelK, LessonRVActivityK> {

    private var activity: Activity = lessonRVActivityK.getActivity()
    private lateinit var dictionariesRVAdapter: DictionariesRVAdapterK
    private var applicationServiceK: ApplicationServiceK = ApplicationServiceK(this)

    init{

        activity.btnAddDictRV.setOnClickListener{
            showAddDictionaryDialog(
                "LessonRVViewModelK",
                activity,
                R.layout.dialog_new_dictionary,
                applicationServiceK,
                updateRecyclerView = true,
                dictionariesRVAdapter = dictionariesRVAdapter as DictionariesRVAdapterK
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
            activityViewModelUtilitiesK = this,
            adapterType = 1
        ) as DictionariesRVAdapterK

        // add data in recycler view
        loadData()

        // init gestures
        initItemTouchCallback(activity.rvDictionaries,dictionariesRVAdapter)
    }


    /** load data from database into recycler view */
    private fun loadData(){
        val tmpList: MutableList<DictionaryDetailsK> = ArrayList()
        applicationServiceK.lessonServiceK.getAllLiveSampleLessons().forEach {
            tmpList.add(it)
        }
        dictionariesRVAdapter.submitList(tmpList)
    }


    /** When you modify data in another activity reload the entire list for recycler view */
    fun resetStateData(){
        startActivityStateData()
    }


    fun getAdapter(): DictionariesRVAdapterK { return dictionariesRVAdapter }


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