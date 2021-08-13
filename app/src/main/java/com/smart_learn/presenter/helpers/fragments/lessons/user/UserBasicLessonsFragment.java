package com.smart_learn.presenter.helpers.fragments.lessons.user;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.presenter.helpers.adapters.lessons.UserLessonsAdapter;
import com.smart_learn.presenter.helpers.fragments.lessons.BasicLessonsFragment;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicLessonsFragment <VM extends UserBasicLessonsViewModel> extends BasicLessonsFragment<DocumentSnapshot, VM> {

    protected void onAdapterShareLessonClick(@NonNull @NotNull DocumentSnapshot lessonSnapshot){}

    @Override
    protected boolean useSecondaryGroupOnMenu() {
        return true;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new UserLessonsAdapter(new UserLessonsAdapter.Callback() {
            @Override
            public void onDeleteLessonAlert(int wordsNr, int expressionsNr) {
                onAdapterDeleteLessonAlert(wordsNr, expressionsNr);
            }

            @Override
            public void onShareLessonClick(@NonNull @NotNull DocumentSnapshot lessonSnapshot) {
                onAdapterShareLessonClick(lessonSnapshot);
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
                return UserBasicLessonsFragment.this;
            }
        }));

    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(newText == null || newText.isEmpty()){
            newText = CoreUtilities.General.DEFAULT_VALUE_FOR_SEARCH;
        }

        viewModel.getAdapter().setFilterOption(UserBasicLessonsFragment.this, newText);
    }

    @Override
    protected void onSearchActionCollapse() {
        super.onSearchActionCollapse();
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setInitialOption(UserBasicLessonsFragment.this);
        }
    }

}