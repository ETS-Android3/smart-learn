package com.smart_learn.presenter.helpers.fragments.words.user.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.word.UserWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.fragments.words.user.UserBasicWordsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public abstract class UserStandardWordsViewModel extends UserBasicWordsViewModel {

    private final AtomicBoolean isDeletingActive;

    public UserStandardWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        isDeletingActive = new AtomicBoolean(false);
    }

    public void addWord(boolean isSharedLessonSelected, String wordValue, String phonetic, String notes, ArrayList<Translation> translations) {
        if(currentLessonSnapshot == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_adding_word));
            return;
        }

        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add(wordValue);

        WordDocument newWord = new WordDocument(
                new DocumentMetadata(
                        UserService.getInstance().getUserUid(),
                        System.currentTimeMillis(),
                        CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues)),
                notes,
                false,
                "",
                isSharedLessonSelected,
                Translation.fromListToJson(translations),
                wordValue,
                phonetic
        );

        UserWordService.getInstance().addWord(currentLessonSnapshot, newWord, new DataCallbacks.General() {
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

        if(currentLessonSnapshot == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
            Timber.w("currentLessonSnapshot is not set");
            isDeletingActive.set(false);
            return;
        }

        if(adapter == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
            Timber.w("adapter is null");
            isDeletingActive.set(false);
            return;
        }

        ArrayList<DocumentSnapshot> selectedWords = adapter.getSelectedValues();
        if(selectedWords.isEmpty()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.no_selected_word));
            isDeletingActive.set(false);
            return;
        }

        UserWordService.getInstance().deleteWordList(currentLessonSnapshot, selectedWords, new DataCallbacks.General() {
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
