package com.smart_learn.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.core.services.NotificationService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicNotebookCommonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class UserLessonRepository extends BasicFirestoreRepository<LessonDocument> {

    private static UserLessonRepository instance;

    private UserLessonRepository() {

    }

    public static UserLessonRepository getInstance() {
        if(instance == null){
            instance = new UserLessonRepository();
        }
        return instance;
    }

    public Query getQueryForAllLessons(long limit) {
        return getLessonsCollectionReference()
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForLocalLessons(long limit) {
        return getLessonsCollectionReference()
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.LOCAL)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForReceivedLessons(long limit) {
        return getLessonsCollectionReference()
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.RECEIVED)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForSharedLessons(long limit) {
        return getLessonsCollectionReference()
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.SHARED)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForLocalLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference()
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.LOCAL)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForReceivedLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference()
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.RECEIVED)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForSharedLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference()
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.SHARED)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForAllLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference()
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public CollectionReference getLessonsCollectionReference(){
        return FirebaseFirestore.getInstance()
                .collection("/" + COLLECTION_USERS + "/" + UserService.getInstance().getUserUid() + "/" + COLLECTION_LESSONS);
    }

    public void addEmptyLesson(@NonNull @NotNull LessonDocument lesson, @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddEmptyLesson(lesson, callback));
    }

    private void tryToAddEmptyLesson(@NonNull @NotNull LessonDocument lesson, @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Add lesson in user lessons
        lesson.getDocumentMetadata().setCounted(true);
        lesson.setNrOfWords(0);
        lesson.setNrOfExpressions(0);
        DocumentReference newLessonDocRef = getLessonsCollectionReference().document();
        batch.set(newLessonDocRef, LessonDocument.convertDocumentToHashMap(lesson));

        // 2. Update contour on user document and user modified time
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_LESSONS_FIELD_NAME, FieldValue.increment(1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void shareLesson(@NonNull @NotNull String lessonDocumentId,
                            @NonNull @NotNull LessonDocument lesson,
                            @NonNull @NotNull ArrayList<String> friendUidList,
                            @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToShareLesson(lessonDocumentId, lesson, friendUidList, callback));
    }

    private void tryToShareLesson(@NonNull @NotNull String lessonDocumentId,
                                  @NonNull @NotNull LessonDocument lesson,
                                  @NonNull @NotNull ArrayList<String> friendUidList,
                                  @NonNull @NotNull DataCallbacks.General callback){

        String wordList = ""; // use to transform object to json with GSON
        String expressionList = "";  // use to transform object to json with GSON

        // TODO
        // 1. Get all lesson words

        // TODO
        // 2. Get all lesson expression


        continueWithLessonShare(lesson.getName(), LessonDocument.convertDocumentToJson(lesson), wordList, expressionList, friendUidList, callback);
    }

    private void continueWithLessonShare(@NonNull @NotNull String lessonName,
                                         @NonNull @NotNull String lesson,
                                         @NonNull @NotNull String wordList,
                                         @NonNull @NotNull String expressionList,
                                         @NonNull @NotNull ArrayList<String> friendUidList,
                                         @NonNull @NotNull DataCallbacks.General callback){

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Send notification with type TYPE_NORMAL_LESSON_SENT to the current user
        NotificationDocument currentUserNotification = new NotificationDocument(
                new DocumentMetadata(UserService.getInstance().getUserUid(), System.currentTimeMillis(), new ArrayList<>()),
                UserService.getInstance().getUserUid(),
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserDocumentReference(),
                NotificationDocument.Types.TYPE_NORMAL_LESSON_SENT
        );
        // use lesson name as extra info in order to show it in notification
        currentUserNotification.setExtraInfo(lessonName);
        DocumentReference currentUserNotificationDocRef = NotificationService.getInstance().getNotificationsCollection().document();
        batch.set(currentUserNotificationDocRef, NotificationDocument.convertDocumentToHashMap(currentUserNotification));

        // 2. Send notification with type TYPE_NORMAL_LESSON_RECEIVED to all friends from list
        for(String friendUid : friendUidList){
            NotificationDocument friendNotification = new NotificationDocument(
                    new DocumentMetadata(friendUid, System.currentTimeMillis(), new ArrayList<>()),
                    UserService.getInstance().getUserUid(),
                    UserService.getInstance().getUserDisplayName(),
                    UserService.getInstance().getUserDocumentReference(),
                    NotificationDocument.Types.TYPE_NORMAL_LESSON_RECEIVED
            );
            // use lesson name as extra info in order to show it in notification
            friendNotification.setExtraInfo(lessonName);
            // set received lesson
            friendNotification.setReceivedLesson(lesson);
            // set lesson words
            friendNotification.setReceivedLessonWordList(wordList);
            // set lesson expressions
            friendNotification.setReceivedLessonExpressionList(expressionList);

            DocumentReference friendNotificationDocRef = NotificationService.getInstance().getSpecificNotificationsCollection(friendUid).document();
            batch.set(friendNotificationDocRef, NotificationDocument.convertDocumentToHashMap(friendNotification));
        }

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

    public void updateLessonName(@NonNull @NotNull String newName, @NonNull @NotNull DocumentSnapshot lessonSnapshot,
                                 @NonNull @NotNull DataCallbacks.General callback){
        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add(newName);
        HashMap<String, Object> data = new HashMap<>();
        data.put(DocumentMetadata.Fields.SEARCH_LIST_FIELD_NAME, CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues));
        data.put(LessonDocument.Fields.NAME_FIELD_NAME, newName);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, lessonSnapshot, callback);
    }

    public void updateLessonNotes(@NonNull @NotNull String newNotes, @NonNull @NotNull DocumentSnapshot lessonSnapshot,
                                  @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
        data.put(BasicNotebookCommonDocument.Fields.NOTES_FIELD_NAME, newNotes);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        updateDocument(data, lessonSnapshot, callback);
    }

    public void deleteLesson(@NonNull @NotNull DocumentReference lessonReference, @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToDeleteLesson(lessonReference, callback));
    }

    private void tryToDeleteLesson(@NonNull @NotNull DocumentReference lessonReference, @NonNull @NotNull DataCallbacks.General callback){
        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. Delete lesson from user lessons collection
        batch.delete(lessonReference);

        // 2. Update counter on user document and user modified time
        HashMap<String, Object> userData = new HashMap<>();
        userData.put(UserDocument.Fields.NR_OF_LESSONS_FIELD_NAME, FieldValue.increment(-1));
        userData.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        batch.update(UserService.getInstance().getUserDocumentReference(), userData);

        // 3. Transaction is complete so commit
        commitBatch(batch, callback);
    }

}