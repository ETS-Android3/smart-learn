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
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.firebase.firestore.entities.LessonEntranceDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicNotebookCommonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class UserExpressionRepository extends BasicFirestoreRepository<ExpressionDocument> {

    private static UserExpressionRepository instance;

    private UserExpressionRepository() {

    }

    public static UserExpressionRepository getInstance() {
        if(instance == null){
            instance = new UserExpressionRepository();
        }
        return instance;
    }

    public Query getQueryForAllLessonExpressions(String lessonDocumentId, long limit) {
        return getExpressionsCollectionReference(lessonDocumentId)
                .orderBy(ExpressionDocument.Fields.EXPRESSION_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForLessonExpressions(String lessonDocumentId, long limit, @NonNull @NotNull String value) {
        return getExpressionsCollectionReference(lessonDocumentId)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(ExpressionDocument.Fields.EXPRESSION_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public CollectionReference getExpressionsCollectionReference(String lessonDocumentId){
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
        DocumentReference newExpressionDocRef = getExpressionsCollectionReference(lessonReference.getId()).document();
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

    public void updateExpressionValue(@NonNull @NotNull String newValue, @NonNull @NotNull DocumentSnapshot expressionSnapshot,
                                      @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
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
}
