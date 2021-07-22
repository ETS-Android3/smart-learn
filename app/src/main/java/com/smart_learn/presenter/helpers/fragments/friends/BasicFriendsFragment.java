package com.smart_learn.presenter.helpers.fragments.friends;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.friends.FriendsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public abstract class BasicFriendsFragment <VM extends BasicFriendsViewModel> extends BasicFragmentForRecyclerView<VM> {

    // general
    protected abstract boolean useToolbarMenu();

    // for adapter
    protected abstract void onAdapterRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot);
    protected abstract boolean onAdapterShowCheckedIcon();
    protected abstract boolean onAdapterShowOptionsToolbar();
    protected abstract void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item);
    protected abstract void onAdapterLongClick(@NonNull @NotNull DocumentSnapshot item);
    protected abstract void onAdapterUpdateSelectedItemsCounter(int value);

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_friends;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.friends;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(useToolbarMenu()){
            // use this to set toolbar menu inside fragment
            // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(!useToolbarMenu()){
            return;
        }

        inflater.inflate(R.menu.menu_toolbar_fragment_friends, menu);
        Utilities.Activities.setSearchMenuItem(menu, R.id.action_search_menu_toolbar_fragment_friends, new Callbacks.SearchActionCallback() {
            @Override
            public void onQueryTextChange(String newText) {
                onFilter(newText);
            }
        });

        MenuItem searchItem = menu.findItem(R.id.action_search_menu_toolbar_fragment_friends);
        if(searchItem == null){
            Timber.w("searchItem is null ==> search is not functionally");
            return;
        }

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

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new FriendsAdapter(new FriendsAdapter.Callback() {
            @Override
            public void onRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot) {
                onAdapterRemoveFriend(friendSnapshot);
            }

            @Override
            public boolean showCheckedIcon() {
                return onAdapterShowCheckedIcon();
            }

            @Override
            public boolean showToolbar() {
                return onAdapterShowOptionsToolbar();
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                onAdapterSimpleClick(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {
                onAdapterLongClick(item);
            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                onAdapterUpdateSelectedItemsCounter(value);
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return BasicFriendsFragment.this;
            }
        }));
    }

    private void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(TextUtils.isEmpty(newText)){
            viewModel.getAdapter().setInitialOption(BasicFriendsFragment.this);
        }
        else {
            viewModel.getAdapter().setFilterOption(BasicFriendsFragment.this, newText);
        }
    }

    private void unsetValueFromEmptyLabel(){
        emptyLabel.setText("");
    }

    private void resetValueFromEmptyLabel(){
        emptyLabel.setText(super.getEmptyLabelDescriptionResourceId());
    }

}