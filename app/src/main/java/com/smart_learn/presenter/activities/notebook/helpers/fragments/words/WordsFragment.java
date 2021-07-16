package com.smart_learn.presenter.activities.notebook.helpers.fragments.words;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.words.helpers.WordDialog;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    protected int getToolbarTitle() {
        return R.string.words;
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
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).showLessonGroupBottomNavigation();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_layout_with_recycler_view, menu);

        // TODO: Implement filtering options (at this moment options will be hidden)
        menu.setGroupVisible(R.id.secondary_group_menu_layout_with_recycler_view, false);
        Utilities.Activities.setSearchMenuItem(menu, R.id.action_search_menu_layout_with_recycler_view,
                new Callbacks.SearchActionCallback() {
                    @Override
                    public void onQueryTextChange(String newText) {
                        onFilter(newText);
                    }
                });
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

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddWordDialog();
            }
        });
    }

    private void showAddWordDialog(){
        WordDialog dialog = new WordDialog(new WordDialog.Callback() {
            @Override
            public void onAddWord(@NonNull @NotNull String wordValue, @NonNull @NotNull String phonetic,
                                  @NonNull @NotNull String notes, @NonNull @NotNull ArrayList<Translation> translations) {
                viewModel.addWord(wordValue, phonetic, notes, translations);
            }
        });
        dialog.show(requireActivity().getSupportFragmentManager(), "WordsFragment");
    }
}














