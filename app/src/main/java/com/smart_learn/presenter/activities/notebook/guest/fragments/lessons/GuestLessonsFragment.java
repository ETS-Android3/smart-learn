package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.helpers.LessonsAdapter;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.LessonsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;


public class GuestLessonsFragment extends LessonsFragment<GuestLessonsViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestLessonsViewModel> getModelClassForViewModel() {
        return GuestLessonsViewModel.class;
    }

    @Override
    protected int getBottomSheetLayout() {
        return R.layout.include_layout_action_mode_guest;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_guest;
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().getFilter().filter(newText);
        }
    }

    @Override
    protected void onActionModeCreate() {
        // mark that action mode started
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setLiveActionMode(true);
        }

        // Use this to prevent any previous selection. If an error occurred and
        // action mode could not be closed then items could not be disabled and will
        // hang as selected.  FIXME: try yo find a better way to do that
        GuestLessonService.getInstance().updateSelectAll(false);
    }

    @Override
    protected void onActionModeDestroy() {
        // use this to disable all selection
        GuestLessonService.getInstance().updateSelectAll(false);

        // mark that action mode finished
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setLiveActionMode(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.lessons));
        ((GuestNotebookActivity)requireActivity()).hideBottomNavigationMenu();
        sharedViewModel.setSelectedLessonId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setSelectedWordId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // this fragment does not need refreshing
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        // set bottom sheet listeners
        Button btnSelectAll = requireActivity().findViewById(R.id.btn_select_include_layout_action_mode_guest);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllItemsAreSelected(!viewModel.isAllItemsAreSelected());
                GuestLessonService.getInstance().updateSelectAll(viewModel.isAllItemsAreSelected());
                Utilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });

        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_guest);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestLessonsFragment.super.deleteLessonAlert(new Callbacks.StandardAlertDialogCallback() {
                    @Override
                    public void onPositiveButtonPress() {
                        viewModel.deleteSelectedItems();
                    }
                });
            }
        });


        // recycler view include layout listeners
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

        viewModel.setAdapter(new LessonsAdapter(new Callbacks.FragmentGeneralCallback<GuestLessonsFragment>() {
            @Override
            public GuestLessonsFragment getFragment() {
                return GuestLessonsFragment.this;
            }
        }));

        // set observers
        GuestLessonService.getInstance().getLiveSelectedItemsCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionMode != null){
                    actionMode.setTitle(getString(R.string.selected) + " " + integer);
                }
            }
        });

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




    public void goToHomeLessonFragment(Lesson lesson){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lesson == null || lesson.getLessonId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLessonId(lesson.getLessonId());
        ((GuestNotebookActivity)requireActivity()).goToHomeLessonFragment();
    }

}