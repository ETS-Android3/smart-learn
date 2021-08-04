package com.smart_learn.presenter.activities.test.user.fragments.select_friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.friends.select.SelectFriendsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class UserSelectFriendsFragment extends SelectFriendsFragment<UserSelectFriendsViewModel> {

    @Getter
    private UserTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserSelectFriendsViewModel> getModelClassForViewModel() {
        return UserSelectFriendsViewModel.class;
    }

    @Override
    protected int getFloatingActionButtonIconResourceId() {
        return R.drawable.ic_baseline_navigate_next_24;
    }

    @Override
    protected void onFloatingActionButtonPress() {
       goToSelectLessonFragment();
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.selected_friends_point) + " " + value);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    private void goToSelectLessonFragment(){
        if(viewModel.getAdapter() == null || viewModel.getAdapter().getSelectedValues().isEmpty()){
            showMessage(R.string.no_friend_selected);
            return;
        }
        if(sharedViewModel.getGeneratedTest() == null || !(sharedViewModel.getGeneratedTest() instanceof TestDocument)){
            showMessage(R.string.error_can_not_continue);
            return;
        }
        ((TestDocument)sharedViewModel.getGeneratedTest()).setSelectedFriends(viewModel.getAdapter().getSelectedValues());
        ((UserTestActivity)requireActivity()).goToUserSelectLessonFragment();
    }
}

