package com.smart_learn.presenter.activities.notebook.guest.fragments;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.activities.notebook.helpers.NotebookSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * This SharedViewModel will be used to share lessonId and to set currentVisibleFragment,
 * being used by HomeLessonFragment, WordsFragment ... TODO: complete this .
 * */
public class GuestNotebookSharedViewModel extends NotebookSharedViewModel {

    public static int NO_ITEM_SELECTED = -1;

    // Is set when a lesson is clicked in LessonsFragment and navigation to HomeFragment is made.
    // This will allow to the secondary fragments like WordsFragment to know from which lesson to
    // to load words.
    @Getter
    @Setter
    private int selectedLessonId;
    // Is set when a word is clicked in WordsFragment and navigation to WordHomeFragment is made.
    // This will allow the WordHomeFragment to know what word to load.
    @Getter
    @Setter
    private int selectedWordId;
    // Is set when an expression is clicked in ExpressionsFragment and navigation to ExpressionHomeFragment is made.
    // This will allow the ExpressionHomeFragment to know what expression to load.
    @Getter
    @Setter
    private int selectedExpressionId;

    public GuestNotebookSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
