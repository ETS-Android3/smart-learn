package com.smart_learn.presenter.helpers.fragments.words.guest.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.word.GuestWordService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.fragments.words.guest.GuestBasicWordsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public abstract class GuestStandardWordsViewModel extends GuestBasicWordsViewModel {

    @Getter
    @Setter
    private boolean allItemsAreSelected;
    private final AtomicBoolean isDeletingActive;

    public GuestStandardWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        allItemsAreSelected = false;
        isDeletingActive = new AtomicBoolean(false);
    }

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

    protected void deleteSelectedWords(){
        if(isDeletingActive.get()){
            return;
        }
        isDeletingActive.set(true);

        if(currentLessonId == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
            Timber.w("currentLessonId is not set");
            isDeletingActive.set(false);
            return;
        }

        if(allItemsAreSelected){
            GuestWordService.getInstance().deleteAll(currentLessonId, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_deleting_words));
                    if (adapter != null) {
                        adapter.resetSelectedItems();
                    }
                    isDeletingActive.set(false);
                }

                @Override
                public void onFailure() {
                    liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
                    isDeletingActive.set(false);
                }
            });
            return;
        }

        if(adapter == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
            Timber.w("adapter is null");
            isDeletingActive.set(false);
            return;
        }

        ArrayList<Word> selectedWords = adapter.getSelectedValues();
        if(selectedWords.isEmpty()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.no_selected_word));
            isDeletingActive.set(false);
            return;
        }

        GuestWordService.getInstance().deleteAll(selectedWords, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_deleting_words));
                if (adapter != null) {
                    adapter.resetSelectedItems();
                }
                isDeletingActive.set(false);
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
                isDeletingActive.set(false);
            }
        });
    }
}
