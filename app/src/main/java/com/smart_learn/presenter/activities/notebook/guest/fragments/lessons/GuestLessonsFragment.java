package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.helpers.LessonsAdapter;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.LessonsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;
import timber.log.Timber;


public class GuestLessonsFragment extends LessonsFragment<GuestLessonsViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestLessonsViewModel> getModelClassForViewModel() {
        return GuestLessonsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().getFilter().filter(newText);
        }
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

        MenuItem searchItem = menu.findItem(R.id.action_search_menu_layout_with_recycler_view);
        if(searchItem == null){
            Timber.w("searchItem is null ==> search is not functionally");
            return;
        }

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                unsetValueFromEmptyLabel();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                resetValueFromEmptyLabel();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.setSelectedLessonId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setSelectedWordId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setSelectedExpressionId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // this fragment does not need refreshing
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        // set listeners
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddLessonDialog();
            }
        });
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);

        // set adapter
        viewModel.setAdapter(new LessonsAdapter(new Callbacks.FragmentGeneralCallback<GuestLessonsFragment>() {
            @Override
            public GuestLessonsFragment getFragment() {
                return GuestLessonsFragment.this;
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


    public void goToGuestHomeLessonFragment(Lesson lesson){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lesson == null || lesson.getLessonId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLessonId(lesson.getLessonId());
        ((GuestNotebookActivity)requireActivity()).goToGuestHomeLessonFragment();
    }

}