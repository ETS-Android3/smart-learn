package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.NotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.LessonsFragment;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers.LessonDialog;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers.LessonsAdapter;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;


public class GuestLessonsFragment extends LessonsFragment<GuestLessonsViewModel> {

    @Getter
    private NotebookSharedViewModel sharedViewModel;

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
        // Use this to prevent any previous selection. If an error occurred and
        // action mode could not be closed then items could not be disabled and will
        // hang as selected.  FIXME: try yo find a better way to do that
        viewModel.getLessonService().updateSelectAll(false);
    }

    @Override
    protected void onActionModeDestroy() {
        // use this to disable all selection
        viewModel.getLessonService().updateSelectAll(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.lessons));
        ((NotebookActivity)requireActivity()).hideBottomNavigationMenu();
        sharedViewModel.setSelectedLessonId(NotebookSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setSelectedWordId(NotebookSharedViewModel.NO_ITEM_SELECTED);
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
                viewModel.getLessonService().updateSelectAll(viewModel.isAllItemsAreSelected());
                Utilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });

        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_guest);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteSelectedItems();
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
        sharedViewModel = new ViewModelProvider(requireActivity()).get(NotebookSharedViewModel.class);

        viewModel.setAdapter(new LessonsAdapter(new Callbacks.FragmentGeneralCallback<GuestLessonsFragment>() {
            @Override
            public GuestLessonsFragment getFragment() {
                return GuestLessonsFragment.this;
            }
        }));

        // set observers
        viewModel.getLessonService().getLiveSelectedItemsCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionMode != null){
                    actionMode.setTitle(getString(R.string.selected) + " " + integer);
                }
            }
        });

        viewModel.getLessonService().getAllLiveSampleLessons().observe(this, new Observer<List<Lesson>>() {
            @Override
            public void onChanged(List<Lesson> lessons) {
                Utilities.Activities.changeTextViewStatus(lessons.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(lessons);
                }
            }
        });
    }

    protected void showAddLessonDialog(){
        DialogFragment dialogFragment = new LessonDialog(new LessonDialog.Callback() {
            @Override
            public void onAddLesson(@NonNull @NotNull Lesson lesson) {
                GeneralUtilities.showShortToastMessage(GuestLessonsFragment.this.requireContext(),
                        viewModel.getLessonService().tryToAddOrUpdateNewLesson(lesson, false).getInfo());
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "LessonsActivity");
    }


    public void goToHomeLessonFragment(Lesson lesson){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lesson == null || lesson.getLessonId() == NotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLessonId(lesson.getLessonId());
        ((NotebookActivity)requireActivity()).goToHomeLessonFragment();
    }

}