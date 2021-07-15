package com.smart_learn.presenter.activities.notebook.helpers.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public abstract class WordsViewModel <AD extends RecyclerView.Adapter<?> & PresenterHelpers.AdapterHelper> extends BasicViewModelForRecyclerView<AD> {

    public WordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public abstract void addWord(String wordValue, String phonetic, String notes, ArrayList<Translation> translations);
}