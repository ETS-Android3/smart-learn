package com.smart_learn.data.repository

/**
 * INFO:
 *      - https://developer.android.com/training/data-storage/sqlite#kotlin
 */
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.smart_learn.core.general.DATABASE_ERROR
import com.smart_learn.core.general.DATABASE_SUCCESS
import com.smart_learn.core.general.DEBUG_MODE

/**  TODO: To check This class must be a singleton class
 *    https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 * */
class DatabaseHandler(private var activity: Activity) :
    SQLiteOpenHelper(activity, DATABASE_NAME,null, DATABASE_VERSION) {

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "memo_testing.db"
    }


    /** Is executed only once when the database is initially created
     * (when application is installed)
     * */
    override fun onCreate(db: SQLiteDatabase) {
        if(DEBUG_MODE) {
            Log.d(DATABASE_SUCCESS,"On create call in database handler")
        }
        createTables(db)
        addInitialData(db)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // TODO: add another sql for db.execSQL
        //db.execSQL("DROP TABLE IF EXISTS $EXAMPLE_DICTIONARY")
        onCreate(db)
    }


    /** Creates initial tables. Is executed only once when database is created */
    private fun createTables(db: SQLiteDatabase) {

        try {

            var query = "CREATE TABLE IF NOT EXISTS ${DatabaseSchema.DICTIONARIES_TABLE} (" +
                    "${DatabaseSchema.DictionariesTable.PRIMARY_KEY} INTEGER PRIMARY KEY," +
                    DatabaseSchema.DictionariesTable.COLUMN_DICTIONARY_NAME +
                    " VARCHAR(${DatabaseSchema.DictionariesTable.DIMENSION_COLUMN_NAME})" +
                    " NOT NULL UNIQUE);"

            db.execSQL(query)

            query = "CREATE TABLE IF NOT EXISTS ${DatabaseSchema.ENTRIES_TABLE} (" +
                    "${DatabaseSchema.EntriesTable.PRIMARY_KEY} INTEGER PRIMARY KEY," +
                    DatabaseSchema.EntriesTable.COLUMN_WORD +
                    " VARCHAR(${DatabaseSchema.EntriesTable.DIMENSION_COLUMN_WORD}) NOT NULL UNIQUE," +
                    "${DatabaseSchema.EntriesTable.COLUMN_TRANSLATION} TEXT NOT NULL," +
                    DatabaseSchema.EntriesTable.COLUMN_PHONETIC +
                    " VARCHAR(${DatabaseSchema.EntriesTable.DIMENSION_COLUMN_PHONETIC}) NOT NULL," +
                    "${DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY} INTEGER," +
                    " FOREIGN KEY (${DatabaseSchema.EntriesTable.FOREIGN_KEY_DICTIONARY})" +
                    " REFERENCES ${DatabaseSchema.DICTIONARIES_TABLE} (${DatabaseSchema.DictionariesTable.PRIMARY_KEY})" +
                    ");"

            db.execSQL(query)

            Log.i(DATABASE_SUCCESS,"Initial tables have been created")

        }
        catch (e: Exception){
            Log.e(DATABASE_ERROR, "Tables were NOT created [$e]")
        }
    }


    /** Add example data */
    private fun addInitialData(db: SQLiteDatabase) {

        try{
            val queries: List<String> = activity.assets.open("example_data.sql")
                .bufferedReader()
                .readText()
                .replace("\r","") // remove all new lines characters for windows
                .replace("\n","") // and linux
                .split(";") // split queries in single queries

            // TODO: when all /r/n are removed is possible to appear error on complex queries which are on multiple rows
            //  if you add multiple queries in file check this problem


            // only one query at a time can be executed
            // https://stackoverflow.com/questions/3805938/executing-multiple-statements-with-sqlitedatabase-execsql
            queries.forEach {
                // last line will be empty so ignore that
                if(it.isNotEmpty()) {
                    val query = "$it;" // add ';' to query because after split it disappear
                    println(query)
                    db.execSQL(query)
                }
            }

            Log.i(DATABASE_SUCCESS, "Example data were added")

        }
        catch (e: Exception){
            Log.e(DATABASE_ERROR, "Example data were NOT added [$e]")
        }
    }


    /** Executes a select query */
    @SuppressLint("Recycle")
    fun executeQuery(query: String) : Cursor? {

        val db = this.readableDatabase
        var cursor: Cursor?

        try{
            // TODO: check to see how to use query(...) and replace rawQuery(...)
            cursor = db?.rawQuery(query, null)

            if(DEBUG_MODE) {
                Log.d(DATABASE_SUCCESS, "Executed query [$query]")
            }
        } catch (e: SQLiteException) {
            cursor = null
            Log.e(DATABASE_ERROR, "ERROR in executeQuery [$e]")
            //db.execSQL(query)
        }

        return cursor
    }


    /** Executes a prepared insert query */
    fun executeInsert(table: String, values: ContentValues) {
        val db = this.writableDatabase
        val result = db.insert(table, null, values)

        if(result == -1L){
            Log.e(DATABASE_ERROR,"insertion was NOT made for [$values]")
            return
        }

        Log.i(DATABASE_SUCCESS,"Insertion was made for $values")
    }

    /** Executes a prepared update query */
    fun executeUpdate(table: String, values: ContentValues, selection: String, selectionArgs: Array<String>) {
        val db = this.writableDatabase
        val result = db.update(table,values,selection,selectionArgs)

        var printedValues: String = ""
        selectionArgs.forEach { printedValues = "$printedValues $it" }

        if(result == -1){
            Log.e(DATABASE_ERROR,
                "update was NOT made for values = [$values] ,selection = [$selection] and args = [$printedValues]"
            )
            return
        }

        Log.i(DATABASE_SUCCESS,"update was made for values = [$values] ,selection = [$selection] " +
                "and args = [$printedValues]")

    }

    /** Executes a prepared delete query */
    fun executeDelete(table: String, selection: String, selectionArgs: Array<String>) {
        val db = this.writableDatabase
        val result = db.delete(table,selection,selectionArgs)

        var printedValues: String = ""
        selectionArgs.forEach { printedValues = "$printedValues $it" }

        if(result < 0){
            Log.e(DATABASE_ERROR,"delete was NOT made for selection = [$selection] and" +
                    " args = [$printedValues]")
            return
        }

        if(result == 0){
            Log.e(DATABASE_SUCCESS,"deleted 0 rows for selection = [$selection] and" +
                    " args = [$printedValues]")
            return
        }

        Log.i(DATABASE_SUCCESS,"deleted $result rows for selection = [$selection] and" +
                " args = [$printedValues]")

    }
}