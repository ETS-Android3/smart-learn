package com.smart_learn.presenter.user.fragments.common.lessons.standard;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smart_learn.R;
import com.smart_learn.core.common.services.SettingsService;
import com.smart_learn.core.user.services.UserLessonService;
import com.smart_learn.databinding.LayoutBottomSheetShowLessonsOptionsBinding;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.lessons.helpers.LessonDialog;
import com.smart_learn.presenter.user.fragments.common.lessons.UserBasicLessonsFragment;

import org.jetbrains.annotations.NotNull;


public abstract class UserStandardLessonsFragment <VM extends UserStandardLessonsViewModel> extends UserBasicLessonsFragment<VM> {

    protected void addNewEmptySharedLesson(@NonNull @NotNull String lessonName){}

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
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }

    @Override
    protected void onAdapterDeleteLessonAlert(int wordsNr, int expressionsNr) {
        deleteLessonAlert(wordsNr, expressionsNr);
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
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.local_lessons));
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
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.personal_lessons));
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
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.received_lessons));
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
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.common_lessons));
            }
        });
    }

    private void showAddLessonDialog(){
        DialogFragment dialogFragment = new LessonDialog(new LessonDialog.Callback() {
            @Override
            public void onAddLesson(@NonNull @NotNull String lessonName) {
                int option = SettingsService.getInstance().getUserLessonShowOption();
                switch (option){
                    case UserLessonService.SHOW_ONLY_SHARED_LESSONS:
                        addNewEmptySharedLesson(lessonName);
                        break;
                    case UserLessonService.SHOW_ALL_LESSONS:
                    case UserLessonService.SHOW_ONLY_LOCAL_LESSONS:
                    case UserLessonService.SHOW_ONLY_RECEIVED_LESSONS:
                    default:
                        viewModel.addLessonByName(lessonName);
                        break;
                }
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "UserStandardLessonsFragment");
    }

}