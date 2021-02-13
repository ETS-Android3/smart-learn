package com.smart_learn.core.services

import com.smart_learn.data.entities.DictionaryDetailsK
import com.smart_learn.data.entities.DictionaryEntranceK
import com.smart_learn.presenter.view_models.ActivityViewModelUtilitiesK
import com.smart_learn.core.general.SELECTED_DICTIONARY_ID
import com.smart_learn.data.repository.LessonRepositoryK


/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class LessonServiceK(private val activityViewModelUtilitiesK: ActivityViewModelUtilitiesK<*, *>) {

    private var lessonRepositoryK: LessonRepositoryK = LessonRepositoryK(activityViewModelUtilitiesK)

    fun getFullLiveLessonInfo() : List<DictionaryEntranceK> {
        return lessonRepositoryK.getFullLiveLessonInfo(SELECTED_DICTIONARY_ID)
    }

    fun getFullLiveLessonInfo(dictionaryId : Int) : List<DictionaryEntranceK> {
        return lessonRepositoryK.getFullLiveLessonInfo(dictionaryId)
    }

    fun checkIfLessonExist(dictionaryName: String) : Boolean {
        return lessonRepositoryK.checkIfLessonExist(dictionaryName)
    }

    fun checkIfWordExist(word: String, dictionaryId: Int) : Boolean {
        return lessonRepositoryK.checkIfWordExist(word,dictionaryId)
    }

    fun insert(dictionaryName: String){
        lessonRepositoryK.insert(dictionaryName)
    }

    fun update(dictionaryDetailsK: DictionaryDetailsK){
        lessonRepositoryK.update(dictionaryDetailsK)
    }

    fun delete(dictionaryId: Int){
        lessonRepositoryK.delete(dictionaryId)
    }

    fun insert(dictionaryEntranceK: DictionaryEntranceK) {
        lessonRepositoryK.insert(dictionaryEntranceK)
    }

    fun update(dictionaryEntranceK: DictionaryEntranceK){
        lessonRepositoryK.update(dictionaryEntranceK)
    }

    fun deleteWord(entranceId: Int){
        lessonRepositoryK.deleteWord(entranceId)
    }

    fun getAllLiveSampleLessons() : List<DictionaryDetailsK> {
        return lessonRepositoryK.getAllLiveSampleLessons()
    }

    fun getSampleLiveLesson(dictionaryId: Int): DictionaryDetailsK? {
        return lessonRepositoryK.getSampleLiveLesson(dictionaryId)
    }

    /** Remove support for this method.
     *
    fun getUpdatedEntrance(dictionaryEntrance: DictionaryEntranceK): DictionaryEntranceK? {
        return lessonRepositoryK.getUpdatedEntrance(dictionaryEntrance)
    }
    */

    fun getSampleLiveLesson(title: String): DictionaryDetailsK? {
        return lessonRepositoryK.getSampleLiveLesson(title)
    }

    fun getSampleWord(entranceId: Int): DictionaryEntranceK? {
        return lessonRepositoryK.getSampleWord(entranceId)
    }

}