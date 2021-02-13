package com.smart_learn.data.entities

import com.smart_learn.data.models.LessonDetailsModelK

class LessonDetailsK(
    override var lessonId: Int = -1,
    override var title: String
) : LessonDetailsModelK(lessonId, title) {

    // this indexes are used for search value in recycler view for making the foreground color
    var searchIndexes: List<IntRange> = ArrayList()

    // helper for recycler view
    var isSelected: Boolean = false

    override fun toString(): String {
        return "LessonDetailsK(lessonId=$lessonId, title='$title')"
    }
}