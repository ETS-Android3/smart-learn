package com.smart_learn.core.services;

import android.text.TextUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.UserLessonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import timber.log.Timber;

public class UserLessonService extends BasicFirestoreService<LessonDocument, UserLessonRepository> {

    public static final int SHOW_ALL_LESSONS = 1;
    public static final int SHOW_ONLY_LOCAL_LESSONS = 2;
    public static final int SHOW_ONLY_RECEIVED_LESSONS = 3;
    public static final int SHOW_ONLY_SHARED_LESSONS = 4;

    private static UserLessonService instance;

    private UserLessonService() {
        super(UserLessonRepository.getInstance());
    }

    public static UserLessonService getInstance() {
        if(instance == null){
            instance = new UserLessonService();
        }
        return instance;
    }

    public Query getQueryForLessons(long limit, int option) {
        switch (option){
            case SHOW_ONLY_LOCAL_LESSONS:
                return repositoryInstance.getQueryForLocalLessons(limit);
            case SHOW_ONLY_RECEIVED_LESSONS:
                return repositoryInstance.getQueryForReceivedLessons(limit);
            case SHOW_ONLY_SHARED_LESSONS:
                return repositoryInstance.getQueryForSharedLessons(limit);
            case SHOW_ALL_LESSONS:
            default:
                return repositoryInstance.getQueryForAllLessons(limit);
        }

    }

    public Query getQueryForFilter(long limit, String value, int option) {
        if(value == null){
            value = "";
        }
        switch (option){
            case SHOW_ONLY_LOCAL_LESSONS:
                return repositoryInstance.getQueryForFilterForLocalLessons(limit, value);
            case SHOW_ONLY_RECEIVED_LESSONS:
                return repositoryInstance.getQueryForFilterForReceivedLessons(limit, value);
            case SHOW_ONLY_SHARED_LESSONS:
                return repositoryInstance.getQueryForFilterForSharedLessons(limit, value);
            case SHOW_ALL_LESSONS:
            default:
                return repositoryInstance.getQueryForFilterForAllLessons(limit, value);
        }
    }

    public Query getQueryForSharedLessonParticipants(ArrayList<String> sharedLessonParticipants, long limit) {
        if(sharedLessonParticipants == null){
            sharedLessonParticipants = new ArrayList<>();
        }
        return repositoryInstance.getQueryForSharedLessonParticipants(sharedLessonParticipants, limit);
    }

    public CollectionReference getLessonsCollectionReference(boolean isSharedLesson){
        return repositoryInstance.getLessonsCollectionReference(isSharedLesson);
    }

    /**
     * Use to add lesson without words/expressions.
     * */
    public void addEmptyLesson(LessonDocument lessonDocument, DataCallbacks.General callback){
        if (lessonDocument == null){
            Timber.w("lessonDocument is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(TextUtils.isEmpty(lessonDocument.getName())){
            Timber.w("name must not be null or empty");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Lesson " + lessonDocument.getName() + " was added",
                    "Lesson = " + lessonDocument.getName() + " was NOT added");
        }

        repositoryInstance.addEmptyLesson(lessonDocument, callback);
    }

    public void addEmptySharedLesson(LessonDocument lessonDocument, ArrayList<DocumentSnapshot> friendsSnapshotList, DataCallbacks.General callback){
        if (lessonDocument == null){
            Timber.w("lessonDocument is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(TextUtils.isEmpty(lessonDocument.getName())){
            Timber.w("name must not be null or empty");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Lesson " + lessonDocument.getName() + " was added",
                    "Lesson = " + lessonDocument.getName() + " was NOT added");
        }

        if(friendsSnapshotList == null || friendsSnapshotList.isEmpty()){
            callback.onSuccess();
            Timber.w("No friends to send shared lesson");
            return;
        }

        repositoryInstance.addEmptySharedLesson(lessonDocument, friendsSnapshotList, callback);
    }

    public void updateLessonName(String newName, DocumentSnapshot lessonSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Lesson name for document " + lessonSnapshot.getId() + " updated",
                    "Lesson name for document " + lessonSnapshot.getId() + " was NOT updated");
        }

        if(newName == null || newName.isEmpty()){
            callback.onFailure();
            Timber.w("name can not be null or empty");
            return;
        }

        repositoryInstance.updateLessonName(newName, lessonSnapshot, callback);
    }

    public void updateLessonNotes(String newNotes, DocumentSnapshot lessonSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Lesson notes for document " + lessonSnapshot.getId() + " updated",
                    "Lesson notes for document " + lessonSnapshot.getId() + " was NOT updated");
        }

        if(newNotes == null){
           newNotes = "";
        }

        repositoryInstance.updateLessonNotes(newNotes, lessonSnapshot, callback);
    }

    public void updateLesson(DocumentSnapshot lessonSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + lessonSnapshot.getId() + " updated",
                    "Document " + lessonSnapshot.getId() + " was NOT updated");
        }

        LessonDocument lesson = lessonSnapshot.toObject(LessonDocument.class);
        if(lesson == null){
            callback.onFailure();
            Timber.w("lesson is null");
            return;
        }

        if(TextUtils.isEmpty(lesson.getName())){
            Timber.w("name must not be null or empty");
            callback.onFailure();
            return;
        }

        HashMap<String, Object> data = LessonDocument.convertDocumentToHashMap(lesson);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        repositoryInstance.updateDocument(data, lessonSnapshot, callback);
    }

    public void shareLesson(DocumentSnapshot lessonSnapshot, ArrayList<DocumentSnapshot> friendsSnapshotList, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + lessonSnapshot.getId() + " shared",
                    "Document " + lessonSnapshot.getId() + " was NOT shared");
        }

        if(friendsSnapshotList == null || friendsSnapshotList.isEmpty()){
            callback.onSuccess();
            return;
        }

        for(DocumentSnapshot snapshot : friendsSnapshotList){
            if(DataUtilities.Firestore.notGoodDocumentSnapshot(snapshot)){
                callback.onFailure();
                return;
            }
        }

        LessonDocument lesson = lessonSnapshot.toObject(LessonDocument.class);
        if(lesson == null){
            callback.onFailure();
            Timber.w("lesson is null");
            return;
        }

        ArrayList<String> friendUidList = new ArrayList<>();
        for(DocumentSnapshot snapshot : friendsSnapshotList){
            FriendDocument friendDocument = snapshot.toObject(FriendDocument.class);
            if(friendDocument == null){
                callback.onFailure();
                Timber.w("friendDocument for snapshot id = [" + snapshot.getId() + "] is null");
                return;
            }
            friendUidList.add(friendDocument.getFriendUid());
        }

        repositoryInstance.shareLesson(lessonSnapshot.getId(), lesson, friendUidList, callback);
    }

    public void deleteLesson(DocumentSnapshot lessonSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(lessonSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Document " + lessonSnapshot.getId() + " deleted",
                    "Document " + lessonSnapshot.getId() + " was NOT deleted");
        }


        repositoryInstance.deleteLesson(lessonSnapshot.getReference(), callback);
    }

}
