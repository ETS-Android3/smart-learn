package com.smart_learn.presenter.helpers.fragments.expressions.guest;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.expressions.GuestExpressionsAdapter;
import com.smart_learn.presenter.helpers.fragments.expressions.BasicExpressionsFragment;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;


public abstract class GuestBasicExpressionsFragment <VM extends GuestBasicExpressionsViewModel> extends BasicExpressionsFragment<Expression, VM> {

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

        viewModel.setAdapter(new GuestExpressionsAdapter(currentLessonId, new GuestExpressionsAdapter.Callback() {

            @Override
            public void onSimpleClick(@NonNull @NotNull Expression item) {
                onAdapterSimpleClick(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull Expression item) {
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
                return GuestBasicExpressionsFragment.this;
            }
        }));

        // set observers
        GuestExpressionService.getInstance().getCurrentLessonLiveExpressions(currentLessonId).observe(this, new Observer<List<Expression>>() {
            @Override
            public void onChanged(List<Expression> expressions) {
                Utilities.Activities.changeTextViewStatus(expressions.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(expressions);
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