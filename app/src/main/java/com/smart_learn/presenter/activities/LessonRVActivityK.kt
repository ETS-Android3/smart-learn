package com.smart_learn.presenter.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.smart_learn.R
import com.smart_learn.core.general.showSettingsDialog
import com.smart_learn.presenter.view_models.LessonRVViewModelK

class LessonRVActivityK : AppCompatActivity(){

    private lateinit var lessonRVViewModel: LessonRVViewModelK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rv_lessons)
        setSupportActionBar(findViewById(R.id.toolbarLessons))

        // set up toolbar
        supportActionBar?.apply {
            title = "Lessons"

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // other settings
        lessonRVViewModel = LessonRVViewModelK(this)
    }


    /** Inflate the menu. This adds items to the action bar if it is present. */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rv_menu, menu)

        // set the search action
        val item: MenuItem = menu.findItem(R.id.actionSearch)
        val searchView: SearchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                lessonRVViewModel.getAdapter().filter.filter(newText)
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
        lessonRVViewModel.resetStateData()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * These functions are for activity management
     * */
    fun startLessonActivityK(){
        val intent = Intent(this, LessonActivityK::class.java)
        startActivity(intent)
    }

    fun getActivity(): Activity { return this }

}
