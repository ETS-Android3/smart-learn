package com.smart_learn.core.services;

import android.text.TextUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.UserExpressionRepository;
import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;

import timber.log.Timber;

public class UserExpressionService extends BasicFirestoreService<ExpressionDocument, UserExpressionRepository> {

    private static UserExpressionService instance;

    private UserExpressionService() {
        super(UserExpressionRepository.getInstance());
    }

    public static UserExpressionService getInstance() {
        if(instance == null){
            instance = new UserExpressionService();
        }
        return instance;
    }

    public Query getQueryForAllLessonExpressions(String lessonDocumentId, long limit,  boolean isSharedLesson) {
        return repositoryInstance.getQueryForAllLessonExpressions(lessonDocumentId, limit, isSharedLesson);
    }

    public Query getQueryForAllLessonExpressions(String lessonDocumentId, boolean isSharedLesson) {
        return repositoryInstance.getQueryForAllLessonExpressions(lessonDocumentId, isSharedLesson);
    }

    public Query getQueryForFilter(String lessonDocumentId, long limit,  boolean isSharedLesson, String value) {
        if(value == null){
            value = "";
        }
        return repositoryInstance.getQueryForFilterForLessonExpressions(lessonDocumentId, limit, isSharedLesson, value);
    }

    public CollectionReference getExpressionsCollectionReference(String lessonDocumentId, boolean isFromSharedLesson){
        return repositoryInstance.getExpressionsCollectionReference(lessonDocumentId, isFromSharedLesson);
    }

    public void addExpression(DocumentSnapshot lessonSnapshot, ExpressionDocument expressionDocument, DataCallbacks.General callback){
        if (expressionDocument == null){
            Timber.w("expressionDocument is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(TextUtils.isEmpty(expressionDocument.getExpression())){
            Timber.w("expression value must not be null or empty");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Expression [" + expressionDocument.getExpression() + "] was added",
                    "Expression [" + expressionDocument.getExpression() + "] was NOT added");
        }

        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            callback.onFailure();
            return;
        }

        if(expressionDocument.isFromSharedLesson()){
            repositoryInstance.addSharedLessonExpression(lessonSnapshot.getReference(), expressionDocument, callback);
        }
        else {
            repositoryInstance.addExpression(lessonSnapshot.getReference(), expressionDocument, callback);
        }
    }

    public void updateExpressionValue(String newValue, DocumentSnapshot expressionSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(expressionSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Expression value for document " + expressionSnapshot.getId() + " updated",
                    "Expression value for document " + expressionSnapshot.getId() + " was NOT updated");
        }

        if(newValue == null || newValue.isEmpty()){
            callback.onFailure();
            Timber.w("newValue can not be null or empty");
            return;
        }

        repositoryInstance.updateExpressionValue(newValue, expressionSnapshot, callback);
    }

    public void updateExpressionNotes(String newNotes, DocumentSnapshot expressionSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(expressionSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Expression notes for document " + expressionSnapshot.getId() + " updated",
                    "Expression notes for document " + expressionSnapshot.getId() + " was NOT updated");
        }

        if(newNotes == null){
            newNotes = "";
        }

        repositoryInstance.updateExpressionNotes(newNotes, expressionSnapshot, callback);
    }

    public void updateExpressionTranslations(ArrayList<Translation> newTranslationList, DocumentSnapshot expressionSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(expressionSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Expression translations for document " + expressionSnapshot.getId() + " updated",
                    "Expression translations for document " + expressionSnapshot.getId() + " was NOT updated");
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

        repositoryInstance.updateExpressionTranslations(newTranslationList, expressionSnapshot, callback);
    }

    public void deleteExpression(DocumentSnapshot lessonSnapshot, DocumentSnapshot expressionSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(expressionSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + expressionSnapshot.getId() + " deleted",
                    "Document " + expressionSnapshot.getId() + " was NOT deleted");
        }

        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            callback.onFailure();
            return;
        }

        ExpressionDocument expression = expressionSnapshot.toObject(ExpressionDocument.class);
        if(expression == null){
            callback.onFailure();
            Timber.w("expression is null");
            return;
        }

        if(expression.isFromSharedLesson()){
            repositoryInstance.deleteSharedLessonExpression(lessonSnapshot.getReference(), expressionSnapshot.getReference(), callback);
        }
        else {
            repositoryInstance.deleteExpression(lessonSnapshot.getReference(), expressionSnapshot.getReference(), callback);
        }

    }

    public void deleteExpressionList(DocumentSnapshot lessonSnapshot, ArrayList<DocumentSnapshot> expressionSnapshotList, DataCallbacks.General callback){
        if(expressionSnapshotList == null || expressionSnapshotList.isEmpty()){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("expressionSnapshot can not be null or empty");
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

        ArrayList<DocumentReference> expressionRefList = new ArrayList<>();
        for(DocumentSnapshot snapshot : expressionSnapshotList){
            if(DataUtilities.Firestore.notGoodDocumentSnapshot(snapshot)){
                if(callback != null){
                    callback.onFailure();
                }
                return;
            }

            ExpressionDocument expression = snapshot.toObject(ExpressionDocument.class);
            if(expression == null){
                callback.onFailure();
                Timber.w("expression is null");
                return;
            }

            if((expression.isFromSharedLesson() && !isSharedLesson) || (!expression.isFromSharedLesson() && isSharedLesson)){
                callback.onFailure();
                Timber.w("incompatible expression and lesson");
                return;
            }

            expressionRefList.add(snapshot.getReference());
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Expressions deleted","Expressions was NOT deleted");
        }

        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            callback.onFailure();
            return;
        }

        if(isSharedLesson){
            repositoryInstance.deleteSharedLessonExpressionList(lessonSnapshot.getReference(), expressionRefList, callback);
        }
        else{
            repositoryInstance.deleteExpressionList(lessonSnapshot.getReference(), expressionRefList, callback);
        }
    }
}