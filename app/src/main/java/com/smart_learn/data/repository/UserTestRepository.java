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
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class UserTestRepository extends BasicFirestoreRepository<TestDocument> {

    private static UserTestRepository instance;

    private UserTestRepository() {
    }

    public static UserTestRepository getInstance() {
        if(instance == null){
            instance = new UserTestRepository();
        }
        return instance;
    }


    public CollectionReference getLocalTestsCollection(){
        return getSpecificLocalTestsCollection(UserService.getInstance().getUserUid());
    }

    public CollectionReference getOnlineTestsCollection(){
        return FirebaseFirestore.getInstance().collection("/" + COLLECTION_ONLINE_TESTS);
    }

    public CollectionReference getSpecificLocalTestsCollection(@NonNull @NotNull String userUid){
        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_USERS + "/" + userUid + "/" + COLLECTION_LOCAL_TESTS);
    }

    public Query getQueryForAllVisibleNonScheduledLocalTests(long limit) {
        return getLocalTestsCollection()
                .whereEqualTo(Test.Fields.IS_HIDDEN_FIELD_NAME, false)
                .whereEqualTo(Test.Fields.IS_SCHEDULED_FIELD_NAME, false)
                .orderBy(Test.Fields.TEST_NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForAllVisibleInProgressLocalTests(long limit) {
        return getLocalTestsCollection()
                .whereEqualTo(Test.Fields.IS_HIDDEN_FIELD_NAME, false)
                .whereEqualTo(Test.Fields.IS_SCHEDULED_FIELD_NAME, false)
                .whereEqualTo(Test.Fields.IS_FINISHED_FIELD_NAME, false)
                .orderBy(Test.Fields.TEST_NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForAllVisibleFinishedLocalTests(long limit) {
        return getLocalTestsCollection()
                .whereEqualTo(Test.Fields.IS_HIDDEN_FIELD_NAME, false)
                .whereEqualTo(Test.Fields.IS_SCHEDULED_FIELD_NAME, false)
                .whereEqualTo(Test.Fields.IS_FINISHED_FIELD_NAME, true)
                .orderBy(Test.Fields.TEST_NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForAllScheduledLocalTests(long limit) {
        return getLocalTestsCollection()
                .whereEqualTo(Test.Fields.IS_SCHEDULED_FIELD_NAME, true)
                .orderBy(DocumentMetadata.Fields.COMPOSED_CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                .limit(limit);
    }

    public Query getQueryForAllVisibleOnlineTests(long limit) {
        return getOnlineTestsCollection()
                .whereEqualTo(Test.Fields.IS_HIDDEN_FIELD_NAME, false)
                .whereEqualTo(Test.Fields.IS_SCHEDULED_FIELD_NAME, false)
                .orderBy(DocumentMetadata.Fields.CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                .limit(limit);
    }

    public void markAsHidden(@NonNull @NotNull DocumentSnapshot testSnapshot, @NonNull @NotNull DataCallbacks.General callback){
        Map<String,Object> data = new HashMap<>();
        data.put(Test.Fields.IS_HIDDEN_FIELD_NAME, true);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, testSnapshot, callback);
    }

    public void setSchedule(@NonNull @NotNull DocumentSnapshot testSnapshot, boolean isScheduleActive, @NonNull @NotNull DataCallbacks.General callback){
        Map<String,Object> data = new HashMap<>();
        data.put(Test.Fields.IS_SCHEDULE_ACTIVE_FIELD_NAME, isScheduleActive);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, testSnapshot, callback);
    }

    public void addLocalTest(@NonNull @NotNull TestDocument test, @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddLocalAddTest(test, getLocalTestsCollection().document(), callback));
    }

    public void addLocalTest(@NonNull @NotNull TestDocument test,
                             @NonNull @NotNull DocumentReference newTestDocumentReference,
                             @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddLocalAddTest(test, newTestDocumentReference, callback));
    }

    private void tryToAddLocalAddTest(@NonNull @NotNull TestDocument test,
                                      @NonNull @NotNull DocumentReference newTestDocumentReference,
                                      @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add test in user local tests
        test.getDocumentMetadata().setCounted(true);
        if(test.isFinished() && !test.isCountedAsFinished()){
            test.setCountedAsFinished(true);
        }
        batch.set(newTestDocumentReference, TestDocument.convertDocumentToHashMap(test));

        // 2. Update counters on user document and user modified time
        HashMap<String, Object> userData = new HashMap<>();
        if(test.isScheduled()){
            userData.put(UserDocument.Fields.NR_OF_LOCAL_SCHEDULED_TESTS_FIELD_NAME, FieldValue.increment(1));
        }
        else{
            // here will be local unscheduled in progress or finished tests
            if(test.isFinished()){
                userData.put(UserDocument.Fields.NR_OF_LOCAL_UNSCHEDULED_FINISHED_TESTS_FIELD_NAME, FieldValue.increment(1));
                userData.put(UserDocument.Fields.TOTAL_SUCCESS_RATE_FIELD_NAME, FieldValue.increment(test.getSuccessRate()));
            }
            else {
                userData.put(UserDocument.Fields.NR_OF_LOCAL_UNSCHEDULED_IN_PROGRESS_TESTS_FIELD_NAME, FieldValue.increment(1));
            }
        }

        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void updateLocalTest(@NonNull @NotNull TestDocument test, @NonNull @NotNull DocumentReference testReference, @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToUpdateLocalTest(test, testReference, callback));
    }

    private void tryToUpdateLocalTest(@NonNull @NotNull TestDocument test, @NonNull @NotNull DocumentReference testReference, @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Update counters on user document and user modified time
        HashMap<String, Object> userData = new HashMap<>();
        if(!test.isScheduled() && test.isFinished() && !test.isCountedAsFinished()){
            test.setCountedAsFinished(true);
            userData.put(UserDocument.Fields.NR_OF_LOCAL_UNSCHEDULED_FINISHED_TESTS_FIELD_NAME, FieldValue.increment(1));
            userData.put(UserDocument.Fields.NR_OF_LOCAL_UNSCHEDULED_IN_PROGRESS_TESTS_FIELD_NAME, FieldValue.increment(-1));
            userData.put(UserDocument.Fields.TOTAL_SUCCESS_RATE_FIELD_NAME, FieldValue.increment(test.getSuccessRate()));
        }

        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 2. Update test
        batch.update(testReference, Test.convertDocumentToHashMap(test));

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void deleteScheduledTest(@NonNull @NotNull DocumentReference testReference, @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteScheduledTest(testReference, callback));
    }

    private void tryToDeleteScheduledTest(@NonNull @NotNull DocumentReference testReference, @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete test
        batch.delete(testReference);

        // 2. Update counters on user document and user modified time
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_LOCAL_SCHEDULED_TESTS_FIELD_NAME, FieldValue.increment(-1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }
}
