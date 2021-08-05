package com.smart_learn.presenter.activities.notebook.user.fragments.participants;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.adapters.lessons.shared_lesson.SharedLessonParticipantsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;


public class SharedLessonParticipantsFragment extends BasicFragmentForRecyclerView<SharedLessonParticipantsViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<SharedLessonParticipantsViewModel> getModelClassForViewModel() {
        return SharedLessonParticipantsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.participants;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_participants;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserNotebookActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        UserNotebookSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);

        if (sharedViewModel.getSelectedSharedLessonParticipants() == null || sharedViewModel.getSelectedSharedLessonParticipants().isEmpty()) {
            showMessage(R.string.error_can_not_continue);
            requireActivity().onBackPressed();
            return;
        }

        viewModel.setAdapter(new SharedLessonParticipantsAdapter(sharedViewModel.getSelectedSharedLessonParticipants(), new SharedLessonParticipantsAdapter.Callback() {
            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                // no action needed here
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {
                // no action needed here
            }

            @Override
            public boolean showCheckedIcon() {
                return false;
            }

            @Override
            public boolean showToolbar() {
                return false;
            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                // no action needed here
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return SharedLessonParticipantsFragment.this;
            }
        }));
    }

}