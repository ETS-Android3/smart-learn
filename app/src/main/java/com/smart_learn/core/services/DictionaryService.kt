package com.smart_learn.core.services

import com.smart_learn.entities.DictionaryDetails
import com.smart_learn.entities.DictionaryEntrance
import com.smart_learn.core.general.ActivityServiceUtilities
import com.smart_learn.core.general.SELECTED_DICTIONARY_ID
import com.smart_learn.repository.LessonRepositoryK


/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class DictionaryService(private val activityServiceUtilities: ActivityServiceUtilities<*,*>) {

    private var lessonRepositoryK: LessonRepositoryK = LessonRepositoryK(activityServiceUtilities)

    fun getDictionaryEntries() : List<DictionaryEntrance> {
        return lessonRepositoryK.getDictionaryEntries(SELECTED_DICTIONARY_ID)
    }

    fun getDictionaryEntries(dictionaryId : Int) : List<DictionaryEntrance> {
        return lessonRepositoryK.getDictionaryEntries(dictionaryId)
    }

    fun checkIfDictionaryExist(dictionaryName: String) : Boolean {
        return lessonRepositoryK.checkIfDictionaryExist(dictionaryName)
    }

    fun checkIfWordExist(word: String, dictionaryId: Int) : Boolean {
        return lessonRepositoryK.checkIfWordExist(word,dictionaryId)
    }

    fun addDictionary(dictionaryName: String){
        lessonRepositoryK.addDictionary(dictionaryName)
    }

    fun updateDictionary(dictionaryDetails: DictionaryDetails){
        lessonRepositoryK.updateDictionary(dictionaryDetails)
    }

    fun deleteDictionary(dictionaryId: Int){
        lessonRepositoryK.deleteDictionary(dictionaryId)
    }

    fun addEntrance(dictionaryEntrance: DictionaryEntrance) {
        lessonRepositoryK.addEntrance(dictionaryEntrance)
    }

    fun updateEntrance(dictionaryEntrance: DictionaryEntrance){
        lessonRepositoryK.updateEntrance(dictionaryEntrance)
    }

    fun deleteEntrance(entranceId: Int){
        lessonRepositoryK.deleteEntrance(entranceId)
    }

    fun getDictionaries() : List<DictionaryDetails> {
        return lessonRepositoryK.getDictionaries()
    }

    fun getDictionary(dictionaryId: Int): DictionaryDetails? {
        return lessonRepositoryK.getDictionary(dictionaryId)
    }

    fun getUpdatedEntrance(dictionaryEntrance: DictionaryEntrance): DictionaryEntrance? {
        return lessonRepositoryK.getUpdatedEntrance(dictionaryEntrance)
    }

    fun getDictionary(title: String): DictionaryDetails? {
        return lessonRepositoryK.getDictionary(title)
    }

    fun getEntrance(entranceId: Int): DictionaryEntrance? {
        return lessonRepositoryK.getEntrance(entranceId)
    }

}