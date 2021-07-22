package com.smart_learn.presenter.helpers.fragments.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.helpers.LessonDialog;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public abstract class BasicLessonsFragment <T, VM extends BasicLessonsViewModel<?>> extends BasicFragmentForRecyclerView<VM> {

    // general
    protected abstract boolean useToolbarMenu();
    protected abstract int getFloatingActionButtonIconResourceId();
    protected abstract void onFloatingActionButtonPress();
    protected abstract void onFilter(String newText);

    // adapter general
    protected abstract boolean onAdapterShowCheckedIcon();
    protected abstract boolean onAdapterShowOptionsToolbar();
    protected abstract void onAdapterSimpleClick(@NonNull @NotNull T item);
    protected abstract void onAdapterLongClick(@NonNull @NotNull T item);
    protected abstract void onAdapterUpdateSelectedItemsCounter(int value);

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(useToolbarMenu()){
            // use this to set toolbar menu inside fragment
            // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
            setHasOptionsMenu(true);
        }
        return binding.getRoot();
    }



    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        if(showFloatingActionButton()){
            floatingActionButton.setImageResource(getFloatingActionButtonIconResourceId());

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFloatingActionButtonPress();
                }
            });
        }
    }

    private void unsetValueFromEmptyLabel(){
        emptyLabel.setText("");
    }

    private void resetValueFromEmptyLabel(){
        emptyLabel.setText(super.getEmptyLabelDescriptionResourceId());
    }









    protected abstract boolean hideSecondaryGroupToolbarMenu();
    protected abstract boolean hideGuestGroupToolbarMenu();
    protected abstract boolean hideUserGroupToolbarMenu();

    protected abstract void onAdapterDeleteLessonAlert(int wordsNr, int expressionsNr);


    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(!useToolbarMenu()){
            return;
        }

        inflater.inflate(R.menu.menu_toolbar_fragment_lessons, menu);

        if(hideSecondaryGroupToolbarMenu()){
            menu.setGroupVisible(R.id.guest_group_menu_toolbar_fragment_lessons, false);
            menu.setGroupVisible(R.id.user_group_menu_toolbar_fragment_lessons, false);
        }
        else {
            if(hideGuestGroupToolbarMenu()){
                menu.setGroupVisible(R.id.guest_group_menu_toolbar_fragment_lessons, false);
            }

            if(hideUserGroupToolbarMenu()){
                menu.setGroupVisible(R.id.user_group_menu_toolbar_fragment_lessons, false);
            }
        }


        Utilities.Activities.setSearchMenuItem(menu, R.id.action_search_menu_toolbar_fragment_lessons,
                new Callbacks.SearchActionCallback() {
                    @Override
                    public void onQueryTextChange(String newText) {
                        onFilter(newText);
                    }
                });

        MenuItem searchItem = menu.findItem(R.id.action_search_menu_toolbar_fragment_lessons);
        if(searchItem == null){
            Timber.w("searchItem is null ==> search is not functionally");
            return;
        }

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.setGroupVisible(R.id.user_group_menu_toolbar_fragment_lessons, false);
                menu.setGroupVisible(R.id.guest_group_menu_toolbar_fragment_lessons, false);
                unsetValueFromEmptyLabel();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(!hideSecondaryGroupToolbarMenu()){
                    if(!hideGuestGroupToolbarMenu()){
                        menu.setGroupVisible(R.id.guest_group_menu_toolbar_fragment_lessons, true);
                    }
                    if(!hideUserGroupToolbarMenu()){
                        menu.setGroupVisible(R.id.user_group_menu_toolbar_fragment_lessons, true);
                    }
                }
                resetValueFromEmptyLabel();
                return true;
            }
        });
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_lessons;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.lessons;
    }


    protected void deleteLessonAlert(int wordsNr, int expressionsNr) {
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


}