package com.smart_learn.presenter.activities.notebook.user.fragments.words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.UserWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.words.WordsViewModel;
import com.smart_learn.presenter.activities.notebook.user.fragments.UserNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.user.fragments.words.helpers.WordsAdapter;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class UserWordsViewModel extends WordsViewModel<WordsAdapter> {

    @Getter
    @Setter
    private DocumentSnapshot currentLessonSnapshot;
    private final AtomicBoolean isDeleting;

    public UserWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
        currentLessonSnapshot = UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED;
        isDeleting = new AtomicBoolean(false);
    }

    @Override
    public void addWord(String wordValue, String phonetic, String notes, ArrayList<Translation> translations) {
        if(currentLessonSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
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
        if(isDeleting.get()){
            return;
        }
        isDeleting.set(true);

        if(currentLessonSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
            Timber.w("currentLessonSnapshot is not set");
            isDeleting.set(false);
            return;
        }

        if(adapter == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
            Timber.w("adapter is null");
            isDeleting.set(false);
            return;
        }

        if(adapter.getSelectedWords().isEmpty()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.no_selected_word));
            isDeleting.set(false);
            return;
        }

        UserWordService.getInstance().deleteWordList(currentLessonSnapshot, adapter.getSelectedWords(), new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_deleting_words));
                if (adapter != null) {
                    adapter.resetSelectedItems();
                }
                isDeleting.set(false);
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_deleting_words));
                isDeleting.set(false);
            }
        });
    }
}
