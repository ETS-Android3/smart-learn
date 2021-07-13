package com.smart_learn.presenter.activities.notebook.guest.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.WordService;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.words.WordsViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.words.helpers.WordsAdapter;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

public class GuestWordsViewModel extends WordsViewModel<WordsAdapter> {

    @Getter
    private final WordService wordsService;
    @Getter
    @Setter
    private boolean allItemsAreSelected;

    public GuestWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        wordsService = new WordService(application);
        allItemsAreSelected = false;
    }
}
