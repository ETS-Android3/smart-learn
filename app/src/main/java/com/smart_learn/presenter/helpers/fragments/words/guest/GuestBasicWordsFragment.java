package com.smart_learn.presenter.helpers.fragments.words.guest;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.core.services.word.GuestWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.words.GuestWordsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.helpers.fragments.words.BasicWordsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

public abstract class GuestBasicWordsFragment <VM extends GuestBasicWordsViewModel> extends BasicWordsFragment<Word, VM> {

    public static final int NO_LESSON_SELECTED = -1;
    protected int currentLessonId = NO_LESSON_SELECTED;

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

        // try to link fragment with lesson
        if(getArguments() == null){
            Timber.w("getArguments() is null");
            return;
        }

        currentLessonId = getArguments().getInt(SELECTED_LESSON_KEY);
        if(currentLessonId == NO_LESSON_SELECTED){
            Timber.w("currentLessonId is not selected");
            return;
        }

        // set current lesson on view model for further operations inside view model
        viewModel.setCurrentLessonId(currentLessonId);

        viewModel.setAdapter(new GuestWordsAdapter(currentLessonId, new GuestWordsAdapter.Callback() {

            @Override
            public boolean isSelectedItemValid(@NonNull @NotNull Word item) {
                return onAdapterIsSelectedItemValid(item);
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull Word item) {
                onAdapterSimpleClick(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull Word item) {
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
                return GuestBasicWordsFragment.this;
            }
        }));

        // set observers
        GuestWordService.getInstance().getCurrentLessonLiveWords(currentLessonId).observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                PresenterUtilities.Activities.changeTextViewStatus(words.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(words);
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