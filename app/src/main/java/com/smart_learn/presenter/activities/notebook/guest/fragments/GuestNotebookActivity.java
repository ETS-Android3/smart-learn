package com.smart_learn.presenter.activities.notebook.guest.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.helpers.WebViewFragment;

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
        return R.menu.menu_bottom_navigation_activity_guest_notebook;
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
                            case R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook:
                                navController.navigate(R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook,null,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            case R.id.guest_words_fragment_nav_graph_activity_guest_notebook:
                                navController.navigate(R.id.guest_words_fragment_nav_graph_activity_guest_notebook,null,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            case R.id.guest_home_word_fragment_nav_graph_activity_guest_notebook:
                                navController.navigate(R.id.guest_home_word_fragment_nav_graph_activity_guest_notebook,null,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_words_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            case R.id.guest_meaning_web_view_fragment_nav_graph_activity_guest_notebook:
                                // https://developer.android.com/guide/navigation/navigation-pass-data#java
                                Bundle meaningBundle = new Bundle();
                                meaningBundle.putString(WebViewFragment.URL_KEY, sharedViewModel.getMeaningUrl());
                                navController.navigate(R.id.guest_meaning_web_view_fragment_nav_graph_activity_guest_notebook, meaningBundle,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_words_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            case R.id.guest_examples_web_view_fragment_nav_graph_activity_guest_notebook:
                                // https://developer.android.com/guide/navigation/navigation-pass-data#java
                                Bundle examplesBundle = new Bundle();
                                examplesBundle.putString(WebViewFragment.URL_KEY, sharedViewModel.getExamplesUrl());
                                navController.navigate(R.id.guest_examples_web_view_fragment_nav_graph_activity_guest_notebook, examplesBundle,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_words_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            case R.id.guest_expressions_fragment_nav_graph_activity_guest_notebook:
                                navController.navigate(R.id.guest_expressions_fragment_nav_graph_activity_guest_notebook,null,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                        }
                        return false;
                    }
                }, null);

    }

    public void goToGuestHomeLessonFragment(){
        navController.navigate(R.id.action_guest_lessons_fragment_to_guest_home_lesson_fragment_nav_graph_activity_guest_notebook,null,
                Utilities.Activities.getEnterBottomMenuNavOptions(R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook));
    }

    public void goToGuestHomeWordFragment(){
        navController.navigate(R.id.action_guest_words_fragment_nav_graph_activity_notebook_to_guest_home_word_fragment_nav_graph_activity_guest_notebook,null,
                Utilities.Activities.getEnterBottomMenuNavOptions(R.id.guest_home_word_fragment_nav_graph_activity_guest_notebook));
    }

    public void goToGuestHomeExpressionFragment(){
        navController.navigate(R.id.action_guest_expressions_fragment_nav_graph_activity_guest_notebook_to_guest_home_expression_fragment_nav_graph_activity_guest_notebook,
                null, Utilities.Activities.getEnterBottomMenuNavOptions(R.id.guest_home_expression_fragment_nav_graph_activity_guest_notebook));
    }

    @Override
    public void showMainGroupBottomNavigation(){
        super.hideBottomNavigationMenu();
    }

    @Override
    public void showLessonGroupBottomNavigation(){
        binding.bottomNavigationActivityNotebook.getMenu().setGroupVisible(R.id.lesson_group_menu_bottom_navigation_activity_guest_notebook, true);
        binding.bottomNavigationActivityNotebook.getMenu().setGroupVisible(R.id.word_group_menu_bottom_navigation_activity_guest_notebook, false);
        super.showBottomNavigationMenu();
    }

    @Override
    public void showWordGroupBottomNavigation(){
        binding.bottomNavigationActivityNotebook.getMenu().setGroupVisible(R.id.lesson_group_menu_bottom_navigation_activity_guest_notebook, false);
        binding.bottomNavigationActivityNotebook.getMenu().setGroupVisible(R.id.word_group_menu_bottom_navigation_activity_guest_notebook, true);
        super.showBottomNavigationMenu();
    }
}