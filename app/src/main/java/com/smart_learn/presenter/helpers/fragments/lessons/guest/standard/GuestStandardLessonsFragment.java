package com.smart_learn.presenter.helpers.fragments.lessons.guest.standard;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.smart_learn.R;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.lessons.helpers.LessonDialog;
import com.smart_learn.presenter.helpers.fragments.lessons.guest.GuestBasicLessonsFragment;

import org.jetbrains.annotations.NotNull;

public abstract class GuestStandardLessonsFragment <VM extends GuestStandardLessonsViewModel> extends GuestBasicLessonsFragment<VM> {

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
    protected void onAdapterSimpleClick(@NonNull @NotNull Lesson item) {
    }

    @Override
    protected void onAdapterLongClick(@NonNull @NotNull Lesson item) {

    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {

    }

    @Override
    protected void onAdapterDeleteLessonAlert(int wordsNr, int expressionsNr) {
        deleteLessonAlert(wordsNr, expressionsNr);
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