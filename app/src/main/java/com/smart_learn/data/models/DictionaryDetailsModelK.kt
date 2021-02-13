package com.smart_learn.data.models

/**
 * Helper for recyclerView for dictionary details
 * */
abstract class DictionaryDetailsModelK(
    open var dictionaryId: Int, // primary key is auto incremented in database
    open var title: String
)
