package com.smart_learn.core.services

import com.smart_learn.data.entities.DictionaryDetails
import com.smart_learn.data.entities.DictionaryEntrance
import com.smart_learn.core.general.ActivityServiceUtilities
import com.smart_learn.core.general.SELECTED_DICTIONARY_ID
import com.smart_learn.data.repository.LessonRepositoryK


/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class LessonServiceK(private val activityServiceUtilities: ActivityServiceUtilities<*,*>) {

    private var lessonRepositoryK: LessonRepositoryK = LessonRepositoryK(activityServiceUtilities)

    fun getFullLiveLessonInfo() : List<DictionaryEntrance> {
        return lessonRepositoryK.getFullLiveLessonInfo(SELECTED_DICTIONARY_ID)
    }

    fun getFullLiveLessonInfo(dictionaryId : Int) : List<DictionaryEntrance> {
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

    fun update(dictionaryDetails: DictionaryDetails){
        lessonRepositoryK.update(dictionaryDetails)
    }

    fun delete(dictionaryId: Int){
        lessonRepositoryK.delete(dictionaryId)
    }

    fun insert(dictionaryEntrance: DictionaryEntrance) {
        lessonRepositoryK.insert(dictionaryEntrance)
    }

    fun update(dictionaryEntrance: DictionaryEntrance){
        lessonRepositoryK.update(dictionaryEntrance)
    }

    fun deleteWord(entranceId: Int){
        lessonRepositoryK.deleteWord(entranceId)
    }

    fun getAllLiveSampleLessons() : List<DictionaryDetails> {
        return lessonRepositoryK.getAllLiveSampleLessons()
    }

    fun getSampleLiveLesson(dictionaryId: Int): DictionaryDetails? {
        return lessonRepositoryK.getSampleLiveLesson(dictionaryId)
    }

    fun getUpdatedEntrance(dictionaryEntrance: DictionaryEntrance): DictionaryEntrance? {
        return lessonRepositoryK.getUpdatedEntrance(dictionaryEntrance)
    }

    fun getSampleLiveLesson(title: String): DictionaryDetails? {
        return lessonRepositoryK.getSampleLiveLesson(title)
    }

    fun getSampleWord(entranceId: Int): DictionaryEntrance? {
        return lessonRepositoryK.getSampleWord(entranceId)
    }

}