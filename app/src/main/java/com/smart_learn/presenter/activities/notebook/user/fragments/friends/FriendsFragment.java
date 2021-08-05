package com.smart_learn.presenter.activities.notebook.user.fragments.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.friends.select.SelectFriendsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class FriendsFragment extends SelectFriendsFragment<FriendsViewModel> {

    public static final String SELECT_FRIEND_FOR_NEW_EMPTY_SHARED_LESSON = "SELECT_FRIEND_FOR_NEW_EMPTY_SHARED_LESSON";

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<FriendsViewModel> getModelClassForViewModel() {
        return FriendsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected int getFloatingActionButtonIconResourceId() {
        boolean addNewSharedEmptyLesson = getArguments() != null && getArguments().getBoolean(SELECT_FRIEND_FOR_NEW_EMPTY_SHARED_LESSON);
        return addNewSharedEmptyLesson ? R.drawable.ic_baseline_navigate_next_24 : R.drawable.ic_baseline_share_24;
    }

    @Override
    protected void onFloatingActionButtonPress() {
        if(sharedViewModel.isAddNewEmptySharedLesson()){
            viewModel.addNewEmptySharedLesson(FriendsFragment.this, sharedViewModel.getNewEmptySharedLessonName());
        }
        else{
            viewModel.shareLesson(FriendsFragment.this, sharedViewModel.getSelectedLesson());
        }
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) this.requireActivity(), getString(R.string.selected_friends_point) + " " + value);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);
    }
}

