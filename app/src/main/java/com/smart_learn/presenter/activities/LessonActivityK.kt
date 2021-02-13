package com.smart_learn.presenter.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.smart_learn.R
import com.smart_learn.core.general.showSettingsDialog
import com.smart_learn.presenter.view_models.LessonViewModelK

class LessonActivityK : AppCompatActivity() {

    private lateinit var lessonViewModel: LessonViewModelK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        setSupportActionBar(findViewById(R.id.toolbar))

        // set up toolbar
        supportActionBar?.apply {
            title = "Lesson"

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // other settings
        lessonViewModel = LessonViewModelK(this)

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


    /**
     * These functions are for activity management
     * */


    /** When a lesson is deleted go to previous activity */
    fun startLessonRVActivityK(){
        val intent = Intent(this, LessonRVActivityK::class.java)
        startActivity(intent)
    }


    fun startTestGenerationActivity(){
        val intent = Intent(this, TestGenerationActivity::class.java)
        startActivity(intent)
    }


    /** Show entries from the selected lesson */
    fun startEntrancesRVActivity(){
        val intent = Intent(this, EntranceRVActivityK::class.java)
        startActivity(intent)
    }


    fun getActivity(): Activity { return this }

}