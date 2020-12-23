package com.smart_learn.services

import com.smart_learn.entities.DictionaryDetails
import com.smart_learn.entities.DictionaryEntrance
import com.smart_learn.general.ActivityServiceUtilities
import com.smart_learn.general.SELECTED_DICTIONARY_ID
import com.smart_learn.repository.DictionaryRepository


/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class DictionaryService(private val activityServiceUtilities: ActivityServiceUtilities<*,*>) {

    private var dictionaryRepository: DictionaryRepository = DictionaryRepository(activityServiceUtilities)

    fun getDictionaryEntries() : List<DictionaryEntrance> {
        return dictionaryRepository.getDictionaryEntries(SELECTED_DICTIONARY_ID)
    }

    fun getDictionaryEntries(dictionaryId : Int) : List<DictionaryEntrance> {
        return dictionaryRepository.getDictionaryEntries(dictionaryId)
    }

    fun checkIfDictionaryExist(dictionaryName: String) : Boolean {
        return dictionaryRepository.checkIfDictionaryExist(dictionaryName)
    }

    fun checkIfWordExist(word: String, dictionaryId: Int) : Boolean {
        return dictionaryRepository.checkIfWordExist(word,dictionaryId)
    }

    fun addDictionary(dictionaryName: String){
        dictionaryRepository.addDictionary(dictionaryName)
    }

    fun updateDictionary(dictionaryDetails: DictionaryDetails){
        dictionaryRepository.updateDictionary(dictionaryDetails)
    }

    fun deleteDictionary(dictionaryId: Int){
        dictionaryRepository.deleteDictionary(dictionaryId)
    }

    fun addEntrance(dictionaryEntrance: DictionaryEntrance) {
        dictionaryRepository.addEntrance(dictionaryEntrance)
    }

    fun updateEntrance(dictionaryEntrance: DictionaryEntrance){
        dictionaryRepository.updateEntrance(dictionaryEntrance)
    }

    fun deleteEntrance(entranceId: Int){
        dictionaryRepository.deleteEntrance(entranceId)
    }

    fun getDictionaries() : List<DictionaryDetails> {
        return dictionaryRepository.getDictionaries()
    }

    fun getDictionary(dictionaryId: Int): DictionaryDetails? {
        return dictionaryRepository.getDictionary(dictionaryId)
    }

    fun getUpdatedEntrance(dictionaryEntrance: DictionaryEntrance): DictionaryEntrance? {
        return dictionaryRepository.getUpdatedEntrance(dictionaryEntrance)
    }

    fun getDictionary(title: String): DictionaryDetails? {
        return dictionaryRepository.getDictionary(title)
    }

    fun getEntrance(entranceId: Int): DictionaryEntrance? {
        return dictionaryRepository.getEntrance(entranceId)
    }

}