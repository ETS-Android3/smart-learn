package com.smart_learn.presenter.activities.notebook.user.fragments.lessons;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.UserLessonService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.LayoutBottomSheetShowLessonsOptionsBinding;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.LessonsFragment;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.adapters.lessons.UserLessonsAdapter;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public class UserLessonsFragment extends LessonsFragment<UserLessonsViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserLessonsViewModel> getModelClassForViewModel() {
        return UserLessonsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(TextUtils.isEmpty(newText)){
            viewModel.getAdapter().setInitialOption(UserLessonsFragment.this);
        }
        else {
            viewModel.getAdapter().setFilterOption(UserLessonsFragment.this, newText);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_layout_with_recycler_view, menu);

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
                menu.setGroupVisible(R.id.secondary_group_menu_layout_with_recycler_view, false);
                unsetValueFromEmptyLabel();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.setGroupVisible(R.id.secondary_group_menu_layout_with_recycler_view, true);
                resetValueFromEmptyLabel();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == android.R.id.home){
            requireActivity().onBackPressed();
            return true;
        }
        if(id == R.id.action_filter_menu_layout_with_recycler_view){
             showFilterOptionsDialog();
            return true;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.setSelectedLesson(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED);
        sharedViewModel.setSelectedWord(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED);
        sharedViewModel.setSelectedExpression(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

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
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);

        // set adapter
        viewModel.setAdapter(new UserLessonsAdapter(new UserLessonsAdapter.Callback() {
            @Override
            public void onDeleteLessonAlert(int wordsNr, int expressionsNr) {
                deleteLessonAlert(wordsNr, expressionsNr);
            }

            @Override
            public void onShareLessonClick(@NonNull @NotNull DocumentSnapshot lessonSnapshot) {
                shareLesson(lessonSnapshot);
            }

            @Override
            public boolean showCheckedIcon() {
                return false;
            }

            @Override
            public boolean showToolbar() {
                return true;
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                goToUserHomeLessonFragment(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {

            }

            @Override
            public void updateSelectedItemsCounter(int value) {

            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return UserLessonsFragment.this;
            }
        }));
    }


    private void goToUserHomeLessonFragment(DocumentSnapshot lessonSnapshot){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lessonSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson document id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLesson(lessonSnapshot);
        ((UserNotebookActivity)requireActivity()).goToUserHomeLessonFragment();
    }


    private void showFilterOptionsDialog(){

        // create dialog and load layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.requireContext(), R.style.AppTheme_BottomSheetDialogTheme);

        LayoutBottomSheetShowLessonsOptionsBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this.requireContext()),
                R.layout.layout_bottom_sheet_show_lessons_options,null, false);
        bottomSheetBinding.setLifecycleOwner(this);

        // set selected button (by default all are unselected)
        bottomSheetBinding.setIsAllSelected(false);
        bottomSheetBinding.setIsLocalSelected(false);
        bottomSheetBinding.setIsReceivedSelected(false);
        bottomSheetBinding.setIsSharedSelected(false);

        int option = SettingsService.getInstance().getUserLessonShowOption();
        switch (option){
            case UserLessonService.SHOW_ONLY_LOCAL_LESSONS:
                bottomSheetBinding.setIsLocalSelected(true);
                break;
            case UserLessonService.SHOW_ONLY_RECEIVED_LESSONS:
                bottomSheetBinding.setIsReceivedSelected(true);
                break;
            case UserLessonService.SHOW_ONLY_SHARED_LESSONS:
                bottomSheetBinding.setIsSharedSelected(true);
                break;
            case UserLessonService.SHOW_ALL_LESSONS:
            default:
                bottomSheetBinding.setIsAllSelected(true);
                break;
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnShowAllLayoutBottomSheetShowLessonsOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsAllSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                viewModel.setNewOptionForShowingLessons(UserLessonsFragment.this, UserLessonService.SHOW_ALL_LESSONS);
            }
        });

        bottomSheetBinding.btnShowLocalLayoutBottomSheetShowLessonsOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsLocalSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                viewModel.setNewOptionForShowingLessons(UserLessonsFragment.this, UserLessonService.SHOW_ONLY_LOCAL_LESSONS);
            }
        });

        bottomSheetBinding.btnShowReceivedLayoutBottomSheetShowLessonsOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsReceivedSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                viewModel.setNewOptionForShowingLessons(UserLessonsFragment.this, UserLessonService.SHOW_ONLY_RECEIVED_LESSONS);
            }
        });

        bottomSheetBinding.btnShowSharedLayoutBottomSheetShowLessonsOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsSharedSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                viewModel.setNewOptionForShowingLessons(UserLessonsFragment.this, UserLessonService.SHOW_ONLY_SHARED_LESSONS);
            }
        });
    }

    public void shareLesson(@NonNull @NotNull DocumentSnapshot lessonSnapshot){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lessonSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson document id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLesson(lessonSnapshot);
        ((UserNotebookActivity)requireActivity()).goToSelectFriendsFragment();
    }

}