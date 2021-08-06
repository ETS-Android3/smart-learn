package com.smart_learn.core.services;

import android.text.TextUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
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

    public CollectionReference getWordsCollectionReference(String lessonDocumentId, boolean isFromSharedLesson){
        return repositoryInstance.getWordsCollectionReference(lessonDocumentId, isFromSharedLesson);
    }

    public Query getQueryForAllLessonWords(String lessonDocumentId, long limit, boolean isSharedLesson) {
        return repositoryInstance.getQueryForAllLessonWords(lessonDocumentId, limit, isSharedLesson);
    }

    public Query getQueryForAllLessonWords(String lessonDocumentId, boolean isSharedLesson) {
        return repositoryInstance.getQueryForAllLessonWords(lessonDocumentId, isSharedLesson);
    }

    public Query getQueryForFilter(String lessonDocumentId, long limit,  boolean isSharedLesson, String value) {
        if(value == null){
            value = "";
        }
        return repositoryInstance.getQueryForFilterForLessonWords(lessonDocumentId, limit, isSharedLesson, value);
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

        if(wordDocument.isFromSharedLesson()){
            repositoryInstance.addSharedLessonWord(lessonSnapshot.getReference(), wordDocument, callback);
        }
        else{
            repositoryInstance.addWord(lessonSnapshot.getReference(), wordDocument, callback);
        }

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

    public void updateWordPhonetic(String newValue, DocumentSnapshot wordSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(wordSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Word phonetic for document " + wordSnapshot.getId() + " updated",
                    "Word phonetic for document " + wordSnapshot.getId() + " was NOT updated");
        }

        if(newValue == null){
            newValue = "";
        }

        repositoryInstance.updateWordPhonetic(newValue, wordSnapshot, callback);
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

        for(Translation item : newTranslationList){
            if(item == null){
                callback.onFailure();
                Timber.w("item is null");
                return;
            }
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

        WordDocument word = wordSnapshot.toObject(WordDocument.class);
        if(word == null){
            callback.onFailure();
            Timber.w("word is null");
            return;
        }

        if(word.isFromSharedLesson()){
            repositoryInstance.deleteSharedLessonWord(lessonSnapshot.getReference(), wordSnapshot.getReference(), callback);
        }
        else{
            repositoryInstance.deleteWord(lessonSnapshot.getReference(), wordSnapshot.getReference(), callback);
        }

    }

    public void deleteWordList(DocumentSnapshot lessonSnapshot, ArrayList<DocumentSnapshot> wordSnapshotList, DataCallbacks.General callback){
        if(wordSnapshotList == null || wordSnapshotList.isEmpty()){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("wordSnapshotList can not be null or empty");
            return;
        }

        LessonDocument lessonDocument = lessonSnapshot.toObject(LessonDocument.class);
        if(lessonDocument == null){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("lessonDocument is null");
            return;
        }

        boolean isSharedLesson = lessonDocument.getType() == LessonDocument.Types.SHARED;

        ArrayList<DocumentReference> wordRefList = new ArrayList<>();
        for(DocumentSnapshot snapshot : wordSnapshotList){
            if(DataUtilities.Firestore.notGoodDocumentSnapshot(snapshot)){
                if(callback != null){
                    callback.onFailure();
                }
                return;
            }

            WordDocument word = snapshot.toObject(WordDocument.class);
            if(word == null){
                callback.onFailure();
                Timber.w("word is null");
                return;
            }

            if((word.isFromSharedLesson() && !isSharedLesson) || (!word.isFromSharedLesson() && isSharedLesson)){
                callback.onFailure();
                Timber.w("incompatible word and lesson");
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

        if(isSharedLesson){
            repositoryInstance.deleteSharedLessonWordList(lessonSnapshot.getReference(), wordRefList, callback);
        }
        else{
            repositoryInstance.deleteWordList(lessonSnapshot.getReference(), wordRefList, callback);
        }
    }
}
