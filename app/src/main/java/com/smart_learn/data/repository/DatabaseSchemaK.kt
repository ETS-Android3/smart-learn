package com.smart_learn.data.repository

/**
 * This is how database look
 *
 * IMPORTANT: When you change the schema change models too
 * */

object DatabaseSchemaK {

    const val LESSONS_TABLE = "lessons"
    const val ENTRIES_TABLE = "entries"

    object LessonsTable{
        const val PRIMARY_KEY = "pk_lesson_id"
        const val COLUMN_LESSON_NAME = "lesson_name"
        const val DIMENSION_COLUMN_NAME = 64
    }

    object EntriesTable{
        const val PRIMARY_KEY = "pk_entry_id"
        const val FOREIGN_KEY_LESSON = "fk_lesson_id" /* references PRIMARY_KEY from LessonsTable*/
        const val COLUMN_WORD = "word"
        const val COLUMN_TRANSLATION = "translation" // this column does not have a limit
        const val COLUMN_PHONETIC = "phonetic"
        const val DIMENSION_COLUMN_WORD = 255
        const val DIMENSION_COLUMN_PHONETIC = 255
    }
}