package com.licenta.smart_learn.services.activities

import android.app.Activity
import com.licenta.smart_learn.R
import com.licenta.smart_learn.activities.MainActivity
import com.licenta.smart_learn.general.ActivityServiceUtilities
import com.licenta.smart_learn.general.showAddDictionaryDialog
import com.licenta.smart_learn.services.ApplicationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityService(private var mainActivity: MainActivity) :
    ActivityServiceUtilities<MainActivityService, MainActivity> {

    private var activity: Activity = mainActivity.getActivity()

    /** FIXME: fix this BUG . A fix can be to instantiate variable in the init constructor
     *  when you initialize something with 'this' put declaration last or to try to make DatabaseHandler a singleton class
     *   but this problem will remain
     * If after this declaration exist other declaration then will appear some null exception when you
     * will try to access that data
     *
     * Example: if declaration will be
     *     private var applicationService: ApplicationService = ApplicationService(this)
     *     private var activity: Activity = mainActivity.getActivity()
     *
     *   it will appear a null exception in DatabaseHandler because activity is NULL
     *   (application service will go through services until the repository and after that to DatabaseHandler)
     * */
    private var applicationService: ApplicationService = ApplicationService(this)

    init {

        activity.btnNewDictionary.setOnClickListener {
            showAddDictionaryDialog(
                "MainActivityService",
                activity,
                R.layout.dialog_new_dictionary,
                applicationService
            )
        }

        activity.btnOpenDictionary.setOnClickListener {
            mainActivity.startDictionariesRVActivity()
        }

    }

    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): MainActivityService {
        return this
    }

    override fun getParentActivity(): MainActivity {
        return mainActivity
    }

    override fun getApplicationService(): ApplicationService {
        return applicationService
    }

}