package com.smart_learn.core.user.services;

import android.text.TextUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.core.user.services.helpers.BasicFirestoreService;
import com.smart_learn.data.user.firebase.firestore.entities.GroupChatMessageDocument;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.common.helpers.DataUtilities;
import com.smart_learn.data.user.repository.UserTestRepository;

import timber.log.Timber;

public class UserTestService extends BasicFirestoreService<TestDocument, UserTestRepository> {

    private static UserTestService instance;

    private UserTestService() {
        super(UserTestRepository.getInstance());
    }

    public static UserTestService getInstance() {
        if(instance == null){
            instance = new UserTestService();
        }
        return instance;
    }

    public Query getQueryForTests(long limit, int option) {
        switch (option){
            case TestService.SHOW_ONLY_LOCAL_SCHEDULED_TESTS:
                return repositoryInstance.getQueryForAllScheduledLocalTests(limit);
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS:
                return repositoryInstance.getQueryForAllVisibleInProgressLocalTests(limit);
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS:
                return repositoryInstance.getQueryForAllVisibleFinishedLocalTests(limit);
            case TestService.SHOW_ONLY_ONLINE_TESTS:
                return repositoryInstance.getQueryForAllVisibleOnlineTests(limit);
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS:
            default:
                return repositoryInstance.getQueryForAllVisibleNonScheduledLocalTests(limit);
        }
    }

    public Query getQueryForAllScheduledActiveLocalTests() {
        return repositoryInstance.getQueryForAllScheduledActiveLocalTests();
    }

    public Query getQueryForOnlineTestChatMessages(String testDocumentId, long limit) {
        return repositoryInstance.getQueryForOnlineTestChatMessages(testDocumentId, limit);
    }

    public Query getQueryForOnlineTestParticipantsRanking(String testDocumentId, long limit) {
        return repositoryInstance.getQueryForOnlineTestParticipantsRanking(testDocumentId, limit);
    }

    public CollectionReference getLocalTestsCollection(){
        return repositoryInstance.getLocalTestsCollection();
    }

    public CollectionReference getOnlineTestsCollection(){
        return repositoryInstance.getOnlineTestsCollection();
    }

    public CollectionReference getOnlineTestParticipantsCollectionReference(String testDocumentId){
        return repositoryInstance.getOnlineTestParticipantsCollectionReference(testDocumentId);
    }

    public void markAsHidden(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(testSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + testSnapshot.getId() + " marked as hidden",
                    "Document " + testSnapshot.getId() + " was NOT marked as hidden");
        }

        repositoryInstance.markAsHidden(testSnapshot, callback);
    }

    public void addTest(TestDocument testDocument, DocumentReference newTestDocumentReference, DataCallbacks.General callback){
        if (newTestDocumentReference == null){
            Timber.w("newTestDocumentReference is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if (testDocument == null){
            Timber.w("testDocument is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Test " + testDocument.getTestName() + " was added",
                    "Test = " + testDocument.getTestName() + " was NOT added");
        }

        if(TextUtils.isEmpty(testDocument.getTestName())){
            Timber.w("name must not be null or empty");
            callback.onFailure();
            return;
        }

        if(testDocument.getDocumentMetadata() == null){
            Timber.w("getDocumentMetadata() is null");
            callback.onFailure();
            return;
        }

        if(testDocument.isOnline()){
            if(testDocument.getSelectedFriends() == null || testDocument.getSelectedFriends().isEmpty()){
                Timber.w("getSelectedFriends() can not be null or empty");
                callback.onFailure();
                return;
            }
            repositoryInstance.addOnlineTest(testDocument, testDocument.getSelectedFriends(), newTestDocumentReference, callback);
        }
        else {
            repositoryInstance.addLocalTest(testDocument, newTestDocumentReference, callback);
        }
    }

    public void updateTest(TestDocument updatedTest, DocumentSnapshot updatedTestSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(updatedTestSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Test document " + updatedTestSnapshot.getId() + " updated",
                    "Test document " + updatedTestSnapshot.getId() + " was NOT updated");
        }

        if(updatedTest == null){
            Timber.w("test is null");
            callback.onFailure();
            return;
        }

        if(TextUtils.isEmpty(updatedTest.getTestName())){
            Timber.w("name must not be null or empty");
            callback.onFailure();
            return;
        }

        if(updatedTest.getDocumentMetadata() == null){
            Timber.w("getDocumentMetadata() is null");
            callback.onFailure();
            return;
        }

        repositoryInstance.updateTest(updatedTest, updatedTestSnapshot.getReference(), callback);
    }

    public void deleteScheduledTest(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(testSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + testSnapshot.getId() + " deleted",
                    "Document " + testSnapshot.getId() + " was NOT deleted");
        }

        repositoryInstance.deleteScheduledTest(testSnapshot.getReference(), callback);
    }

    public void sendOnlineTestMessage(String containerTestDocumentId, GroupChatMessageDocument messageDocument){
        if(messageDocument == null){
            Timber.w("messageDocument is null");
            return;
        }

        if(containerTestDocumentId == null || containerTestDocumentId.isEmpty()){
            Timber.w("containerTestDocumentId is null or empty");
            return;
        }

        if(messageDocument.getDocumentMetadata() == null){
            Timber.w("documentMetadata is null");
            return;
        }

        if(messageDocument.getFromUserDisplayName() == null){
            Timber.w("documentMetadata is null");
            return;
        }

        repositoryInstance.sendOnlineTestMessage(containerTestDocumentId, messageDocument);
    }
}