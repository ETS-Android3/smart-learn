package com.smart_learn.presenter.guest.activities.notebook.fragments.home_word;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.guest.services.GuestWordService;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.guest.room.entitites.Word;
import com.smart_learn.data.common.entities.Translation;
import com.smart_learn.presenter.common.activities.notebook.fragments.home_word.HomeWordViewModel;
import com.smart_learn.core.common.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;

public class GuestHomeWordViewModel extends HomeWordViewModel {

    @Getter
    protected final MutableLiveData<Word> liveWord;

    public GuestHomeWordViewModel(@NonNull @NotNull Application application) {
        super(application);
        liveWord = new MutableLiveData<>(Word.generateEmptyObject());
    }

    public void setLiveWord(Word word){
        liveWord.setValue(word);
        liveWordValue.setValue(word.getWord());
        liveIsOwner.setValue(true);
        liveWordPhonetic.setValue(word.getPhonetic());
        liveWordNotes.setValue(word.getNotes());
        allTranslations = word.getTranslations();
        if(adapter != null){
            adapter.setItems(allTranslations);
        }
    }

    @Override
    protected void saveWordValue(String newValue) {
        Word word = liveWord.getValue();
        if(word == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_update_word));
            return;
        }

        word.setWord(newValue);

        GuestWordService.getInstance().update(word, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_update_word));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_word));
            }
        });
    }

    @Override
    protected void saveWordPhonetic(String newValue) {
        Word word = liveWord.getValue();
        if(word == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_update_phonetic));
            return;
        }

        word.setPhonetic(newValue);

        GuestWordService.getInstance().update(word, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_update_phonetic));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_phonetic));
            }
        });
    }

    @Override
    protected void saveWordNotes(String newValue) {
        Word word = liveWord.getValue();
        if(word == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            return;
        }

        word.setNotes(newValue);

        GuestWordService.getInstance().update(word, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_update_notes));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            }
        });
    }

    @Override
    protected void addWordTranslation(Translation translation) {
        Word word = liveWord.getValue();
        if(word == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_add_translations));
            return;
        }

        word.getTranslations().add(translation);

        GuestWordService.getInstance().update(word, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_add_translations));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_add_translations));
            }
        });
    }

    @Override
    protected void updateWordTranslations(ArrayList<Translation> newList, @NonNull @NotNull DataCallbacks.General callback){
        Word word = liveWord.getValue();
        if(word == null){
            callback.onFailure();
            return;
        }

        word.setTranslations(newList);

        GuestWordService.getInstance().update(word, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }
}
