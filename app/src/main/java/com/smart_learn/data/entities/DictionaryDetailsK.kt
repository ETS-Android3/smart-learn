package com.smart_learn.data.entities

import com.smart_learn.data.models.DictionaryDetailsModelK

class DictionaryDetailsK(
    override var dictionaryId: Int = -1,
    override var title: String
) : DictionaryDetailsModelK(dictionaryId, title) {

    // this indexes are used for search value in recycler view for making the foreground color
    var searchIndexes: List<IntRange> = ArrayList()

    // helper for recycler view
    var isSelected: Boolean = false

    override fun toString(): String {
        return "DictionaryDetailsK(dictionaryId=$dictionaryId, title='$title')"
    }
}