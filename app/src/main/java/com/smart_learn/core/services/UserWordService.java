package com.smart_learn.core.services;

import android.text.TextUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.UserWordRepository;
import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;

import timber.log.Timber;

public class UserWordService extends BasicFirestoreService<WordDocument, UserWordRepository> {

    private static UserWordService instance;

    private UserWordService() {
        super(UserWordRepository.getInstance());
    }

    public static UserWordService getInstance() {
        if(instance == null){
            instance = new UserWordService();
        }
        return instance;
    }

    public Query getQueryForAllLessonWords(String lessonDocumentId, long limit) {
        return repositoryInstance.getQueryForAllLessonWords(lessonDocumentId, limit);
    }

    public Query getQueryForFilter(String lessonDocumentId, long limit, String value) {
        if(value == null){
            value = "";
        }
        return repositoryInstance.getQueryForFilterForLessonWords(lessonDocumentId, limit, value);
    }

    public CollectionReference getWordsCollectionReference(String lessonDocumentId){
        return repositoryInstance.getWordsCollectionReference(lessonDocumentId);
    }

    public void addWord(DocumentSnapshot lessonSnapshot, WordDocument wordDocument, DataCallbacks.General callback){
        if (wordDocument == null){
            Timber.w("word is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(TextUtils.isEmpty(wordDocument.getWord())){
            Timber.w("word value must not be null or empty");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Word " + wordDocument.getWord() + " was added",
                    "Word " + wordDocument.getWord() + " was NOT added");
        }

        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            callback.onFailure();
            return;
        }

        repositoryInstance.addWord(lessonSnapshot.getReference(), wordDocument, callback);

    }

    public void updateWordValue(String newValue, DocumentSnapshot wordSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(wordSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Word value for document " + wordSnapshot.getId() + " updated",
                    "Word value for document " + wordSnapshot.getId() + " was NOT updated");
        }

        if(newValue == null || newValue.isEmpty()){
            callback.onFailure();
            Timber.w("newValue can not be null or empty");
            return;
        }

        repositoryInstance.updateWordValue(newValue, wordSnapshot, callback);
    }

    public void updateWordNotes(String newNotes, DocumentSnapshot wordSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(wordSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Word notes for document " + wordSnapshot.getId() + " updated",
                    "Word notes for document " + wordSnapshot.getId() + " was NOT updated");
        }

        if(newNotes == null){
            newNotes = "";
        }

        repositoryInstance.updateWordNotes(newNotes, wordSnapshot, callback);
    }

    public void updateWordTranslations(ArrayList<Translation> newTranslationList, DocumentSnapshot wordSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(wordSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Word translations for document " + wordSnapshot.getId() + " updated",
                    "Word translations for document " + wordSnapshot.getId() + " was NOT updated");
        }

        if(newTranslationList == null){
            newTranslationList = new ArrayList<>();
        }

        repositoryInstance.updateWordTranslations(newTranslationList, wordSnapshot, callback);
    }

    public void deleteWord(DocumentSnapshot lessonSnapshot, DocumentSnapshot wordSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(wordSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + wordSnapshot.getId() + " deleted",
                    "Document " + wordSnapshot.getId() + " was NOT deleted");
        }

        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            callback.onFailure();
            return;
        }

        repositoryInstance.deleteWord(lessonSnapshot.getReference(), wordSnapshot.getReference(), callback);
    }

    public void deleteWordList(DocumentSnapshot lessonSnapshot, ArrayList<DocumentSnapshot> wordSnapshotList, DataCallbacks.General callback){
        if(wordSnapshotList == null || wordSnapshotList.isEmpty()){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("wordSnapshotList can not be null or empty");
            return;
        }

        ArrayList<DocumentReference> wordRefList = new ArrayList<>();
        for(DocumentSnapshot snapshot : wordSnapshotList){
            if(DataUtilities.Firestore.notGoodDocumentSnapshot(snapshot)){
                if(callback != null){
                    callback.onFailure();
                }
                return;
            }
            wordRefList.add(snapshot.getReference());
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Words deleted","Words was NOT deleted");
        }

        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            callback.onFailure();
            return;
        }

        repositoryInstance.deleteWordList(lessonSnapshot.getReference(), wordRefList, callback);
    }
}
