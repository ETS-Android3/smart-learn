package com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.helpers.LessonDialog;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class LessonsFragment <VM extends LessonsViewModel<?>> extends BasicFragmentForRecyclerView<VM> {

    protected abstract void onFilter(String newText);

    @Override
    protected boolean showFloatingActionButton() {
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

    @Override
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).hideBottomNavigationMenu();
    }

    public void deleteLessonAlert(int wordsNr, int expressionsNr) {
        if(wordsNr == 0 && expressionsNr == 0){
            return;
        }

        String message = "";
        if(wordsNr != 0 && expressionsNr != 0){
            message = getString(R.string.delete_lesson_description_1) + " " + wordsNr + " " + getString(R.string.delete_lesson_description_2) + " " +
                    getString(R.string.delete_lesson_description_3) + " " + expressionsNr + " " + getString(R.string.delete_lesson_description_4) + " " +
                    getString(R.string.delete_lesson_description_5);
        }
        else{
            if(wordsNr == 0){
                message = getString(R.string.delete_lesson_description_1) + " " + expressionsNr + " " + getString(R.string.delete_lesson_description_4) + " " +
                        getString(R.string.delete_lesson_description_5);
            }
            if(expressionsNr == 0){
                message = getString(R.string.delete_lesson_description_1) + " " + wordsNr + " " + getString(R.string.delete_lesson_description_2) + " " +
                        getString(R.string.delete_lesson_description_5);
            }
        }

        Utilities.Activities.showStandardInfoDialog(requireContext(), getString(R.string.delete_lesson), message);
    }

    protected void showAddLessonDialog(){
        DialogFragment dialogFragment = new LessonDialog(new LessonDialog.Callback() {
            @Override
            public void onAddLesson(@NonNull @NotNull String lessonName) {
                viewModel.addLessonByName(lessonName);
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "LessonsFragment");
    }

}