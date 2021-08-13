package com.smart_learn.presenter.activities.notebook.user.fragments.home_word;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.word.UserWordService;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.HomeWordViewModel;
import com.smart_learn.core.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserHomeWordViewModel extends HomeWordViewModel {

    private DocumentSnapshot wordSnapshot;

    public UserHomeWordViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public void setLiveWord(DocumentSnapshot newSnapshot, WordDocument newWord){
        wordSnapshot = newSnapshot;
        liveIsOwner.setValue(newWord.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid()));
        liveIsFromSharedLesson.setValue(newWord.isFromSharedLesson());
        liveCreatedBy.setValue(newWord.getOwnerDisplayName());
        liveWordValue.setValue(newWord.getWord());
        liveWordPhonetic.setValue(newWord.getPhonetic());
        liveWordNotes.setValue(newWord.getNotes());
        allTranslations = Translation.fromJsonToList(newWord.getTranslations());
        if(adapter != null){
            adapter.setItems(allTranslations);
        }
    }


    @Override
    protected void saveWordValue(String newValue) {
        UserWordService.getInstance().updateWordValue(newValue, wordSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_update_word));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_word));
            }
        });
    }

    @Override
    protected void saveWordPhonetic(String newValue) {
        UserWordService.getInstance().updateWordPhonetic(newValue, wordSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_update_phonetic));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_phonetic));
            }
        });
    }

    @Override
    protected void saveWordNotes(String newValue) {
        UserWordService.getInstance().updateWordNotes(newValue, wordSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.succes_update_notes));
            }
            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_update_notes));
            }
        });
    }

    @Override
    protected void addWordTranslation(Translation translation) {
        ArrayList<Translation> newTranslations = new ArrayList<>(allTranslations);
        newTranslations.add(translation);

        UserWordService.getInstance().updateWordTranslations(newTranslations, wordSnapshot, new DataCallbacks.General() {
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
        UserWordService.getInstance().updateWordTranslations(newList, wordSnapshot, new DataCallbacks.General() {
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
