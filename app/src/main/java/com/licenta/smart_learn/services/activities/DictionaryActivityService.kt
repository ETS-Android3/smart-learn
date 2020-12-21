package com.licenta.smart_learn.services.activities

import android.app.Activity
import com.licenta.smart_learn.R
import com.licenta.smart_learn.activities.DictionaryActivity
import com.licenta.smart_learn.general.ActivityServiceUtilities
import com.licenta.smart_learn.general.SELECTED_DICTIONARY_ID
import com.licenta.smart_learn.general.showAddDictionaryDialog
import com.licenta.smart_learn.general.showAddEntranceDialog
import com.licenta.smart_learn.services.ApplicationService
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

        activity.btnTest.setOnClickListener {
            dictionaryActivity.startTestActivity()
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