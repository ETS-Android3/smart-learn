package com.smart_learn.entities

import com.smart_learn.models.DictionaryDetailsModel

class DictionaryDetails(
    override var dictionaryId: Int = -1,
    override var title: String
) : DictionaryDetailsModel(dictionaryId, title) {

    // this indexes are used for search value in recycler view for making the foreground color
    var searchIndexes: List<IntRange> = ArrayList()

    // helper for recycler view
    var isSelected: Boolean = false

    override fun toString(): String {
        return "DictionaryDetails(dictionaryId=$dictionaryId, title='$title')"
    }
}