package com.smart_learn.data.repository.lesson;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.smart_learn.core.services.NotificationService;
import com.smart_learn.core.services.helpers.ThreadExecutorService;
import com.smart_learn.core.services.expression.UserExpressionService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.word.UserWordService;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.firebase.firestore.entities.NotificationDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicNotebookCommonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

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
        return getLessonsCollectionReference(false)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForLocalLessons(long limit) {
        return getLessonsCollectionReference(false)
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.LOCAL)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForReceivedLessons(long limit) {
        return getLessonsCollectionReference(false)
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.RECEIVED)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForSharedLessons(long limit) {
        return getLessonsCollectionReference(true)
                .whereArrayContains(LessonDocument.Fields.PARTICIPANTS_FIELD_NAME, UserService.getInstance().getUserUid())
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForSharedLessonParticipants(ArrayList<String> sharedLessonParticipants, long limit) {
        return FirebaseFirestore.getInstance().collection("/" + COLLECTION_USERS)
                .whereIn(DocumentMetadata.Fields.COMPOSED_OWNER_FIELD_NAME, sharedLessonParticipants)
                .orderBy(BasicProfileDocument.Fields.DISPLAY_NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForLocalLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference(false)
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.LOCAL)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForReceivedLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference(false)
                .whereEqualTo(LessonDocument.Fields.TYPE_FIELD_NAME, LessonDocument.Types.RECEIVED)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForSharedLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference(true)
                .whereArrayContains(LessonDocument.Fields.PARTICIPANTS_FIELD_NAME, UserService.getInstance().getUserUid())
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public Query getQueryForFilterForAllLessons(long limit, @NonNull @NotNull String value) {
        return getLessonsCollectionReference(false)
                .whereArrayContains(DocumentMetadata.Fields.COMPOSED_SEARCH_LIST_FIELD_NAME, value)
                .orderBy(LessonDocument.Fields.NAME_FIELD_NAME, Query.Direction.ASCENDING)
                .limit(limit);
    }

    public CollectionReference getLessonsCollectionReference(boolean isSharedLesson){
        if(isSharedLesson){
            return FirebaseFirestore.getInstance().collection("/" + COLLECTION_SHARED_LESSONS);
        }
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
        DocumentReference newLessonDocRef = getLessonsCollectionReference(false).document();
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

        // Steps:
        // 1. Extract lesson words and convert them intro a JSON string in order to be attached to
        //    the notification.
        // 2. Extract lesson expressions and convert them intro a JSON string in order to be attached
        //    to the notification.
        // 3. Send notification.

        // 1. Get all lesson words
        continueWithWordsExtraction(lessonDocumentId, lesson, friendUidList, callback);
    }

    private void continueWithWordsExtraction(@NonNull @NotNull String lessonDocumentId,
                                             @NonNull @NotNull LessonDocument lesson,
                                             @NonNull @NotNull ArrayList<String> friendUidList,
                                             @NonNull @NotNull DataCallbacks.General callback){
        UserWordService.getInstance()
                .getQueryForAllLessonWords(lessonDocumentId, lesson.getType() == LessonDocument.Types.SHARED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w(task.getException());
                            callback.onFailure();
                            return;
                        }

                        // get words
                        ArrayList<WordDocument> wordList = new ArrayList<>();
                        for(DocumentSnapshot snapshot : task.getResult().getDocuments()){
                            if(snapshot == null){
                                continue;
                            }
                            WordDocument word = snapshot.toObject(WordDocument.class);
                            if(word != null){
                                wordList.add(word);
                            }
                        }

                        // will be stored as JSON in order to be attached to notification
                        String wordListJson = WordDocument.fromListToJson(wordList);
                        // 2. Get all lesson expressions
                        continueWithExpressionsExtraction(lessonDocumentId, lesson, friendUidList, wordListJson, callback);
                    }
                });
    }

    private void continueWithExpressionsExtraction(@NonNull @NotNull String lessonDocumentId,
                                                   @NonNull @NotNull LessonDocument lesson,
                                                   @NonNull @NotNull ArrayList<String> friendUidList,
                                                   @NonNull @NotNull String wordList,
                                                   @NonNull @NotNull DataCallbacks.General callback){
        UserExpressionService.getInstance()
                .getQueryForAllLessonExpressions(lessonDocumentId, lesson.getType() == LessonDocument.Types.SHARED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w(task.getException());
                            callback.onFailure();
                            return;
                        }

                        // get expressions
                        ArrayList<ExpressionDocument> expressionsList = new ArrayList<>();
                        for(DocumentSnapshot snapshot : task.getResult().getDocuments()){
                            if(snapshot == null){
                                continue;
                            }
                            ExpressionDocument expression = snapshot.toObject(ExpressionDocument.class);
                            if(expression != null){
                                expressionsList.add(expression);
                            }
                        }

                        // will be stored as JSON in order to be attached to notification
                        String expressionListJson = ExpressionDocument.fromListToJson(expressionsList);
                        // 3. Send notification.
                        continueWithLessonShare(lesson.getName(), LessonDocument.convertDocumentToJson(lesson), wordList, expressionListJson, friendUidList, callback);
                    }
                });
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

    public void addEmptySharedLesson(@NonNull @NotNull LessonDocument lesson,
                                     @NonNull @NotNull ArrayList<DocumentSnapshot> selectedFriends,
                                     @NonNull @NotNull DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToAddEmptySharedLesson(lesson, selectedFriends, callback));
    }

    private void tryToAddEmptySharedLesson(@NonNull @NotNull LessonDocument lesson,
                                           @NonNull @NotNull ArrayList<DocumentSnapshot> selectedFriends,
                                           @NonNull @NotNull DataCallbacks.General callback){

        ArrayList<String> participants = new ArrayList<>();

        // for this operation will be necessary a transaction
        WriteBatch batch = FirebaseFirestore.getInstance().batch();

        // 1. For every friend from selected friends send a notification with TYPE_SHARED_LESSON_RECEIVED.
        for(DocumentSnapshot friendSnapshot : selectedFriends){
            if(friendSnapshot == null){
                continue;
            }
            FriendDocument friend = friendSnapshot.toObject(FriendDocument.class);
            if(friend == null){
                continue;
            }
            participants.add(friend.getFriendUid());

            // Send notification.
            NotificationDocument friendNotification = new NotificationDocument(
                    new DocumentMetadata(friend.getFriendUid(), System.currentTimeMillis(), new ArrayList<>()),
                    UserService.getInstance().getUserUid(),
                    UserService.getInstance().getUserDisplayName(),
                    UserService.getInstance().getUserDocumentReference(),
                    NotificationDocument.Types.TYPE_SHARED_LESSON_RECEIVED
            );
            // use lesson name as extra info in order to show it in notification
            friendNotification.setExtraInfo(lesson.getName());

            DocumentReference friendNotificationDocRef = NotificationService.getInstance().getSpecificNotificationsCollection(friend.getFriendUid()).document();
            batch.set(friendNotificationDocRef, NotificationDocument.convertDocumentToHashMap(friendNotification));
        }

        if(participants.isEmpty()){
            Timber.w("participants is empty");
            callback.onFailure();
            return;
        }

        // 2. Current user is also a participant so add him also and send notification for current user
        // with TYPE_SHARED_LESSON_SENT.
        participants.add(UserService.getInstance().getUserUid());

        // Send notification.
        NotificationDocument userNotification = new NotificationDocument(
                new DocumentMetadata(UserService.getInstance().getUserUid(), System.currentTimeMillis(), new ArrayList<>()),
                UserService.getInstance().getUserUid(),
                UserService.getInstance().getUserDisplayName(),
                UserService.getInstance().getUserDocumentReference(),
                NotificationDocument.Types.TYPE_SHARED_LESSON_SENT
        );
        // use lesson name as extra info in order to show it in notification
        userNotification.setExtraInfo(lesson.getName());

        DocumentReference userNotificationDocRef = NotificationService.getInstance().getNotificationsCollection().document();
        batch.set(userNotificationDocRef, NotificationDocument.convertDocumentToHashMap(userNotification));


        // 3. Add lesson in shared lessons collection
        lesson.setNrOfWords(0);
        lesson.setNrOfExpressions(0);
        lesson.setParticipants(participants);
        lesson.setFromDisplayName(UserService.getInstance().getUserDisplayName());
        DocumentReference lessonRef = getLessonsCollectionReference(true).document();
        batch.set(lessonRef, LessonDocument.convertDocumentToHashMap(lesson));

        // 4. Transaction is complete so commit
        commitBatch(batch, callback);
    }

}