package com.smart_learn.core.services.activities

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.DictionaryActivity
import com.smart_learn.core.general.ActivityServiceUtilities
import com.smart_learn.core.general.SELECTED_DICTIONARY_ID
import com.smart_learn.core.general.showAddDictionaryDialog
import com.smart_learn.core.general.showAddEntranceDialog
import com.smart_learn.core.services.ApplicationService
import com.smart_learn.core.services.TestService
import kotlinx.android.synthetic.main.activity_dictionary.*


class DictionaryActivityService(private var dictionaryActivity: DictionaryActivity)
    : ActivityServiceUtilities<DictionaryActivityService, DictionaryActivity> {

    private var activity: Activity = dictionaryActivity.getActivity()
    private var applicationService: ApplicationService = ApplicationService(this)

    init {
        activity.btnAddNewWord.setOnClickListener {
            showAddEntranceDialog(
                "DictionaryActivityService",
                activity,
                R.layout.dialog_add_word,
                applicationService)
        }

        activity.btnTestLocal.setOnClickListener {
            TestService.getTestServiceInstance().currentTestMode.set(TestService.LOCAL_MODE_TEST.toInt())
            TestService.getTestServiceInstance().launchTestActivity()
        }

        activity.btnTestOnline.setOnClickListener {
            // TODO: remove this when refactoring is made
            TestService.getTestServiceInstance().currentTestMode.set(TestService.REMOTE_MODE_TEST.toInt())
            dictionaryActivity.startTestGenerationActivity()
        }

        activity.btnDisplayWords.setOnClickListener {
            dictionaryActivity.startEntrancesRVActivity()
        }

        activity.btnDeleteDictionary.setOnClickListener {
            applicationService.dictionaryService.deleteDictionary(SELECTED_DICTIONARY_ID)

            // go to previous activity
            dictionaryActivity.startDictionariesRVActivity()
        }

        activity.btnUpdateD.setOnClickListener {
            showAddDictionaryDialog(
                "DictionaryActivityService",
                activity,
                R.layout.dialog_new_dictionary,
                applicationService,
                updateDictionary = true,
                currentDictionary = applicationService.dictionaryService.getDictionary(SELECTED_DICTIONARY_ID)
            )
        }
    }



    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): DictionaryActivityService {
        return this
    }

    override fun getParentActivity(): DictionaryActivity {
        return dictionaryActivity
    }

    override fun getApplicationService(): ApplicationService {
        return applicationService
    }

}