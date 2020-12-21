package com.licenta.smart_learn.services

import com.licenta.smart_learn.entities.DictionaryDetails
import com.licenta.smart_learn.entities.DictionaryEntrance
import com.licenta.smart_learn.general.ActivityServiceUtilities


/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class DictionaryService(private val activityServiceUtilities: ActivityServiceUtilities<*, *>) {

    fun addDictionary(dictionaryName: String){

    }

    fun updateDictionary(dictionaryDetails: DictionaryDetails){

    }

    fun getDictionary(title: String): DictionaryDetails? {
        return DictionaryDetails(title="test")
    }

    fun deleteDictionary(dictionaryId: Int){

    }

    fun getDictionaries() : List<DictionaryDetails> {
        return ArrayList()
    }

    fun getDictionaryEntries(dictionaryId : Int) : List<DictionaryEntrance> {
        return ArrayList()
    }

    fun getDictionary(dictionaryId: Int): DictionaryDetails? {
        return DictionaryDetails(title="test")
    }

    fun deleteEntrance(entranceId: Int){

    }

    fun updateEntrance(dictionaryEntrance: DictionaryEntrance){

    }

    fun getUpdatedEntrance(dictionaryEntrance: DictionaryEntrance): DictionaryEntrance? {
        return DictionaryEntrance(word = "test",translation = "test",phonetic = "test",dictionaryId = 1)
    }

    fun addEntrance(dictionaryEntrance: DictionaryEntrance) {

    }
}