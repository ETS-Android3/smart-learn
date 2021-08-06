package com.smart_learn.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.firebase.firestore.entities.LessonEntranceDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicNotebookCommonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class UserWordRepository extends BasicFirestoreRepository<WordDocument> {

    private static UserWordRepository instance;

    private UserWordRepository() {

    }

    public static UserWordRepository getInstance() {
        if(instance == null){
            instance = new UserWordRepository();
        }
        return instance;
    }

    public Query getQueryForAllLessonWords(String lessonDocumentId, long limit, boolean isFromSharedLesson) {
        return getWordsCollectionReference(lessonDocumentId, isFromSharedLesson)
                .orderBy(WordDocument.Fields.WORD_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForAllLessonWords(String lessonDocumentId, boolean isFromSharedLesson) {
        return getWordsCollectionReference(lessonDocumentId, isFromSharedLesson);
    }

    public Query getQueryForFilterForLessonWords(String lessonDocumentId, long limit, boolean isFromSharedLesson, @NonNull @NotNull String value) {
        return getWordsCollectionReference(lessonDocumentId, isFromSharedLesson)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(WordDocument.Fields.WORD_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public CollectionReference getWordsCollectionReference(String lessonDocumentId, boolean isFromSharedLesson){
        if(isFromSharedLesson){
            return FirebaseFirestore.getInstance()
                    .collection("/" + COLLECTION_SHARED_LESSONS + "/" + lessonDocumentId + "/" + COLLECTION_WORDS);
        }

        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_USERS + "/" + UserService.getInstance().getUserUid() + "/" +
                        COLLECTION_LESSONS + "/" + lessonDocumentId + "/" + COLLECTION_WORDS);
    }

    public void addWord(@NonNull @NotNull DocumentReference lessonReference,
                             @NonNull @NotNull WordDocument word,
                             @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddWord(lessonReference, word, callback));
    }

    private void tryToAddWord(@NonNull @NotNull DocumentReference lessonReference,
                                   @NonNull @NotNull WordDocument word,
                                   @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add word in user words for specific lesson
        word.getDocumentMetadata().setCounted(true);
        word.setFromSharedLesson(false);
        DocumentReference newWordDocRef = getWordsCollectionReference(lessonReference.getId(), false).document();
        batch.set(newWordDocRef, WordDocument.convertDocumentToHashMap(word));

        // 2. Update counter on lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Update counter on user document
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 4. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void addSharedLessonWord(@NonNull @NotNull DocumentReference lessonReference,
                                    @NonNull @NotNull WordDocument word,
                                    @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddSharedLessonWord(lessonReference, word, callback));
    }

    private void tryToAddSharedLessonWord(@NonNull @NotNull DocumentReference lessonReference,
                                          @NonNull @NotNull WordDocument word,
                                          @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add word in words for specific shared lesson
        word.getDocumentMetadata().setCounted(true);
        word.setFromSharedLesson(true);
        word.setOwnerDisplayName(UserService.getInstance().getUserDisplayName());
        DocumentReference newWordDocRef = getWordsCollectionReference(lessonReference.getId(), true).document();
        batch.set(newWordDocRef, WordDocument.convertDocumentToHashMap(word));

        // 2. Update counter on shared lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void updateWordValue(@NonNull @NotNull String newValue, @NonNull @NotNull DocumentSnapshot wordSnapshot,
                                @NonNull @NotNull DataCallbacks.General callback){
        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add(newValue);
        HashMap<String, Object> data = new HashMap<>();
        data.put(DocumentMetadata.Fields.SEARCH_LIST_FIELD_NAME, CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues));
        data.put(WordDocument.Fields.WORD_FIELD_NAME, newValue);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, wordSnapshot, callback);
    }


    public void updateWordPhonetic(@NonNull @NotNull String newValue, @NonNull @NotNull DocumentSnapshot wordSnapshot,
                                    @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
        data.put(WordDocument.Fields.PHONETIC_FIELD_NAME, newValue);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, wordSnapshot, callback);
    }

    public void updateWordNotes(@NonNull @NotNull String newNotes, @NonNull @NotNull DocumentSnapshot wordSnapshot,
                                @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
        data.put(BasicNotebookCommonDocument.Fields.NOTES_FIELD_NAME, newNotes);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, wordSnapshot, callback);
    }

    public void updateWordTranslations(@NonNull @NotNull ArrayList<Translation> newTranslationList,
                                       @NonNull @NotNull DocumentSnapshot wordSnapshot,
                                       @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
        data.put(LessonEntranceDocument.Fields.TRANSLATIONS_FIELD_NAME, Translation.fromListToJson(newTranslationList));
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, wordSnapshot, callback);
    }

    public void deleteSharedLessonWord(@NonNull @NotNull DocumentReference lessonReference,
                                       @NonNull @NotNull DocumentReference wordReference,
                                       @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteSharedLessonWord(lessonReference, wordReference, callback));
    }


    private void tryToDeleteSharedLessonWord(@NonNull @NotNull DocumentReference lessonReference,
                                             @NonNull @NotNull DocumentReference wordReference,
                                             @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete word from shared lessons collection
        batch.delete(wordReference);

        // 2. Update counter on shared lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(-1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void deleteWord(@NonNull @NotNull DocumentReference lessonReference,
                           @NonNull @NotNull DocumentReference wordReference,
                           @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteWord(lessonReference, wordReference, callback));
    }

    private void tryToDeleteWord(@NonNull @NotNull DocumentReference lessonReference,
                                 @NonNull @NotNull DocumentReference wordReference,
                                 @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete word from lessons collection
        batch.delete(wordReference);

        // 2. Update counter on lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(-1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Update counter on user document
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(-1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 4. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void deleteWordList(@NonNull @NotNull DocumentReference lessonReference,
                               @NonNull @NotNull ArrayList<DocumentReference> wordReferenceList,
                               @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteWordList(lessonReference, wordReferenceList, callback));
    }

    private void tryToDeleteWordList(@NonNull @NotNull DocumentReference lessonReference,
                                     @NonNull @NotNull ArrayList<DocumentReference> wordReferenceList,
                                     @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete every word from lessons collection
        // FIXME: batch can have only 500 operations.
        for(DocumentReference wordRef : wordReferenceList){
            batch.delete(wordRef);
        }

        int decrementValue = -1 * wordReferenceList.size();

        // 2. Update counter on lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(decrementValue));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Update counter on user document
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(decrementValue));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 4. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void deleteSharedLessonWordList(@NonNull @NotNull DocumentReference lessonReference,
                                           @NonNull @NotNull ArrayList<DocumentReference> wordReferenceList,
                                           @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteSharedLessonWordList(lessonReference, wordReferenceList, callback));
    }

    private void tryToDeleteSharedLessonWordList(@NonNull @NotNull DocumentReference lessonReference,
                                                 @NonNull @NotNull ArrayList<DocumentReference> wordReferenceList,
                                                 @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete every word from shared lessons words collection
        // FIXME: batch can have only 500 operations.
        for(DocumentReference wordRef : wordReferenceList){
            batch.delete(wordRef);
        }

        int decrementValue = -1 * wordReferenceList.size();

        // 2. Update counter on shared lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_WORDS_FIELD_NAME, FieldValue.increment(decrementValue));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }
}
