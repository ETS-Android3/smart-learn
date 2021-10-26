package com.smart_learn.presenter.user.activities.notebook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart_learn.R;
import com.smart_learn.presenter.common.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.user.activities.notebook.fragments.friends.FriendsFragment;
import com.smart_learn.presenter.user.activities.notebook.fragments.home_expression.UserHomeExpressionFragment;
import com.smart_learn.presenter.user.activities.notebook.fragments.home_lesson.UserHomeLessonFragment;
import com.smart_learn.presenter.user.activities.notebook.fragments.home_word.UserWordContainerFragment;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.expressions.BasicExpressionsFragment;
import com.smart_learn.presenter.user.fragments.common.expressions.UserBasicExpressionsFragment;
import com.smart_learn.presenter.common.fragments.words.BasicWordsFragment;
import com.smart_learn.presenter.user.fragments.common.words.UserBasicWordsFragment;

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
        navController = PresenterUtilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_notebook,
                binding.bottomNavigationActivityNotebook, new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.user_home_lesson_fragment_nav_graph_activity_user_notebook:
                                navController.navigate(R.id.user_home_lesson_fragment_nav_graph_activity_user_notebook,null,
                                        PresenterUtilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.user_lessons_fragment_nav_graph_activity_user_notebook));
                                return true;
                            case R.id.user_words_fragment_nav_graph_activity_user_notebook:
                                Bundle wordArgs = new Bundle();
                                wordArgs.putString(BasicWordsFragment.SELECTED_LESSON_KEY, sharedViewModel.getSelectedLesson().getId());
                                wordArgs.putBoolean(UserBasicWordsFragment.IS_SHARED_LESSON_SELECTED, sharedViewModel.isSharedLessonSelected());
                                navController.navigate(R.id.user_words_fragment_nav_graph_activity_user_notebook, wordArgs,
                                        PresenterUtilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.user_lessons_fragment_nav_graph_activity_user_notebook));
                                return true;
                            case R.id.user_expressions_fragment_nav_graph_activity_user_notebook:
                                Bundle expressionArgs = new Bundle();
                                expressionArgs.putString(BasicExpressionsFragment.SELECTED_LESSON_KEY, sharedViewModel.getSelectedLesson().getId());
                                expressionArgs.putBoolean(UserBasicExpressionsFragment.IS_SHARED_LESSON_SELECTED, sharedViewModel.isSharedLessonSelected());
                                navController.navigate(R.id.user_expressions_fragment_nav_graph_activity_user_notebook, expressionArgs,
                                        PresenterUtilities.Activities.getBottomMenuNavOptionsForOnBackPress(R.id.user_lessons_fragment_nav_graph_activity_user_notebook));
                                return true;
                        }
                        return false;
                    }
                }, null);

    }

    public void goToUserHomeLessonFragment(boolean isSharedLessonSelected){
        Bundle args = new Bundle();
        args.putBoolean(UserHomeLessonFragment.IS_SHARED_LESSON_SELECTED, isSharedLessonSelected);
        navController.navigate(R.id.action_user_lessons_fragment_to_user_home_lesson_fragment_nav_graph_activity_user_notebook, args,
                PresenterUtilities.Activities.getEnterBottomMenuNavOptions(R.id.user_home_lesson_fragment_nav_graph_activity_user_notebook));
    }

    public void goToSelectFriendsFragment(boolean addNewEmptySharedLesson){
        Bundle args = new Bundle();
        args.putBoolean(FriendsFragment.SELECT_FRIEND_FOR_NEW_EMPTY_SHARED_LESSON, addNewEmptySharedLesson);
        navController.navigate(R.id.action_user_lessons_fragment_to_friends_fragment_nav_graph_activity_user_notebook, args);
    }

    public void goToUserWordContainerFragment(boolean isWordOwner){
        Bundle args = new Bundle();
        args.putBoolean(UserWordContainerFragment.IS_WORD_OWNER, isWordOwner);
        navController.navigate(R.id.action_user_words_fragment_to_user_word_container_fragment_nav_graph_activity_user_notebook, args,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToUserHomeExpressionFragment(boolean isExpressionOwner){
        Bundle args = new Bundle();
        args.putBoolean(UserHomeExpressionFragment.IS_EXPRESSION_OWNER, isExpressionOwner);
        navController.navigate(R.id.action_user_expressions_fragment_to_user_home_expression_fragment_nav_graph_activity_user_notebook,
                args, PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToSharedLessonParticipantsFragment(){
        navController.navigate(R.id.action_user_home_lesson_fragment_to_shared_lesson_participants_fragment_nav_graph_activity_user_notebook, null,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }
}