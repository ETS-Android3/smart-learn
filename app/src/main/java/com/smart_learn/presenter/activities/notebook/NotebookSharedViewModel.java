package com.smart_learn.presenter.activities.notebook;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * This SharedViewModel will be used to share lessonId and to set currentVisibleFragment,
 * being used by HomeLessonFragment, WordsFragment ... TODO: complete this .
 * */
public class NotebookSharedViewModel extends BasicAndroidViewModel {

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

    public NotebookSharedViewModel(@NonNull @NotNull Application application) {
        super(application);
    }
}
