package com.smart_learn.presenter.activities.notebook.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smart_learn.core.services.WordService;
import com.smart_learn.presenter.activities.notebook.fragments.words.helpers.WordsAdapter;
import com.smart_learn.presenter.helpers.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;


public class WordsViewModel extends BasicAndroidViewModel {

    @Getter
    private final WordService wordsService;
    @Getter
    @Setter
    @Nullable
    private WordsAdapter wordsAdapter;
    @Getter
    @Setter
    private boolean allItemsAreSelected;

    public WordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        wordsService = new WordService(application);
        allItemsAreSelected = false;
    }
}