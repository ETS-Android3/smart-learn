package com.smart_learn.presenter.activities.community.fragments.friends;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.presenter.activities.community.fragments.friends.helpers.FriendDialog;
import com.smart_learn.presenter.activities.community.fragments.friends.helpers.FriendsAdapter;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class FriendsFragment extends BasicFragmentForRecyclerView<FriendsViewModel> {

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // use this to set toolbar menu inside fragment
        // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar_activity_community, menu);
        Utilities.Activities.setSearchMenuItem(menu, R.id.action_search_menu_toolbar_activity_community, new Callbacks.SearchActionCallback() {
            @Override
            public void onQueryTextChange(String newText) {
                onFilter(newText);
            }
        });

        MenuItem searchItem = menu.findItem(R.id.action_search_menu_toolbar_activity_community);
        if(searchItem == null){
            Timber.w("searchItem is null ==> search is not functionally");
            return;
        }

        // use this to show/hide a specific menu group
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                unsetValueFromEmptyLabel();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                resetValueFromEmptyLabel();
                return true;
            }
        });
    }

    @NonNull
    @Override
    protected @NotNull Class<FriendsViewModel> getModelClassForViewModel() {
        return FriendsViewModel.class;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_friends;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.friends;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set fragment view model adapter
        viewModel.setAdapter(new FriendsAdapter(new FriendsAdapter.Callback<FriendsFragment>() {
            @Override
            public boolean hideItemToolbar() {
                return false;
            }

            @Override
            public boolean showCheckedIcon() {
                return false;
            }

            @Override
            public void onItemClick(@NonNull @NotNull DocumentSnapshot documentSnapshot) {
                showFriendDialog(documentSnapshot);
            }

            @Override
            public FriendsFragment getFragment() {
                return FriendsFragment.this;
            }
        }));
    }

    private void showFriendDialog(@NonNull @NotNull DocumentSnapshot friendSnapshot){
        DialogFragment dialogFragment = new FriendDialog(new FriendDialog.Callback() {
            @Override
            public void onRemoveFriend() {
                viewModel.removeFriend(FriendsFragment.this, friendSnapshot);
            }
        }, friendSnapshot.toObject(FriendDocument.class));

        dialogFragment.show(requireActivity().getSupportFragmentManager(), "FriendsFragment");
    }


    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(TextUtils.isEmpty(newText)){
            viewModel.getAdapter().setInitialOption(FriendsFragment.this);
        }
        else {
            viewModel.getAdapter().setFilterOption(FriendsFragment.this, newText);
        }
    }

    protected void unsetValueFromEmptyLabel(){
        emptyLabel.setText("");
    }

    protected void resetValueFromEmptyLabel(){
        emptyLabel.setText(super.getEmptyLabelDescriptionResourceId());
    }

}