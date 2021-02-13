package com.smart_learn.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.smart_learn.data.entities.DictionaryDetails
import com.smart_learn.data.entities.DictionaryEntrance
import com.smart_learn.core.general.ActivityServiceUtilities

/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class LessonRepositoryK(private val activityServiceUtilities: ActivityServiceUtilities<*, *>) {

    private val databaseHandler: DatabaseHandler = DatabaseHandler(activityServiceUtilities.getActivity())

    /** check if dictionary already exists in database */
    fun checkIfLessonExist(dictionaryName: String) : Boolean {

        val query = "SELECT * FROM ${DatabaseSchema.DICTIONARIES_TABLE}" +
                " WHERE ${DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME} LIKE '${dictionaryName}';"

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor == null || !cursor.moveToFirst()) {
            cursor?.close()
            return false
        }

        cursor.close()
        return true
    }


    /** check if word already exists in specific dictionary */
    fun checkIfWordExist(word: String, dictionaryId: Int) : Boolean {

        val query = "SELECT * FROM ${DatabaseSchema.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY} = $dictionaryId" +
                " AND ${DatabaseSchema.EntriesTable.COLUMN_WORD} LIKE '${word}';"

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor == null || !cursor.moveToFirst()) {
            cursor?.close()
            return false
        }

        cursor.close()

        return true
    }

    /** insert lesson */
    fun insert(dictionaryName: String) {
        val contentValues = ContentValues()
        contentValues.put(DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME, dictionaryName)
        databaseHandler.executeInsert(DatabaseSchema.DICTIONARIES_TABLE,contentValues)
    }

    /** update lesson */
    fun update(dictionaryDetails: DictionaryDetails) {
        val contentValues = ContentValues()
        contentValues.put(DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME, dictionaryDetails.title)

        val selection = "${DatabaseSchema.DictionariesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(dictionaryDetails.dictionaryId.toString())

        databaseHandler.executeUpdate(DatabaseSchema.DICTIONARIES_TABLE,contentValues,selection,selectionArgs)
    }

    /** delete lesson */
    fun delete(dictionaryId: Int){
        // delete all his words
        var selection = "${DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY} = ?"
        var selectionArgs = arrayOf(dictionaryId.toString())
        databaseHandler.executeDelete(DatabaseSchema.ENTRIES_TABLE,selection,selectionArgs)

        // delete dictionary
        selection = "${DatabaseSchema.DictionariesTable.PRIMARY_KEY} = ?"
        selectionArgs = arrayOf(dictionaryId.toString())
        databaseHandler.executeDelete(DatabaseSchema.DICTIONARIES_TABLE,selection,selectionArgs)
    }


    /** Remove support for this method.
     *
    fun getUpdatedEntrance(dictionaryEntrance: DictionaryEntrance): DictionaryEntrance? {

        var query = "SELECT * FROM ${DatabaseSchema.ENTRIES_TABLE}"
        var done: Boolean = false

        if(dictionaryEntrance.entranceId > 0 && !done){
            query += " WHERE ${DatabaseSchema.EntriesTable.PRIMARY_KEY} = ${dictionaryEntrance.entranceId};"
            done = true
        }

        if(dictionaryEntrance.word != "" && !done){
            query += " WHERE ${DatabaseSchema.EntriesTable.COLUMN_WORD} LIKE '${dictionaryEntrance.word}';"
            done = true
        }

        if(dictionaryEntrance.translation != "" && !done){
            query += " WHERE ${DatabaseSchema.EntriesTable.COLUMN_TRANSLATION} LIKE '${dictionaryEntrance.translation}';"
            done = true
        }

        if(dictionaryEntrance.phonetic != ""  && !done){
            query += " WHERE ${DatabaseSchema.EntriesTable.COLUMN_PHONETIC} LIKE '${dictionaryEntrance.phonetic}';"
            done = true
        }

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            val entrance =
                DictionaryEntrance(
                    cursor.getInt(cursor.getColumnIndex(DatabaseSchema.EntriesTable.PRIMARY_KEY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_WORD)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_TRANSLATION)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_PHONETIC)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY))
                )

            cursor.close()

            return entrance
        }

        return null

    }
    */


    /** insert word */
    fun insert(dictionaryEntrance: DictionaryEntrance) {
        val contentValues = ContentValues()

        contentValues.put(DatabaseSchema.EntriesTable.COLUMN_WORD, dictionaryEntrance.word)
        contentValues.put(DatabaseSchema.EntriesTable.COLUMN_TRANSLATION, dictionaryEntrance.translation)
        contentValues.put(DatabaseSchema.EntriesTable.COLUMN_PHONETIC, dictionaryEntrance.phonetic)
        contentValues.put(DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY, dictionaryEntrance.dictionaryId)

        databaseHandler.executeInsert(DatabaseSchema.ENTRIES_TABLE,contentValues)
    }


    fun getSampleWord(entranceId: Int): DictionaryEntrance? {

        val entrance: DictionaryEntrance
        val query = "SELECT * FROM ${DatabaseSchema.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchema.EntriesTable.PRIMARY_KEY} = $entranceId;"

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            entrance =  DictionaryEntrance(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchema.EntriesTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_WORD)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_TRANSLATION)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_PHONETIC)),
                cursor.getInt(cursor.getColumnIndex(DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY))
            )

            cursor.close()

            return entrance
        }

        return null

    }

    /** update word */
    fun update(dictionaryEntrance: DictionaryEntrance) {
        val contentValues = ContentValues()

        contentValues.put(DatabaseSchema.EntriesTable.COLUMN_WORD, dictionaryEntrance.word)
        contentValues.put(DatabaseSchema.EntriesTable.COLUMN_TRANSLATION, dictionaryEntrance.translation)
        contentValues.put(DatabaseSchema.EntriesTable.COLUMN_PHONETIC, dictionaryEntrance.phonetic)
        contentValues.put(DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY, dictionaryEntrance.dictionaryId)

        val selection = "${DatabaseSchema.EntriesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(dictionaryEntrance.entranceId.toString())

        databaseHandler.executeUpdate(DatabaseSchema.ENTRIES_TABLE,contentValues,selection,selectionArgs)
    }

    /** delete word */
    fun deleteWord(entranceId: Int){

        val selection = "${DatabaseSchema.EntriesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(entranceId.toString())

        databaseHandler.executeDelete(DatabaseSchema.ENTRIES_TABLE,selection,selectionArgs)
    }

    /** get all entries for a specific dictionary */
    fun getFullLiveLessonInfo(dictionaryId : Int) : List<DictionaryEntrance> {

        val entriesList = ArrayList<DictionaryEntrance>()
        val query = "SELECT * FROM ${DatabaseSchema.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY} = ${dictionaryId};"

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor != null) {
            while (cursor.moveToNext()){
                entriesList.add(
                    DictionaryEntrance(
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchema.EntriesTable.PRIMARY_KEY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_WORD)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_TRANSLATION)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchema.EntriesTable.COLUMN_PHONETIC)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY))
                    )
                )
            }

            cursor.close()
        }

        return entriesList
    }


    /** get a list of all dictionaries based on DictionaryDetails */
    fun getAllLiveSampleLessons() : List<DictionaryDetails> {

        val dictionaryList = ArrayList<DictionaryDetails>()
        val query = "SELECT * FROM ${DatabaseSchema.DICTIONARIES_TABLE};"

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor != null) {
            while (cursor.moveToNext()){
                dictionaryList.add(
                    DictionaryDetails(
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchema.DictionariesTable.PRIMARY_KEY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME))
                    )
                )
            }

            cursor.close()
        }

        return dictionaryList
    }

    fun getSampleLiveLesson(dictionaryId: Int): DictionaryDetails? {

        val dictionary: DictionaryDetails
        val query = "SELECT * FROM ${DatabaseSchema.DICTIONARIES_TABLE}" +
                " WHERE ${DatabaseSchema.DictionariesTable.PRIMARY_KEY} = $dictionaryId;"

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            dictionary =  DictionaryDetails(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchema.DictionariesTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME))
            )

            cursor.close()

            return dictionary
        }

        return null

    }

    fun getSampleLiveLesson(title: String): DictionaryDetails? {

        val dictionary: DictionaryDetails
        val query = "SELECT * FROM ${DatabaseSchema.DICTIONARIES_TABLE}" +
                " WHERE ${DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME} LIKE '$title';"

        val cursor: Cursor? = databaseHandler.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            dictionary =  DictionaryDetails(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchema.DictionariesTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME))
            )

            cursor.close()

            return dictionary
        }

        return null

    }
}