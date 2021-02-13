package com.smart_learn.core.services

import com.smart_learn.data.entities.LessonDetailsK
import com.smart_learn.data.entities.LessonEntranceK
import com.smart_learn.presenter.view_models.ActivityViewModelUtilitiesK
import com.smart_learn.core.general.SELECTED_LESSON_ID
import com.smart_learn.data.repository.LessonRepositoryK


/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class LessonServiceK(private val activityViewModelUtilitiesK: ActivityViewModelUtilitiesK<*, *>) {

    private var lessonRepositoryK: LessonRepositoryK = LessonRepositoryK(activityViewModelUtilitiesK)

    fun getFullLiveLessonInfo() : List<LessonEntranceK> {
        return lessonRepositoryK.getFullLiveLessonInfo(SELECTED_LESSON_ID)
    }

    fun getFullLiveLessonInfo(lessonId : Int) : List<LessonEntranceK> {
        return lessonRepositoryK.getFullLiveLessonInfo(lessonId)
    }

    fun checkIfLessonExist(lessonName: String) : Boolean {
        return lessonRepositoryK.checkIfLessonExist(lessonName)
    }

    fun checkIfWordExist(word: String, lessonId: Int) : Boolean {
        return lessonRepositoryK.checkIfWordExist(word,lessonId)
    }

    fun insert(lessonName: String){
        lessonRepositoryK.insert(lessonName)
    }

    fun update(lessonDetailsK: LessonDetailsK){
        lessonRepositoryK.update(lessonDetailsK)
    }

    fun delete(lessonId: Int){
        lessonRepositoryK.delete(lessonId)
    }

    fun insert(lessonEntranceK: LessonEntranceK) {
        lessonRepositoryK.insert(lessonEntranceK)
    }

    fun update(lessonEntranceK: LessonEntranceK){
        lessonRepositoryK.update(lessonEntranceK)
    }

    fun deleteWord(entranceId: Int){
        lessonRepositoryK.deleteWord(entranceId)
    }

    fun getAllLiveSampleLessons() : List<LessonDetailsK> {
        return lessonRepositoryK.getAllLiveSampleLessons()
    }

    fun getSampleLiveLesson(lessonId: Int): LessonDetailsK? {
        return lessonRepositoryK.getSampleLiveLesson(lessonId)
    }

    /** Remove support for this method.
     *
    fun getUpdatedEntrance(lessonEntrance: LessonEntranceK): LessonEntranceK? {
        return lessonRepositoryK.getUpdatedEntrance(lessonEntrance)
    }
    */

    fun getSampleLiveLesson(title: String): LessonDetailsK? {
        return lessonRepositoryK.getSampleLiveLesson(title)
    }

    fun getSampleWord(entranceId: Int): LessonEntranceK? {
        return lessonRepositoryK.getSampleWord(entranceId)
    }

}