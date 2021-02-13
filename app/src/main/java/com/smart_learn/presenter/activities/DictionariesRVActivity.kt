package com.smart_learn.presenter.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.smart_learn.R
import com.smart_learn.general.showSettingsDialog
import com.smart_learn.services.activities.DictionariesRVActivityService

class DictionariesRVActivity : AppCompatActivity(){

    private lateinit var dictionariesRVActivityService: DictionariesRVActivityService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv_dictionaries)
        setSupportActionBar(findViewById(R.id.toolbarDictionaries))

        // set up toolbar
        supportActionBar?.apply {
            title = "Dictionaries"

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // other settings
        dictionariesRVActivityService = DictionariesRVActivityService(this)
    }


    /** Inflate the menu. This adds items to the action bar if it is present. */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rv_menu, menu)

        // set the search action
        val item: MenuItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                dictionariesRVActivityService.getAdapter().filter.filter(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        return true
    }


    /** Handle presses on the action bar menu items */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnSettingsRV -> {
                showSettingsDialog(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        dictionariesRVActivityService.resetStateData()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * These functions are for activity management
     * */
    fun startDictionaryActivity(){
        val intent = Intent(this, DictionaryActivity::class.java)
        startActivity(intent)
    }

    fun getActivity(): Activity { return this }

}
