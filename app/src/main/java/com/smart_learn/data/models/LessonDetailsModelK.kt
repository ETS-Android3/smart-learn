package com.smart_learn.data.models

/**
 * Helper for recyclerView for lesson details
 * */
abstract class LessonDetailsModelK(
    open var lessonId: Int, // primary key is auto incremented in database
    open var title: String
)
