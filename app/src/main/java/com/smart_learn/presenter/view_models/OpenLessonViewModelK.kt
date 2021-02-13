package com.smart_learn.presenter.view_models

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.OpenLessonActivityK
import com.smart_learn.core.general.showAddDictionaryDialog
import com.smart_learn.core.services.ApplicationServiceK
import kotlinx.android.synthetic.main.activity_open_lesson.*

class OpenLessonViewModelK(private var openLessonActivityK: OpenLessonActivityK) :
    ActivityViewModelUtilitiesK<OpenLessonViewModelK, OpenLessonActivityK> {

    private var activity: Activity = openLessonActivityK.getActivity()

    /** FIXME: fix this BUG . A fix can be to instantiate variable in the init constructor
     *  when you initialize something with 'this' put declaration last or to try to make DatabaseHandlerK a singleton class
     *   but this problem will remain
     * If after this declaration exist other declaration then will appear some null exception when you
     * will try to access that data
     *
     * Example: if declaration will be
     *     private var applicationServiceK: ApplicationServiceK = ApplicationServiceK(this)
     *     private var activity: Activity = openLessonActivityK.getActivity()
     *
     *   it will appear a null exception in DatabaseHandlerK because activity is NULL
     *   (application service will go through services until the repository and after that to DatabaseHandlerK)
     * */
    private var applicationServiceK: ApplicationServiceK = ApplicationServiceK(this)

    init {

        activity.btnNewDictionary.setOnClickListener {
            showAddDictionaryDialog(
                "OpenLessonViewModelK",
                activity,
                R.layout.dialog_new_dictionary,
                applicationServiceK
            )
        }

        activity.btnOpenDictionary.setOnClickListener {
            openLessonActivityK.startDictionariesRVActivity()
        }

    }

    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): OpenLessonViewModelK {
        return this
    }

    override fun getParentActivity(): OpenLessonActivityK {
        return openLessonActivityK
    }

    override fun getApplicationService(): ApplicationServiceK {
        return applicationServiceK
    }

}