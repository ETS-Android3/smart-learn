package com.smart_learn.data.user.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.core.user.services.NotificationService;
import com.smart_learn.core.common.services.ThreadExecutorService;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.user.firebase.firestore.entities.FriendDocument;
import com.smart_learn.data.user.firebase.firestore.entities.GroupChatMessageDocument;
import com.smart_learn.data.user.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.user.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.user.repository.helpers.BasicFirestoreRepository;
import com.smart_learn.data.common.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


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

    public CollectionReference getOnlineTestMessagesCollectionReference(String testDocumentId){
        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_ONLINE_TESTS + "/" + testDocumentId + "/" +
                        COLLECTION_ONLINE_TEST_CHAT_MESSAGES);
    }

    public CollectionReference getOnlineTestParticipantsCollectionReference(String testDocumentId){
        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_ONLINE_TESTS + "/" + testDocumentId + "/" +
                        COLLECTION_ONLINE_TEST_PARTICIPANTS);
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

    public Query getQueryForAllScheduledActiveLocalTests() {
        return getLocalTestsCollection()
                .whereEqualTo(Test.Fields.IS_SCHEDULED_FIELD_NAME, true)
                .whereEqualTo(Test.Fields.IS_SCHEDULE_ACTIVE_FIELD_NAME, true);
    }

    public Query getQueryForAllVisibleOnlineTests(long limit) {
        return getOnlineTestsCollection()
                .whereArrayContains(TestDocument.Fields.PARTICIPANTS_FIELD_NAME, UserService.getInstance().getUserUid())
                .orderBy(DocumentMetadata.Fields.COMPOSED_CREATED_AT_FIELD_NAME, Query.Direction.DESCENDING)
                .limit(limit);
    }

    public Query getQueryForOnlineTestChatMessages(String testDocumentId, long limit) {
        return getOnlineTestMessagesCollectionReference(testDocumentId)
                .orderBy(DocumentMetadata.Fields.COMPOSED_CREATED_AT_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForOnlineTestParticipantsRanking(String testDocumentId, long limit) {
        return getOnlineTestParticipantsCollectionReference(testDocumentId)
                .limit(limit);
    }

    public void markAsHidden(@NonNull @NotNull DocumentSnapshot testSnapshot, @NonNull @NotNull DataCallbacks.General callback){
        Map<String,Object> data = new HashMap<>();
        data.put(Test.Fields.IS_HIDDEN_FIELD_NAME, true);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, testSnapshot, callback);
    }

    public void addLocalTest(@NonNull @NotNull TestDocument test,
                             @NonNull @NotNull DocumentReference newTestDocumentReference,
                             @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddLocalAddTest(test, newTestDocumentReference, callback));
    }

    private void tryToAddLocalAddTest(@NonNull @NotNull TestDocument test,
                                      @NonNull @NotNull DocumentReference newTestDocumentReference,
                                      @NonNull @NotNull DataCallbacks.General callback){
        // This is an adding process for new tests and by default tests can no be finished or counted as finished.
        if(test.isFinished() || test.isCountedAsFinished()){
            Timber.w("Test is finished or is counted as finished [" + test.toString() + "]");
            callback.onFailure();
            return;
        }

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add test in user local tests
        test.getDocumentMetadata().setCounted(true);
        batch.set(newTestDocumentReference, TestDocument.convertDocumentToHashMap(test));

        // 2. Update counters on user document and user modified time
        HashMap<String, Object> userData = new HashMap<>();
        if(test.isScheduled()){
            userData.put(UserDocument.Fields.NR_OF_LOCAL_SCHEDULED_TESTS_FIELD_NAME, FieldValue.increment(1));
        }
        else{
            userData.put(UserDocument.Fields.NR_OF_LOCAL_UNSCHEDULED_IN_PROGRESS_TESTS_FIELD_NAME, FieldValue.increment(1));
        }

        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void updateTest(@NonNull @NotNull TestDocument updatedTest, @NonNull @NotNull DocumentReference updatedTestReference,
                           @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToUpdateTest(updatedTest, updatedTestReference, callback));
    }

    private void tryToUpdateTest(@NonNull @NotNull TestDocument updatedTest, @NonNull @NotNull DocumentReference updatedTestReference, @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Update counters on user document and user modified time
        HashMap<String, Object> userData = new HashMap<>();
        if(updatedTest.isOnline()){
            if(updatedTest.isFinished() && !updatedTest.isCountedAsFinished()){
                updatedTest.setCountedAsFinished(true);
                userData.put(UserDocument.Fields.NR_OF_ONLINE_FINISHED_TESTS_FIELD_NAME, FieldValue.increment(1));
                userData.put(UserDocument.Fields.NR_OF_ONLINE_IN_PROGRESS_TESTS_FIELD_NAME, FieldValue.increment(-1));
                userData.put(UserDocument.Fields.TOTAL_SUCCESS_RATE_FIELD_NAME, FieldValue.increment(updatedTest.getSuccessRate()));
                userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
                batch.update(UserService.getInstance().getUserDocumentReference(), userData);
            }
        }
        else{
            if(!updatedTest.isScheduled() && updatedTest.isFinished() && !updatedTest.isCountedAsFinished()){
                updatedTest.setCountedAsFinished(true);
                userData.put(UserDocument.Fields.NR_OF_LOCAL_UNSCHEDULED_FINISHED_TESTS_FIELD_NAME, FieldValue.increment(1));
                userData.put(UserDocument.Fields.NR_OF_LOCAL_UNSCHEDULED_IN_PROGRESS_TESTS_FIELD_NAME, FieldValue.increment(-1));
                userData.put(UserDocument.Fields.TOTAL_SUCCESS_RATE_FIELD_NAME, FieldValue.increment(updatedTest.getSuccessRate()));
                userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
                batch.update(UserService.getInstance().getUserDocumentReference(), userData);
            }
        }

        // 2. Update test
        batch.update(updatedTestReference, TestDocument.convertDocumentToHashMap(updatedTest));

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void addOnlineTest(@NonNull @NotNull TestDocument test,
                              @NonNull @NotNull ArrayList<DocumentSnapshot> selectedFriends,
                              @NonNull @NotNull DocumentReference newTestDocumentReference,
                              @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddOnlineAddTest(test, selectedFriends, newTestDocumentReference, callback));
    }

    private void tryToAddOnlineAddTest(@NonNull @NotNull TestDocument test,
                                       @NonNull @NotNull ArrayList<DocumentSnapshot> selectedFriends,
                                       @NonNull @NotNull DocumentReference newTestDocumentReference,
                                       @NonNull @NotNull DataCallbacks.General callback){
        // This is an adding process for new tests and by default tests can no be finished or counted as finished.
        if(test.isFinished() || test.isCountedAsFinished()){
            Timber.w("Test is finished or is counted as finished [" + test.toString() + "]");
            callback.onFailure();
            return;
        }

        ArrayList<String> participants = new ArrayList<>();

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. For every friends from selected friends add a copy of test on current test participants
        // collection and send a notification with new test online.
        HashMap<String, Object> copyDocumentMetadata = DocumentMetadata.convertDocumentToHashMap(test.getDocumentMetadata());
        HashMap<String, Object> copyTest = TestDocument.convertDocumentToHashMap(test);
        // remove participants from copy because is not necessary
        copyTest.put(TestDocument.Fields.PARTICIPANTS_FIELD_NAME, new ArrayList<>());
        // add container test id
        copyTest.put(TestDocument.Fields.CONTAINER_TEST_ID_FIELD_NAME, newTestDocumentReference.getId());

        for(DocumentSnapshot friendSnapshot : selectedFriends){
            if(friendSnapshot == null){
                continue;
            }
            FriendDocument friend = friendSnapshot.toObject(FriendDocument.class);
            if(friend == null){
                continue;
            }
            participants.add(friend.getFriendUid());

            // 2.1. Add test copy.
            // set specific friend info
            copyDocumentMetadata.put(DocumentMetadata.Fields.OWNER_FIELD_NAME, friend.getFriendUid());
            copyTest.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, copyDocumentMetadata);
            copyTest.put(TestDocument.Fields.USER_DISPLAY_NAME_FIELD_NAME, friend.getDisplayName());
            copyTest.put(TestDocument.Fields.USER_EMAIL_FIELD_NAME, friend.getEmail());
            copyTest.put(TestDocument.Fields.USER_PROFILE_PHOTO_URL_FIELD_NAME, friend.getProfilePhotoUrl());

            // document will have ID as friend UID for a faster find
            DocumentReference copyRef = getOnlineTestParticipantsCollectionReference(newTestDocumentReference.getId()).document(friend.getFriendUid());
            batch.set(copyRef, copyTest);

            // 2.2. Send notification.
            NotificationDocument friendNotification = new NotificationDocument(
                    new DocumentMetadata(friend.getFriendUid(), System.currentTimeMillis(), new ArrayList<>()),
                    UserService.getInstance().getUserUid(),
                    UserService.getInstance().getUserDisplayName(),
                    UserService.getInstance().getUserDocumentReference(),
                    NotificationDocument.Types.TYPE_ONLINE_TEST_INVITATION_RECEIVED
            );
            // use test name as extra info in order to show it in notification
            friendNotification.setExtraInfo(test.getCustomTestName());

            DocumentReference friendNotificationDocRef = NotificationService.getInstance().getSpecificNotificationsCollection(friend.getFriendUid()).document();
            batch.set(friendNotificationDocRef, NotificationDocument.convertDocumentToHashMap(friendNotification));
        }

        if(participants.isEmpty()){
            Timber.w("participants is empty");
            callback.onFailure();
            return;
        }

        // 2. Current user is also a participant so add him also and add a test copy for himself.
        participants.add(UserService.getInstance().getUserUid());
        copyDocumentMetadata.put(DocumentMetadata.Fields.OWNER_FIELD_NAME, UserService.getInstance().getUserUid());
        copyTest.put(DocumentMetadata.Fields.DOCUMENT_METADATA_FIELD_NAME, copyDocumentMetadata);
        copyTest.put(TestDocument.Fields.USER_DISPLAY_NAME_FIELD_NAME, UserService.getInstance().getUserDisplayName());
        copyTest.put(TestDocument.Fields.USER_EMAIL_FIELD_NAME, UserService.getInstance().getUserEmail());
        copyTest.put(TestDocument.Fields.USER_PROFILE_PHOTO_URL_FIELD_NAME, UserService.getInstance().getUserPhotoUrl());
        // document will have ID as UID for a faster find
        DocumentReference copyRef = getOnlineTestParticipantsCollectionReference(newTestDocumentReference.getId()).document(UserService.getInstance().getUserUid());
        batch.set(copyRef, copyTest);


        // 3. Add test in test online collection
        HashMap<String, Object> testData = TestDocument.convertDocumentToHashMap(test);
        // remove questions because will not be necessary here (will be stored on every friend test,
        // for progress history)
        testData.put(Test.Fields.QUESTIONS_JSON_FIELD_NAME, "");
        // add specific data
        testData.put(TestDocument.Fields.PARTICIPANTS_FIELD_NAME, participants);
        // for this container test creator will be current user
        testData.put(TestDocument.Fields.USER_DISPLAY_NAME_FIELD_NAME, UserService.getInstance().getUserDisplayName());
        testData.put(TestDocument.Fields.USER_EMAIL_FIELD_NAME, UserService.getInstance().getUserEmail());
        testData.put(TestDocument.Fields.USER_PROFILE_PHOTO_URL_FIELD_NAME, UserService.getInstance().getUserPhotoUrl());
        // add container test id (is actually the ID of this document but store it for a fast access if necessary)
        testData.put(TestDocument.Fields.CONTAINER_TEST_ID_FIELD_NAME, newTestDocumentReference.getId());
        batch.set(newTestDocumentReference, testData);

        // 4. Update current user counters. For the other users counters will be updated when they
        // will receive notifications.
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_ONLINE_IN_PROGRESS_TESTS_FIELD_NAME, FieldValue.increment(1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 5. Transaction is complete so commit
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

    public void sendOnlineTestMessage(@NonNull @NotNull String containerTestDocumentId,
                                      @NonNull @NotNull GroupChatMessageDocument messageDocument){
        // FIXME: same problem as in commitBatch method from BasicFirestoreRepository
        getOnlineTestMessagesCollectionReference(containerTestDocumentId)
                .add(GroupChatMessageDocument.convertDocumentToHashMap(messageDocument));
    }
}
