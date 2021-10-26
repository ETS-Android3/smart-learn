package com.smart_learn.presenter.guest.fragments.common.lessons;


import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.core.guest.services.GuestLessonService;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.guest.room.entitites.Lesson;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.adapters.GuestLessonsAdapter;
import com.smart_learn.presenter.common.fragments.lessons.BasicLessonsFragment;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GuestBasicLessonsFragment <VM extends GuestBasicLessonsViewModel> extends BasicLessonsFragment<Lesson, VM> {

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
                PresenterUtilities.Activities.changeTextViewStatus(lessons.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(lessons);
                }
            }
        });
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(newText == null || newText.isEmpty()){
            newText = CoreUtilities.General.DEFAULT_VALUE_FOR_SEARCH;
        }

        viewModel.getAdapter().getFilter().filter(newText);
    }
}