package com.smart_learn.presenter.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.smart_learn.R
import com.smart_learn.general.showSettingsDialog
import com.smart_learn.services.activities.DictionaryActivityService

class DictionaryActivity : AppCompatActivity() {

    private lateinit var dictionaryActivityService: DictionaryActivityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)
        setSupportActionBar(findViewById(R.id.toolbar))

        // set up toolbar
        supportActionBar?.apply {
            title = "Dictionary"

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // other settings
        dictionaryActivityService = DictionaryActivityService(this)

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


    /** When a dictionary is deleted go to previous activity */
    fun startDictionariesRVActivity(){
        val intent = Intent(this, DictionariesRVActivity::class.java)
        startActivity(intent)
    }


    fun startTestGenerationActivity(){
        val intent = Intent(this, TestGenerationActivity::class.java)
        startActivity(intent)
    }


    /** Show entries from the selected dictionary */
    fun startEntrancesRVActivity(){
        val intent = Intent(this, EntrancesRVActivity::class.java)
        startActivity(intent)
    }


    fun getActivity(): Activity { return this }

}