package com.licenta.smart_learn.repository

/**
 * This is how database look
 *
 * IMPORTANT: When you change the schema change models too
 * */

object DatabaseSchema {

    const val DICTIONARIES_TABLE = "dictionaries"
    const val ENTRIES_TABLE = "entries"

    object DictionariesTable{
        const val PRIMARY_KEY = "pk_dictionary_id"
        const val COLUMN_DICTIONARY_NAME = "dictionary_name"
        const val DIMENSION_COLUMN_NAME = 64
    }

    object EntriesTable{
        const val PRIMARY_KEY = "pk_entry_id"
        const val FOREIGN_KEY_DICTIONARY = "fk_dictionary_id" /* references PRIMARY_KEY from DictionariesTable*/
        const val COLUMN_WORD = "word"
        const val COLUMN_TRANSLATION = "translation" // this column does not have a limit
        const val COLUMN_PHONETIC = "phonetic"
        const val DIMENSION_COLUMN_WORD = 255
        const val DIMENSION_COLUMN_PHONETIC = 255
    }
}