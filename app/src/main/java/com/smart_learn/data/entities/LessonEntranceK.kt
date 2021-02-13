package com.smart_learn.data.entities

import com.smart_learn.data.models.LessonEntranceModelK

class LessonEntranceK(
    override var entranceId: Int = -1, // primary key is auto incremented in database
    override var word: String,
    override var translation: String,
    override var phonetic: String,
    override var lessonId: Int
) : LessonEntranceModelK(entranceId,word,translation,phonetic,lessonId) {

    // this indexes are used for search value in recycler view for making the foreground color
    var searchIndexes: List<IntRange> = ArrayList()

    // helper for recycler view
    var isSelected: Boolean = false

    /** primary key is not used for equality */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LessonEntranceK

        if (word != other.word) return false
        if (translation != other.translation) return false
        if (phonetic != other.phonetic) return false
        if (lessonId != other.lessonId) return false
        if (isSelected != other.isSelected) return false

        return true
    }

    /** primary key is not used for hash code */
    override fun hashCode(): Int {
        var result = word.hashCode()
        result = 31 * result + translation.hashCode()
        result = 31 * result + phonetic.hashCode()
        result = 31 * result + lessonId
        result = 31 * result + isSelected.hashCode()
        return result
    }

    override fun toString(): String {
        return "LessonEntranceK(entranceId=$entranceId, word='$word', translation='$translation'," +
                " phonetic='$phonetic', lessonId=$lessonId, isSelected=$isSelected)"
    }

}

