package com.smart_learn.presenter.activities.notebook.user;

import android.annotation.SuppressLint;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

public class UserNotebookActivity extends NotebookActivity<UserNotebookSharedViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserNotebookSharedViewModel> getModelClassForViewModel() {
        return UserNotebookSharedViewModel.class;
    }

    @Override
    protected int getNavigationGraphResource() {
        return R.navigation.nav_graph_activity_user_notebook;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.menu_bottom_navigation_activity_user_notebook;
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
                            case R.id.user_home_lesson_fragment_nav_graph_activity_user_notebook:
                                navController.navigate(R.id.user_home_lesson_fragment_nav_graph_activity_user_notebook,null,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.user_lessons_fragment_nav_graph_activity_user_notebook));
                                return true;
                            case R.id.user_words_fragment_nav_graph_activity_user_notebook:
                                navController.navigate(R.id.user_words_fragment_nav_graph_activity_user_notebook,null,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.user_lessons_fragment_nav_graph_activity_user_notebook));
                                return true;
                            case R.id.user_expressions_fragment_nav_graph_activity_user_notebook:
                                navController.navigate(R.id.user_expressions_fragment_nav_graph_activity_user_notebook,null,
                                        Utilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.user_lessons_fragment_nav_graph_activity_user_notebook));
                                return true;
                        }
                        return false;
                    }
                }, null);

    }

    public void goToUserHomeLessonFragment(){
        navController.navigate(R.id.action_user_lessons_fragment_to_user_home_lesson_fragment_nav_graph_activity_user_notebook,null,
                Utilities.Activities.getEnterBottomMenuNavOptions(R.id.user_home_lesson_fragment_nav_graph_activity_user_notebook));
    }

    public void goToSelectFriendsFragment(){
        navController.navigate(R.id.action_user_lessons_fragment_to_friends_fragment_nav_graph_activity_user_notebook);
    }

    public void goToUserWordContainerFragment(){
        navController.navigate(R.id.action_user_words_fragment_to_user_word_container_fragment_nav_graph_activity_user_notebook,null,
                Utilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToUserHomeExpressionFragment(){
        navController.navigate(R.id.action_user_expressions_fragment_to_user_home_expression_fragment_nav_graph_activity_user_notebook,
                null, Utilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }
}