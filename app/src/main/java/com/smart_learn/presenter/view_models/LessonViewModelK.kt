package com.smart_learn.presenter.view_models

import android.app.Activity
import com.smart_learn.R
import com.smart_learn.presenter.activities.LessonActivityK
import com.smart_learn.core.general.SELECTED_LESSON_ID
import com.smart_learn.core.general.showAddLessonDialog
import com.smart_learn.core.general.showAddEntranceDialog
import com.smart_learn.core.services.ApplicationServiceK
import com.smart_learn.core.services.TestService
import kotlinx.android.synthetic.main.activity_lesson.*


class LessonViewModelK(private var lessonActivityK: LessonActivityK)
    : ActivityViewModelUtilitiesK<LessonViewModelK, LessonActivityK> {

    private var activity: Activity = lessonActivityK.getActivity()
    private var applicationServiceK: ApplicationServiceK = ApplicationServiceK(this)

    init {
        activity.btnAddNewWord.setOnClickListener {
            showAddEntranceDialog(
                "LessonViewModelK",
                activity,
                R.layout.dialog_add_word,
                applicationServiceK)
        }

        activity.btnTestLocal.setOnClickListener {
            TestService.getTestServiceInstance().currentTestMode.set(TestService.LOCAL_MODE_TEST.toInt())
            TestService.getTestServiceInstance().launchTestActivity()
        }

        activity.btnTestOnline.setOnClickListener {
            // TODO: remove this when refactoring is made
            TestService.getTestServiceInstance().currentTestMode.set(TestService.REMOTE_MODE_TEST.toInt())
            lessonActivityK.startTestGenerationActivity()
        }

        activity.btnDisplayWords.setOnClickListener {
            lessonActivityK.startEntrancesRVActivity()
        }

        activity.btnDeleteLesson.setOnClickListener {
            applicationServiceK.lessonServiceK.delete(SELECTED_LESSON_ID)

            // go to previous activity
            lessonActivityK.startLessonRVActivityK()
        }

        activity.btnUpdateLesson.setOnClickListener {
            showAddLessonDialog(
                "LessonViewModelK",
                activity,
                R.layout.dialog_new_lesson,
                applicationServiceK,
                updateLesson = true,
                currentLessonK = applicationServiceK.lessonServiceK.getSampleLiveLesson(SELECTED_LESSON_ID)
            )
        }
    }



    override fun getActivity(): Activity {
        return activity
    }

    override fun getActivityService(): LessonViewModelK {
        return this
    }

    override fun getParentActivity(): LessonActivityK {
        return lessonActivityK
    }

    override fun getApplicationService(): ApplicationServiceK {
        return applicationServiceK
    }

}