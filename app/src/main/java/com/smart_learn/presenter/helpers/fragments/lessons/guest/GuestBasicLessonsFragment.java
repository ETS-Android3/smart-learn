package com.smart_learn.presenter.helpers.fragments.lessons.guest;


import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.lessons.GuestLessonsAdapter;
import com.smart_learn.presenter.helpers.fragments.lessons.BasicLessonsFragment;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GuestBasicLessonsFragment <VM extends GuestBasicLessonsViewModel> extends BasicLessonsFragment<Lesson, VM> {

    @Override
    protected boolean hideGuestGroupToolbarMenu() {
        return false;
    }

    @Override
    protected boolean hideUserGroupToolbarMenu() {
        return true;
    }

    @Override
    protected boolean hideSecondaryGroupToolbarMenu() {
        return true;
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        // guest fragment does not need refreshing
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new GuestLessonsAdapter(new GuestLessonsAdapter.Callback() {
            @Override
            public void onDeleteLessonAlert(int wordsNr, int expressionsNr) {
                onAdapterDeleteLessonAlert(wordsNr, expressionsNr);
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull Lesson item) {
                onAdapterSimpleClick(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull Lesson item) {
                onAdapterLongClick(item);
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
            public void updateSelectedItemsCounter(int value) {
                onAdapterUpdateSelectedItemsCounter(value);
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return GuestBasicLessonsFragment.this;
            }
        }));

        // set observers
        GuestLessonService.getInstance().getAllLiveSampleLessons().observe(this, new Observer<List<Lesson>>() {
            @Override
            public void onChanged(List<Lesson> lessons) {
                Utilities.Activities.changeTextViewStatus(lessons.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(lessons);
                }
            }
        });
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().getFilter().filter(newText);
        }
    }

}