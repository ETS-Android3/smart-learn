package com.smart_learn.data.models


/**
 * Helper for DictionaryEntrance entity
 *
 * this is what is stored in the database
 * */
abstract class DictionaryEntranceModel(
    open var entranceId: Int,  // primary key is auto incremented in database
    open var word: String,
    open var translation: String,
    open var phonetic: String,
    open var dictionaryId: Int
)