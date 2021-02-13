package com.smart_learn.presenter.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.smart_learn.R
import com.smart_learn.core.general.showSettingsDialog
import com.smart_learn.presenter.view_models.EntranceRVViewModelK

class EntranceRVActivityK : AppCompatActivity() {

    private lateinit var entranceRVViewModel: EntranceRVViewModelK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv_entrances)

        // https://android--code.blogspot.com/2020/06/android-kotlin-toolbar-back-button.html
        // https://stackoverflow.com/questions/26515058/this-activity-already-has-an-action-bar-supplied-by-the-window-decor
        // set toolbar as support action bar
        setSupportActionBar(findViewById(R.id.toolbarLessons))

        // set up toolbar
        supportActionBar?.apply {
            title = "Lesson entries"

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // other settings
        entranceRVViewModel = EntranceRVViewModelK(this)
    }



    /** Inflate the menu. This adds items to the action bar if it is present. */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rv_menu, menu)

        // set the search action
        val item: MenuItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                entranceRVViewModel.getAdapter().filter.filter(newText)
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
            R.id.btnSettings -> {
                showSettingsDialog(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onResume() {
        super.onResume()
        entranceRVViewModel.resetStateData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getActivity(): Activity { return this }

}