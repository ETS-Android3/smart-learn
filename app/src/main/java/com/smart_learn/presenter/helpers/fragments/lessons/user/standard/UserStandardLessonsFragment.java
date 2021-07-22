package com.smart_learn.presenter.helpers.fragments.lessons.user.standard;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.UserLessonService;
import com.smart_learn.databinding.LayoutBottomSheetShowLessonsOptionsBinding;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.helpers.LessonDialog;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.lessons.user.UserBasicLessonsFragment;

import org.jetbrains.annotations.NotNull;


public abstract class UserStandardLessonsFragment <VM extends UserStandardLessonsViewModel> extends UserBasicLessonsFragment<VM> {

    @Override
    protected int getFloatingActionButtonIconResourceId() {
        return R.drawable.ic_baseline_plus_24;
    }

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected void onFloatingActionButtonPress() {
        showAddLessonDialog();
    }

    @Override
    protected boolean useToolbarMenu() {
        return true;
    }

    @Override
    protected boolean onAdapterShowCheckedIcon() {
        return false;
    }

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
    }

    @Override
    protected void onAdapterLongClick(@NonNull @NotNull DocumentSnapshot item) {

    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {

    }

    @Override
    protected void onAdapterDeleteLessonAlert(int wordsNr, int expressionsNr) {
        deleteLessonAlert(wordsNr, expressionsNr);
    }

    @Override
    protected void onAdapterShareLessonClick(@NonNull @NotNull DocumentSnapshot lessonSnapshot) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == android.R.id.home){
            requireActivity().onBackPressed();
            return true;
        }
        if(id == R.id.action_filter_menu_toolbar_fragment_lessons){
            showFilterOptionsDialog();
            return true;
        }
        return true;
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
                viewModel.setNewOptionForShowingLessons(UserStandardLessonsFragment.this, UserLessonService.SHOW_ALL_LESSONS);
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
                viewModel.setNewOptionForShowingLessons(UserStandardLessonsFragment.this, UserLessonService.SHOW_ONLY_LOCAL_LESSONS);
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
                viewModel.setNewOptionForShowingLessons(UserStandardLessonsFragment.this, UserLessonService.SHOW_ONLY_RECEIVED_LESSONS);
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
                viewModel.setNewOptionForShowingLessons(UserStandardLessonsFragment.this, UserLessonService.SHOW_ONLY_SHARED_LESSONS);
            }
        });
    }

    private void showAddLessonDialog(){
        DialogFragment dialogFragment = new LessonDialog(new LessonDialog.Callback() {
            @Override
            public void onAddLesson(@NonNull @NotNull String lessonName) {
                viewModel.addLessonByName(lessonName);
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "GuestStandardLessonsFragment");
    }

}