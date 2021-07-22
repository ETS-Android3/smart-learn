package com.smart_learn.presenter.activities.notebook.user.fragments.select_friends;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.friends.FriendsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public class SelectFriendsFragment extends BasicFragmentForRecyclerView<SelectFriendsViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;


    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected boolean isBottomSheetUsed() {
        return false;
    }

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
        inflater.inflate(R.menu.menu_layout_with_recycler_view, menu);

        menu.setGroupVisible(R.id.secondary_group_menu_layout_with_recycler_view, false);
        Utilities.Activities.setSearchMenuItem(menu, R.id.action_search_menu_layout_with_recycler_view,
                new Callbacks.SearchActionCallback() {
                    @Override
                    public void onQueryTextChange(String newText) {
                        onFilter(newText);
                    }
                });

        MenuItem searchItem = menu.findItem(R.id.action_search_menu_layout_with_recycler_view);
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


    @NonNull
    @Override
    protected @NotNull Class<SelectFriendsViewModel> getModelClassForViewModel() {
        return SelectFriendsViewModel.class;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_friends;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.selected_friends_point;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(viewModel.getAdapter() != null){
            showSelectedItems(viewModel.getAdapter().getSelectedValues().size());
        }
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        floatingActionButton.setImageResource(R.drawable.ic_baseline_share_24);

        // set listeners
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.shareLesson(SelectFriendsFragment.this, sharedViewModel.getSelectedLesson());
            }
        });
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);

        // set fragment view model adapter
        viewModel.setAdapter(new FriendsAdapter(new FriendsAdapter.Callback() {
            @Override
            public void onRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot) {

            }

            @Override
            public boolean showCheckedIcon() {
                return true;
            }

            @Override
            public boolean showToolbar() {
                return false;
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {

            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {

            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                showSelectedItems(value);
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return SelectFriendsFragment.this;
            }
        }));

        // here be used only selection mode
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(true);
        }

    }


    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(TextUtils.isEmpty(newText)){
            viewModel.getAdapter().setInitialOption(SelectFriendsFragment.this);
        }
        else {
            viewModel.getAdapter().setFilterOption(SelectFriendsFragment.this, newText);
        }
    }

    protected void unsetValueFromEmptyLabel(){
        emptyLabel.setText("");
    }

    protected void resetValueFromEmptyLabel(){
        emptyLabel.setText(super.getEmptyLabelDescriptionResourceId());
    }

    public void showSelectedItems(int value){
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) this.requireActivity(), getString(R.string.selected_friends_point) + " " + value);
    }


}

