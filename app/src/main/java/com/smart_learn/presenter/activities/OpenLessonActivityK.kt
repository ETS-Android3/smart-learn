package com.smart_learn.presenter.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.smart_learn.R
import com.smart_learn.core.general.showSettingsDialog
import com.smart_learn.presenter.view_models.OpenLessonViewModelK

class OpenLessonActivityK : AppCompatActivity() {

    private lateinit var openLessonViewModel: OpenLessonViewModelK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_lesson)
        setSupportActionBar(findViewById(R.id.toolbarLessons))

        openLessonViewModel = OpenLessonViewModelK(this)

    }


    /** Inflate the menu. This adds items to the action bar if it is present. */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }


    /** Handle presses on the action bar menu items */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnSettings -> {
                showSettingsDialog(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("[APPLICATION CLOSED]","Application was successfully closed")
    }


    /**
     * These functions are for activity management
     * */
    fun startLessonRVActivityK(){
        val intent = Intent(this, LessonRVActivityK::class.java)
        startActivity(intent)
    }

    fun getActivity(): Activity { return this }

}
