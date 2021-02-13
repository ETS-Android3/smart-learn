package com.smart_learn.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.smart_learn.data.entities.LessonDetailsK
import com.smart_learn.data.entities.LessonEntranceK
import com.smart_learn.presenter.view_models.ActivityViewModelUtilitiesK

/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class LessonRepositoryK(private val activityViewModelUtilitiesK: ActivityViewModelUtilitiesK<*, *>) {

    private val databaseHandlerK: DatabaseHandlerK = DatabaseHandlerK(activityViewModelUtilitiesK.getActivity())

    /** check if lesson already exists in database */
    fun checkIfLessonExist(lessonName: String) : Boolean {

        val query = "SELECT * FROM ${DatabaseSchemaK.LESSONS_TABLE}" +
                " WHERE ${DatabaseSchemaK.LessonsTable.COLUMN_LESSON_NAME} LIKE '${lessonName}';"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor == null || !cursor.moveToFirst()) {
            cursor?.close()
            return false
        }

        cursor.close()
        return true
    }


    /** check if word already exists in specific lesson */
    fun checkIfWordExist(word: String, lessonId: Int) : Boolean {

        val query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON} = $lessonId" +
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
    fun insert(lessonName: String) {
        val contentValues = ContentValues()
        contentValues.put(DatabaseSchemaK.LessonsTable.COLUMN_LESSON_NAME, lessonName)
        databaseHandlerK.executeInsert(DatabaseSchemaK.LESSONS_TABLE,contentValues)
    }

    /** update lesson */
    fun update(lessonDetailsK: LessonDetailsK) {
        val contentValues = ContentValues()
        contentValues.put(DatabaseSchemaK.LessonsTable.COLUMN_LESSON_NAME, lessonDetailsK.title)

        val selection = "${DatabaseSchemaK.LessonsTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(lessonDetailsK.lessonId.toString())

        databaseHandlerK.executeUpdate(DatabaseSchemaK.LESSONS_TABLE,contentValues,selection,selectionArgs)
    }

    /** delete lesson */
    fun delete(lessonId: Int){
        // delete all his words
        var selection = "${DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON} = ?"
        var selectionArgs = arrayOf(lessonId.toString())
        databaseHandlerK.executeDelete(DatabaseSchemaK.ENTRIES_TABLE,selection,selectionArgs)

        // delete lesson
        selection = "${DatabaseSchemaK.LessonsTable.PRIMARY_KEY} = ?"
        selectionArgs = arrayOf(lessonId.toString())
        databaseHandlerK.executeDelete(DatabaseSchemaK.LESSONS_TABLE,selection,selectionArgs)
    }


    /** Remove support for this method.

    fun getUpdatedEntrance(lessonEntranceK: LessonEntranceK): LessonEntranceK? {

        var query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}"
        var done: Boolean = false

        if(lessonEntranceK.entranceId > 0 && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = ${lessonEntranceK.entranceId};"
            done = true
        }

        if(lessonEntranceK.word != "" && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.COLUMN_WORD} LIKE '${lessonEntranceK.word}';"
            done = true
        }

        if(lessonEntranceK.translation != "" && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION} LIKE '${lessonEntranceK.translation}';"
            done = true
        }

        if(lessonEntranceK.phonetic != ""  && !done){
            query += " WHERE ${DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC} LIKE '${lessonEntranceK.phonetic}';"
            done = true
        }

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            val entrance =
                LessonEntranceK(
                    cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.PRIMARY_KEY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_WORD)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION)),
                    cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON))
                )

            cursor.close()

            return entrance
        }

        return null

    }
    */


    /** insert word */
    fun insert(lessonEntranceK: LessonEntranceK) {
        val contentValues = ContentValues()

        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_WORD, lessonEntranceK.word)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION, lessonEntranceK.translation)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC, lessonEntranceK.phonetic)
        contentValues.put(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON, lessonEntranceK.lessonId)

        databaseHandlerK.executeInsert(DatabaseSchemaK.ENTRIES_TABLE,contentValues)
    }


    fun getSampleWord(entranceId: Int): LessonEntranceK? {

        val entranceK: LessonEntranceK
        val query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = $entranceId;"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            entranceK =  LessonEntranceK(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_WORD)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC)),
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON))
            )

            cursor.close()

            return entranceK
        }

        return null

    }

    /** update word */
    fun update(lessonEntranceK: LessonEntranceK) {
        val contentValues = ContentValues()

        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_WORD, lessonEntranceK.word)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION, lessonEntranceK.translation)
        contentValues.put(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC, lessonEntranceK.phonetic)
        contentValues.put(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON, lessonEntranceK.lessonId)

        val selection = "${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(lessonEntranceK.entranceId.toString())

        databaseHandlerK.executeUpdate(DatabaseSchemaK.ENTRIES_TABLE,contentValues,selection,selectionArgs)
    }

    /** delete word */
    fun deleteWord(entranceId: Int){

        val selection = "${DatabaseSchemaK.EntriesTable.PRIMARY_KEY} = ?"
        val selectionArgs = arrayOf(entranceId.toString())

        databaseHandlerK.executeDelete(DatabaseSchemaK.ENTRIES_TABLE,selection,selectionArgs)
    }

    /** get all entries for a specific lesson */
    fun getFullLiveLessonInfo(lessonId : Int) : List<LessonEntranceK> {

        val entriesList = ArrayList<LessonEntranceK>()
        val query = "SELECT * FROM ${DatabaseSchemaK.ENTRIES_TABLE}" +
                " WHERE ${DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON} = ${lessonId};"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            while (cursor.moveToNext()){
                entriesList.add(
                    LessonEntranceK(
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.PRIMARY_KEY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_WORD)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_TRANSLATION)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.COLUMN_PHONETIC)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.EntriesTable.FOREIGN_KEY_LESSON))
                    )
                )
            }

            cursor.close()
        }

        return entriesList
    }


    /** get a list of all lessons based on LessonDetailsK */
    fun getAllLiveSampleLessons() : List<LessonDetailsK> {

        val lessonList = ArrayList<LessonDetailsK>()
        val query = "SELECT * FROM ${DatabaseSchemaK.LESSONS_TABLE};"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            while (cursor.moveToNext()){
                lessonList.add(
                    LessonDetailsK(
                        cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.LessonsTable.PRIMARY_KEY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.LessonsTable.COLUMN_LESSON_NAME))
                    )
                )
            }

            cursor.close()
        }

        return lessonList
    }

    fun getSampleLiveLesson(lessonId: Int): LessonDetailsK? {

        val lessonK: LessonDetailsK
        val query = "SELECT * FROM ${DatabaseSchemaK.LESSONS_TABLE}" +
                " WHERE ${DatabaseSchemaK.LessonsTable.PRIMARY_KEY} = $lessonId;"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            lessonK =  LessonDetailsK(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.LessonsTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.LessonsTable.COLUMN_LESSON_NAME))
            )

            cursor.close()

            return lessonK
        }

        return null

    }

    fun getSampleLiveLesson(title: String): LessonDetailsK? {

        val lessonK: LessonDetailsK
        val query = "SELECT * FROM ${DatabaseSchemaK.LESSONS_TABLE}" +
                " WHERE ${DatabaseSchemaK.LessonsTable.COLUMN_LESSON_NAME} LIKE '$title';"

        val cursor: Cursor? = databaseHandlerK.executeQuery(query)

        if (cursor != null) {
            cursor.moveToFirst()
            lessonK =  LessonDetailsK(
                cursor.getInt(cursor.getColumnIndex(DatabaseSchemaK.LessonsTable.PRIMARY_KEY)),
                cursor.getString(cursor.getColumnIndex(DatabaseSchemaK.LessonsTable.COLUMN_LESSON_NAME))
            )

            cursor.close()

            return lessonK
        }

        return null

    }
}