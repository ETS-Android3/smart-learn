package com.smart_learn.data.user.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.core.common.services.ThreadExecutorService;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.user.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.user.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.user.firebase.firestore.entities.LessonEntranceDocument;
import com.smart_learn.data.user.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.BasicNotebookCommonDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.user.repository.helpers.BasicFirestoreRepository;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.common.entities.Translation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class UserExpressionRepository extends BasicFirestoreRepository<ExpressionDocument> {

    private static UserExpressionRepository instance;

    private UserExpressionRepository() {

    }

    public static synchronized UserExpressionRepository getInstance() {
        if(instance == null){
            instance = new UserExpressionRepository();
        }
        return instance;
    }

    public Query getQueryForAllLessonExpressions(String lessonDocumentId, long limit, boolean isFromSharedLesson) {
        return getExpressionsCollectionReference(lessonDocumentId, isFromSharedLesson)
                .orderBy(ExpressionDocument.Fields.EXPRESSION_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForAllLessonExpressions(String lessonDocumentId, boolean isFromSharedLesson) {
        return getExpressionsCollectionReference(lessonDocumentId, isFromSharedLesson);
    }

    public Query getQueryForFilterForLessonExpressions(String lessonDocumentId, long limit, boolean isFromSharedLesson, @NonNull @NotNull String value) {
        return getExpressionsCollectionReference(lessonDocumentId, isFromSharedLesson)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(ExpressionDocument.Fields.EXPRESSION_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public CollectionReference getExpressionsCollectionReference(String lessonDocumentId, boolean isFromSharedLesson){
        if(isFromSharedLesson){
            return FirebaseFirestore.getInstance()
                    .collection("/" + COLLECTION_SHARED_LESSONS + "/" + lessonDocumentId + "/" + COLLECTION_EXPRESSIONS);
        }

        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_USERS + "/" + UserService.getInstance().getUserUid() + "/" +
                        COLLECTION_LESSONS + "/" + lessonDocumentId + "/" + COLLECTION_EXPRESSIONS);
    }

    public void addExpression(@NonNull @NotNull DocumentReference lessonReference,
                              @NonNull @NotNull ExpressionDocument expression,
                              @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddExpression(lessonReference, expression, callback));
    }

    private void tryToAddExpression(@NonNull @NotNull DocumentReference lessonReference,
                                    @NonNull @NotNull ExpressionDocument expression,
                                    @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add expression in user expressions for specific lesson
        expression.getDocumentMetadata().setCounted(true);
        expression.setFromSharedLesson(false);
        DocumentReference newExpressionDocRef = getExpressionsCollectionReference(lessonReference.getId(), false).document();
        batch.set(newExpressionDocRef, ExpressionDocument.convertDocumentToHashMap(expression));

        // 2. Update counter on lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Update counter on user document
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 4. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void addSharedLessonExpression(@NonNull @NotNull DocumentReference lessonReference,
                                         @NonNull @NotNull ExpressionDocument expression,
                                         @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddSharedLessonExpression(lessonReference, expression, callback));
    }

    private void tryToAddSharedLessonExpression(@NonNull @NotNull DocumentReference lessonReference,
                                                @NonNull @NotNull ExpressionDocument expression,
                                                @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add expression in expressions collection for specific shared lesson
        expression.getDocumentMetadata().setCounted(true);
        expression.setFromSharedLesson(true);
        expression.setOwnerDisplayName(UserService.getInstance().getUserDisplayName());
        DocumentReference newExpressionDocRef = getExpressionsCollectionReference(lessonReference.getId(), true).document();
        batch.set(newExpressionDocRef, ExpressionDocument.convertDocumentToHashMap(expression));

        // 2. Update counter on shared lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void updateExpressionValue(@NonNull @NotNull String newValue, @NonNull @NotNull DocumentSnapshot expressionSnapshot,
                                      @NonNull @NotNull DataCallbacks.General callback){
        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add(newValue);
        HashMap<String, Object> data = new HashMap<>();
        data.put(DocumentMetadata.Fields.SEARCH_LIST_FIELD_NAME, CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues));
        data.put(ExpressionDocument.Fields.EXPRESSION_FIELD_NAME, newValue);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, expressionSnapshot, callback);
    }

    public void updateExpressionNotes(@NonNull @NotNull String newNotes, @NonNull @NotNull DocumentSnapshot expressionSnapshot,
                                      @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
        data.put(BasicNotebookCommonDocument.Fields.NOTES_FIELD_NAME, newNotes);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, expressionSnapshot, callback);
    }

    public void updateExpressionTranslations(@NonNull @NotNull ArrayList<Translation> newTranslationList,
                                             @NonNull @NotNull DocumentSnapshot expressionSnapshot,
                                             @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
        data.put(LessonEntranceDocument.Fields.TRANSLATIONS_FIELD_NAME, Translation.fromListToJson(newTranslationList));
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, expressionSnapshot, callback);
    }

    public void deleteExpression(@NonNull @NotNull DocumentReference lessonReference,
                                 @NonNull @NotNull DocumentReference expressionReference,
                                 @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteExpression(lessonReference, expressionReference, callback));
    }

    private void tryToDeleteExpression(@NonNull @NotNull DocumentReference lessonReference,
                                       @NonNull @NotNull DocumentReference expressionReference,
                                       @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete expression from lessons collection
        batch.delete(expressionReference);

        // 2. Update counter on lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(-1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Update counter on user document
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(-1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 4. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void deleteSharedLessonExpression(@NonNull @NotNull DocumentReference lessonReference,
                                             @NonNull @NotNull DocumentReference expressionReference,
                                             @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteSharedLessonExpression(lessonReference, expressionReference, callback));
    }

    private void tryToDeleteSharedLessonExpression(@NonNull @NotNull DocumentReference lessonReference,
                                                    @NonNull @NotNull DocumentReference expressionReference,
                                                    @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete expression from shared lessons collection
        batch.delete(expressionReference);

        // 2. Update counter on shared lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(-1));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void deleteExpressionList(@NonNull @NotNull DocumentReference lessonReference,
                                     @NonNull @NotNull ArrayList<DocumentReference> expressionReferenceList,
                                     @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteExpressionList(lessonReference, expressionReferenceList, callback));
    }

    private void tryToDeleteExpressionList(@NonNull @NotNull DocumentReference lessonReference,
                                           @NonNull @NotNull ArrayList<DocumentReference> expressionReferenceList,
                                           @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete every expression from lessons collection
        // FIXME: batch can have only 500 operations.
        for(DocumentReference expressionRef : expressionReferenceList){
            batch.delete(expressionRef);
        }

        int decrementValue = -1 * expressionReferenceList.size();

        // 2. Update counter on lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(decrementValue));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Update counter on user document
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(decrementValue));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 4. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void deleteSharedLessonExpressionList(@NonNull @NotNull DocumentReference lessonReference,
                                                 @NonNull @NotNull ArrayList<DocumentReference> expressionReferenceList,
                                                 @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteSharedLessonExpressionList(lessonReference, expressionReferenceList, callback));
    }

    private void tryToDeleteSharedLessonExpressionList(@NonNull @NotNull DocumentReference lessonReference,
                                                       @NonNull @NotNull ArrayList<DocumentReference> expressionReferenceList,
                                                       @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete every expression from shared lessons expressions collection
        // FIXME: batch can have only 500 operations.
        for(DocumentReference expressionRef : expressionReferenceList){
            batch.delete(expressionRef);
        }

        int decrementValue = -1 * expressionReferenceList.size();

        // 2. Update counter on shared lesson document
        HashMap<String, Object> lessonData = new HashMap<>();
        lessonData.put(LessonDocument.Fields.NR_OF_EXPRESSIONS_FIELD_NAME, FieldValue.increment(decrementValue));
        lessonData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(lessonReference, lessonData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }
}
