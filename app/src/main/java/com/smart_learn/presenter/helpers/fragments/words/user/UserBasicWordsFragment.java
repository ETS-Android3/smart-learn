package com.smart_learn.presenter.helpers.fragments.words.user;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.core.services.UserLessonService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.helpers.adapters.words.UserWordsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.helpers.fragments.words.BasicWordsFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public abstract class UserBasicWordsFragment <VM extends UserBasicWordsViewModel> extends BasicWordsFragment<DocumentSnapshot, VM> {

    public static final String NO_LESSON_SELECTED = "";
    protected String currentLessonId = NO_LESSON_SELECTED;

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // try to link fragment with lesson
        if(getArguments() == null){
            Timber.w("getArguments() is null");
            return;
        }

        currentLessonId = getArguments().getString(SELECTED_LESSON_KEY);
        if(currentLessonId.equals(NO_LESSON_SELECTED)){
            Timber.w("currentLessonId is not selected");
            return;
        }

        UserLessonService.getInstance()
                .getLessonsCollectionReference()
                .document(currentLessonId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w("lesson snapshot load failed");
                            return;
                        }

                        DocumentSnapshot lessonSnapshot = task.getResult();

                        // set current lesson on view model for further operations inside view model
                        viewModel.setCurrentLessonSnapshot(lessonSnapshot);

                        viewModel.setAdapter(new UserWordsAdapter(lessonSnapshot, new UserWordsAdapter.Callback() {
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
                                return UserBasicWordsFragment.this;
                            }
                        }));

                        // link adapter with fragment recycler view
                        if(viewModel.getAdapter() != null){
                            recyclerView.setAdapter(viewModel.getAdapter());
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

        viewModel.getAdapter().setFilterOption(UserBasicWordsFragment.this, newText);
    }

    @Override
    protected void onSearchActionCollapse() {
        super.onSearchActionCollapse();
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setInitialOption(UserBasicWordsFragment.this);
        }
    }
}