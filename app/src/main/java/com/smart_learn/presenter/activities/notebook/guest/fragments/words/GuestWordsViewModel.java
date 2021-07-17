package com.smart_learn.presenter.activities.notebook.guest.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.words.helpers.WordsAdapter;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.words.WordsViewModel;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class GuestWordsViewModel extends WordsViewModel<WordsAdapter> {

    @Getter
    @Setter
    private int currentLessonId;
    @Getter
    @Setter
    private boolean allItemsAreSelected;

    public GuestWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        allItemsAreSelected = false;
        currentLessonId = GuestNotebookSharedViewModel.NO_ITEM_SELECTED;
    }

    @Override
    public void addWord(String wordValue, String phonetic, String notes, ArrayList<Translation> translations) {
        if(currentLessonId == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_adding_word));
            return;
        }

        Word newWord = new Word(
                notes,
                false,
                new BasicInfo(System.currentTimeMillis()),
                currentLessonId,
                false,
                "",
                translations,
                wordValue,
                phonetic
        );

        GuestWordService.getInstance().insert(newWord, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_adding_word));
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_adding_word));
            }
        });
    }
}
