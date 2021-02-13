package com.smart_learn.presenter.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.smart_learn.R
import com.smart_learn.core.general.showSettingsDialog
import com.smart_learn.core.services.activities.TestActivityService

class TestActivity : AppCompatActivity() {

    private lateinit var testActivityService: TestActivityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        setSupportActionBar(findViewById(R.id.toolbarDictionaries))

        // set up toolbar
        supportActionBar?.apply {
            title = "Test"

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // other settings
        testActivityService = TestActivityService(this)
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


    fun getActivity(): Activity { return this }
}