package com.smart_learn.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.smart_learn.data.entities.DictionaryDetailsK
import com.smart_learn.data.entities.DictionaryEntranceK
import com.smart_learn.presenter.view_models.ActivityViewModelUtilitiesK

/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class LessonRepositoryK(private val activityViewModelUtilitiesK: ActivityViewModelUtilitiesK<*, *>) {

    private val databaseHandlerK: DatabaseHandlerK = DatabaseHandlerK(activityViewModelUtilitiesK.getActivity())

    /** check if dictionary already exists in database */
    fun checkIfLessonExist(dictionaryName: String) : Boolean {

        val query = "SELECT * FROM ${DatabaseSchemaK.DICTIONARIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.DictionariesTable.COLUMN_DICTIONARY_NAME} LIKE '${dictionaryName}';"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor == null || !cursor.moveToFirst()) {
            cursor?.close()
            return false
        }

        cursor.close()
        return true
    }


    /** check if word already exists in specific dictionary */
    fun checkIfWordExist(word: String, dictionaryId: Int) : Boolean {

        val query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY} = $dictionaryId" +
                " AND ${DatabaseSchemaK.EntriesTable.COLUMN_WORD} LIKE '${word}';"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

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
        contentValues.put(DatabaseSchemaK.DictionariesTable.COLUMN_DICTIONARY_NAME, dictionaryName)
        databaseHandlerK.executeInsert(DatabaseSchemaK.DICTIONARIES_TABLE,contentValues)
    }

    /** update lesson */
    fun update(dictionaryDetailsK: DictionaryDetailsK) {
        val contentValues = ContentValues()
        contentValues.put(DatabaseSchemaK.DictionariesTable.COLUMN_DICTIONARY_NAME, dictionaryDetailsK.title)

        val selection = "${DatabaseSchemaK.DictionariesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(dictionaryDetailsK.dictionaryId.toString())

        databaseHandlerK.executeUpdate(DatabaseSchemaK.DICTIONARIES_TABLE,contentValues,selection,selectionArgs)
    }

    /** delete lesson */
    fun delete(dictionaryId: Int){
        // delete all his words
        var selection = "${DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY} = ?"
        var selectionArgs = arrayOf(dictionaryId.toString())
        databaseHandlerK.executeDelete(DatabaseSchemaK.ENTRIES_TABLE,selection,selectionArgs)

        // delete dictionary
        selection = "${DatabaseSchemaK.DictionariesTable.PRIMARY_KEY} = ?"
        selectionArgs = arrayOf(dictionaryId.toString())
        databaseHandlerK.executeDelete(DatabaseSchemaK.DICTIONARIES_TABLE,selection,selectionArgs)
    }


    /** Remove support for this method.
     *
    fun getUpdatedEntrance(dictionaryEntrance: DictionaryEntranceK): DictionaryEntranceK? {

        var query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}"
        var done: Boolean = false

        if(dictionaryEntrance.entranceId > 0 && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = ${dictionaryEntrance.entranceId};"
            done = true
        }

        if(dictionaryEntrance.word != "" && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.COLUMN_WORD} LIKE '${dictionaryEntrance.word}';"
            done = true
        }

        if(dictionaryEntrance.translation != "" && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION} LIKE '${dictionaryEntrance.translation}';"
            done = true
        }

        if(dictionaryEntrance.phonetic != ""  && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC} LIKE '${dictionaryEntrance.phonetic}';"
            done = true
        }

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            val entrance =
                DictionaryEntranceK(
                    cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.PRIMARY_KEY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_WORD)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY))
                )

            cursor.close()

            return entrance
        }

        return null

    }
    */


    /** insert word */
    fun insert(dictionaryEntranceK: DictionaryEntranceK) {
        val contentValues = ContentValues()

        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_WORD, dictionaryEntranceK.word)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION, dictionaryEntranceK.translation)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC, dictionaryEntranceK.phonetic)
        contentValues.put(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY, dictionaryEntranceK.dictionaryId)

        databaseHandlerK.executeInsert(DatabaseSchemaK.ENTRIES_TABLE,contentValues)
    }


    fun getSampleWord(entranceId: Int): DictionaryEntranceK? {

        val entranceK: DictionaryEntranceK
        val query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = $entranceId;"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            entranceK =  DictionaryEntranceK(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_WORD)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC)),
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY))
            )

            cursor.close()

            return entranceK
        }

        return null

    }

    /** update word */
    fun update(dictionaryEntranceK: DictionaryEntranceK) {
        val contentValues = ContentValues()

        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_WORD, dictionaryEntranceK.word)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION, dictionaryEntranceK.translation)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC, dictionaryEntranceK.phonetic)
        contentValues.put(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY, dictionaryEntranceK.dictionaryId)

        val selection = "${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(dictionaryEntranceK.entranceId.toString())

        databaseHandlerK.executeUpdate(DatabaseSchemaK.ENTRIES_TABLE,contentValues,selection,selectionArgs)
    }

    /** delete word */
    fun deleteWord(entranceId: Int){

        val selection = "${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(entranceId.toString())

        databaseHandlerK.executeDelete(DatabaseSchemaK.ENTRIES_TABLE,selection,selectionArgs)
    }

    /** get all entries for a specific dictionary */
    fun getFullLiveLessonInfo(dictionaryId : Int) : List<DictionaryEntranceK> {

        val entriesList = ArrayList<DictionaryEntranceK>()
        val query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY} = ${dictionaryId};"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            while (cursor.moveToNext()){
                entriesList.add(
                    DictionaryEntranceK(
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.PRIMARY_KEY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_WORD)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_DICTIONARY))
                    )
                )
            }

            cursor.close()
        }

        return entriesList
    }


    /** get a list of all dictionaries based on DictionaryDetailsK */
    fun getAllLiveSampleLessons() : List<DictionaryDetailsK> {

        val dictionaryList = ArrayList<DictionaryDetailsK>()
        val query = "SELECT * FROM ${DatabaseSchemaK.DICTIONARIES_TABLE};"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            while (cursor.moveToNext()){
                dictionaryList.add(
                    DictionaryDetailsK(
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.DictionariesTable.PRIMARY_KEY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.DictionariesTable.COLUMN_DICTIONARY_NAME))
                    )
                )
            }

            cursor.close()
        }

        return dictionaryList
    }

    fun getSampleLiveLesson(dictionaryId: Int): DictionaryDetailsK? {

        val dictionaryK: DictionaryDetailsK
        val query = "SELECT * FROM ${DatabaseSchemaK.DICTIONARIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.DictionariesTable.PRIMARY_KEY} = $dictionaryId;"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            dictionaryK =  DictionaryDetailsK(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.DictionariesTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.DictionariesTable.COLUMN_DICTIONARY_NAME))
            )

            cursor.close()

            return dictionaryK
        }

        return null

    }

    fun getSampleLiveLesson(title: String): DictionaryDetailsK? {

        val dictionaryK: DictionaryDetailsK
        val query = "SELECT * FROM ${DatabaseSchemaK.DICTIONARIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.DictionariesTable.COLUMN_DICTIONARY_NAME} LIKE '$title';"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            dictionaryK =  DictionaryDetailsK(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.DictionariesTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.DictionariesTable.COLUMN_DICTIONARY_NAME))
            )

            cursor.close()

            return dictionaryK
        }

        return null

    }
}