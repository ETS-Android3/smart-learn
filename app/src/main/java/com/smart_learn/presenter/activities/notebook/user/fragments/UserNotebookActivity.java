package com.smart_learn.presenter.activities.notebook.user.fragments;

import androidx.annotation.NonNull;

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
                binding.bottomNavigationActivityNotebook, null, null);

    }

    public void goToUserHomeLessonFragment(){

    }

    public void goToSelectFriendsFragment(){
        navController.navigate(R.id.action_user_lessons_fragment_to_select_friends_fragment_nav_graph_activity_user_notebook);
    }
}