package com.licenta.smart_learn.models

/**
 * Helper for recyclerView for dictionary details
 * */
abstract class DictionaryDetailsModel(
    open var dictionaryId: Int, // primary key is auto incremented in database
    open var title: String
)
