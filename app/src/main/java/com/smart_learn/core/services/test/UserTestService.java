package com.smart_learn.core.services.test;

import android.text.TextUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.UserTestRepository;

import timber.log.Timber;

/**
 * Class and all methods will have package-private access.
 * */
class UserTestService extends BasicFirestoreService<TestDocument, UserTestRepository> {

    private static UserTestService instance;

    private UserTestService() {
        super(UserTestRepository.getInstance());
    }

    protected static UserTestService getInstance() {
        if(instance == null){
            instance = new UserTestService();
        }
        return instance;
    }

    protected Query getQueryForTests(long limit, int option) {
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

    protected CollectionReference getLocalTestsCollection(){
        return repositoryInstance.getLocalTestsCollection();
    }

    protected CollectionReference getOnlineTestsCollection(){
        return repositoryInstance.getOnlineTestsCollection();
    }

    protected void markAsHidden(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
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

    protected void setSchedule(DocumentSnapshot testSnapshot, boolean isScheduleActive, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(testSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback(
                    "Document " + testSnapshot.getId() + " updated schedule active to  [" + isScheduleActive + "]",
                    "Document " + testSnapshot.getId() + " can NOT update schedule active to [" + isScheduleActive + "]");
        }

        repositoryInstance.setSchedule(testSnapshot, isScheduleActive, callback);
    }

    protected void addLocalTest(TestDocument testDocument, DataCallbacks.General callback){
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

        repositoryInstance.addLocalTest(testDocument, callback);
    }

    protected void addLocalTest(TestDocument testDocument, DocumentReference newTestDocumentReference, DataCallbacks.General callback){
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

        repositoryInstance.addLocalTest(testDocument, newTestDocumentReference, callback);
    }

    protected void updateLocalTest(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(testSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Test document " + testSnapshot.getId() + " updated",
                    "Test document " + testSnapshot.getId() + " was NOT updated");
        }

        TestDocument test = testSnapshot.toObject(TestDocument.class);
        if(test == null){
            Timber.w("test is null");
            callback.onFailure();
            return;
        }

        if(TextUtils.isEmpty(test.getTestName())){
            Timber.w("name must not be null or empty");
            callback.onFailure();
            return;
        }

        if(test.getDocumentMetadata() == null){
            Timber.w("getDocumentMetadata() is null");
            callback.onFailure();
            return;
        }

        repositoryInstance.updateLocalTest(test, testSnapshot.getReference(), callback);
    }

    protected void deleteScheduledTest(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
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
}