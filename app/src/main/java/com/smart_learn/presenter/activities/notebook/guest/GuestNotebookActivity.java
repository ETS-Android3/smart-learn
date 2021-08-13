package com.smart_learn.presenter.activities.notebook.guest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.words.BasicWordsFragment;

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
        navController = PresenterUtilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_notebook,
                binding.bottomNavigationActivityNotebook, new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook:
                                navController.navigate(R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook,null,
                                        PresenterUtilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            case R.id.guest_words_fragment_nav_graph_activity_guest_notebook:
                                Bundle wordArgs = new Bundle();
                                wordArgs.putInt(BasicWordsFragment.SELECTED_LESSON_KEY, sharedViewModel.getSelectedLessonId());
                                navController.navigate(R.id.guest_words_fragment_nav_graph_activity_guest_notebook, wordArgs,
                                        PresenterUtilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                            case R.id.guest_expressions_fragment_nav_graph_activity_guest_notebook:
                                Bundle expressionArgs = new Bundle();
                                expressionArgs.putInt(BasicWordsFragment.SELECTED_LESSON_KEY, sharedViewModel.getSelectedLessonId());
                                navController.navigate(R.id.guest_expressions_fragment_nav_graph_activity_guest_notebook, expressionArgs,
                                        PresenterUtilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.guest_lessons_fragment_nav_graph_activity_guest_notebook));
                                return true;
                        }
                        return false;
                    }
                }, null);

    }

    public void goToGuestHomeLessonFragment(){
        navController.navigate(R.id.action_guest_lessons_fragment_to_guest_home_lesson_fragment_nav_graph_activity_guest_notebook,null,
                PresenterUtilities.Activities.getEnterBottomMenuNavOptions(R.id.guest_home_lesson_fragment_nav_graph_activity_guest_notebook));
    }

    public void goToGuestWordContainerFragment(){
        navController.navigate(R.id.action_guest_words_fragment_to_guest_word_container_fragment_nav_graph_activity_guest_notebook,null,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToGuestHomeExpressionFragment(){
        navController.navigate(R.id.action_guest_expressions_fragment_to_guest_home_expression_fragment_nav_graph_activity_guest_notebook,
                null, PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

}