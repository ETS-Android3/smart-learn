package com.smart_learn.presenter.activities.notebook.fragments.words;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class WordsFragment <VM extends WordsViewModel<?>> extends BasicFragmentForRecyclerView<VM> {

    protected abstract void onFilter(String newText);
    protected abstract void onActionModeCreate();
    protected abstract void onActionModeDestroy();

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected boolean isBottomSheetUsed() {
        return true;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // use this to set toolbar menu inside fragment
        // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_layout_with_recycler_view, menu);
        Utilities.Activities.setSearchMenuItem(requireActivity(), menu, R.id.action_search_menu_layout_with_recycler_view,
                R.id.secondary_group_menu_layout_with_recycler_view, new Callbacks.SearchActionCallback() {
                    @Override
                    public void onQueryTextChange(String newText) {
                        onFilter(newText);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.action_filter_menu_layout_with_recycler_view){
            ((NotebookActivity)requireActivity()).showFilterOptionsDialog(WordsFragment.this, new Callbacks.FragmentFilterOptionsCallback() {
                @Override
                public void onAZFilter() {

                }
            });

            return true;
        }

        return false;
    }


    public void startFragmentActionMode() {
        super.startActionMode(new Callbacks.ActionModeCustomCallback() {
            @Override
            public void onCreateActionMode() {
                onActionModeCreate();
            }
            @Override
            public void onDestroyActionMode() {
                onActionModeDestroy();
            }
        });
    }
}














