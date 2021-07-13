package com.smart_learn.presenter.activities.notebook.guest.fragments;

import android.annotation.SuppressLint;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

public class GuestNotebookActivity extends NotebookActivity<GuestNotebookSharedViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestNotebookSharedViewModel> getModelClassForViewModel() {
        return GuestNotebookSharedViewModel.class;
    }

    @Override
    protected int getNavigationGraphResource() {
        return R.navigation.nav_graph_activity_guest_notebook;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.menu_bottom_navigation_activity_notebook;
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // try to set navigation graph
        navController = Utilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_notebook,
                binding.bottomNavigationActivityNotebook, new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        switch (item.getItemId()){
                            // fragment with bottom navigation view VISIBLE
                            case R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook:
                                navController.navigate(R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook,null,
                                        Utilities.Activities.getVisibleBottomMenuNavOptions(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            // fragment with bottom navigation view VISIBLE
                            case R.id.guest_words_fragment_nav_graph_activity_guest_notebook:
                                navController.navigate(R.id.guest_words_fragment_nav_graph_activity_guest_notebook,null,
                                        Utilities.Activities.getVisibleBottomMenuNavOptions(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                        }
                        return false;
                    }
                }, null);

    }

    /** Fragment has bottom navigation view HIDDEN. */
    public void goToHomeLessonFragment(){
        navController.navigate(R.id.action_guest_lessons_fragment_to_guest_home_lesson_fragment_nav_graph_activity_guest_notebook,null,
                Utilities.Activities.getEnterBottomMenuNavOptions(R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook));
    }

    /** Fragment has bottom navigation view HIDDEN. */
    public void goToHomeWordFragment(){
        navController.navigate(R.id.action_guest_words_fragment_nav_graph_activity_notebook_to_guest_home_word_fragment_nav_graph_activity_guest_notebook,null,
                Utilities.Activities.getExitBottomMenuNavOptions(R.id.guest_home_word_fragment_nav_graph_activity_guest_notebook));
    }
}