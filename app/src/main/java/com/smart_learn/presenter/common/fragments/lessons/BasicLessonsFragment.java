package com.smart_learn.presenter.common.fragments.lessons;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;


public abstract class BasicLessonsFragment <T, VM extends BasicLessonsViewModel<?>> extends BasicFragmentForRecyclerView<VM> {

    protected void onAdapterSimpleClick(@NonNull @NotNull T item){}
    protected void onAdapterLongClick(@NonNull @NotNull T item){}
    protected void onAdapterDeleteLessonAlert(int wordsNr, int expressionsNr){}

    @Override
    protected boolean useSearchOnMenu() {
        return true;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_lessons;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.lessons;
    }

    protected void deleteLessonAlert(int wordsNr, int expressionsNr) {
        if(wordsNr == 0 && expressionsNr == 0){
            return;
        }

        String message = "";
        if(wordsNr != 0 && expressionsNr != 0){
            message = getString(R.string.delete_lesson_description_1) + " " + wordsNr + " " + getString(R.string.delete_lesson_description_2) + " " +
                    getString(R.string.delete_lesson_description_3) + " " + expressionsNr + " " + getString(R.string.delete_lesson_description_4) + " " +
                    getString(R.string.delete_lesson_description_5);
        }
        else{
            if(wordsNr == 0){
                message = getString(R.string.delete_lesson_description_1) + " " + expressionsNr + " " + getString(R.string.delete_lesson_description_4) + " " +
                        getString(R.string.delete_lesson_description_5);
            }
            if(expressionsNr == 0){
                message = getString(R.string.delete_lesson_description_1) + " " + wordsNr + " " + getString(R.string.delete_lesson_description_2) + " " +
                        getString(R.string.delete_lesson_description_5);
            }
        }

        PresenterUtilities.Activities.showStandardInfoDialog(requireContext(), getString(R.string.delete_lesson), message);
    }

}