package com.smart_learn.core.services.test;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.smart_learn.R;
import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserExpressionService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.UserWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.LessonEntrance;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionFullWrite;
import com.smart_learn.data.entities.QuestionIdentifier;
import com.smart_learn.data.entities.QuestionMetadata;
import com.smart_learn.data.entities.QuestionMixed;
import com.smart_learn.data.entities.QuestionQuiz;
import com.smart_learn.data.entities.QuestionTrueOrFalse;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.firebase.firestore.entities.GroupChatMessageDocument;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.data.room.repository.BasicRoomRepository;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class TestService {

    public static String NO_TEST_ID = "NO_TEST_ID";

    public static final int SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS = 1;
    public static final int SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS = 2;
    public static final int SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS = 3;
    public static final int SHOW_ONLY_LOCAL_SCHEDULED_TESTS = 4;
    public static final int SHOW_ONLY_ONLINE_TESTS = 5;

    private static TestService instance;

    private final GuestTestService guestTestServiceInstance;
    private final UserTestService userTestServiceInstance;

    private TestService() {
        guestTestServiceInstance = GuestTestService.getInstance();
        userTestServiceInstance = UserTestService.getInstance();
    }

    public static TestService getInstance() {
        if(instance == null){
            instance = new TestService();
        }
        return instance;
    }


    /* *********************************************************************************************
     *                                    Guest Test Service
     * ********************************************************************************************/

    public void update(RoomTest value, @Nullable DataCallbacks.General callback) {
        guestTestServiceInstance.update(value, callback);
    }

    public void delete(RoomTest value, @Nullable DataCallbacks.General callback) {
        guestTestServiceInstance.delete(value, callback);
    }

    public LiveData<List<RoomTest>> getAllLiveScheduledTests(){
        return guestTestServiceInstance.getAllLiveNotHiddenScheduledTests();
    }

    public List<RoomTest> getAllNotHiddenScheduledActiveTests(){
        return guestTestServiceInstance.getAllNotHiddenScheduledActiveTests();
    }

    public LiveData<List<RoomTest>> getAllLiveNonScheduledTests() {
        return guestTestServiceInstance.getAllLiveNotHiddenNonScheduledTests();
    }

    public LiveData<List<RoomTest>> getAllLiveInProgressTests(){
        return guestTestServiceInstance.getAllLiveNotHiddenInProgressTests();
    }

    public LiveData<List<RoomTest>> getAllLiveFinishedTests(){
        return guestTestServiceInstance.getAllLiveNotHiddenFinishedTests();
    }

    public LiveData<RoomTest> getLiveTest(int testId){
        return guestTestServiceInstance.getLiveTest(testId);
    }

    public RoomTest getTest(int testId){
        return guestTestServiceInstance.getTest(testId);
    }

    public LiveData<Integer> getLiveNumberOfNonScheduledTests(){
        return guestTestServiceInstance.getLiveNumberOfNotHiddenNonScheduledTests();
    }


    /* *********************************************************************************************
     *                                    User Test Service
     * ********************************************************************************************/

    public Query getQueryForTests(long limit, int option) {
       return userTestServiceInstance.getQueryForTests(limit, option);
    }

    public Query getQueryForOnlineTestChatMessages(String testDocumentId, long limit) {
        return userTestServiceInstance.getQueryForOnlineTestChatMessages(testDocumentId, limit);
    }

    public Query getQueryForOnlineTestParticipantsRanking(String testDocumentId, long limit) {
        return userTestServiceInstance.getQueryForOnlineTestParticipantsRanking(testDocumentId, limit);
    }

    public Query getQueryForAllScheduledActiveLocalTests() {
        return userTestServiceInstance.getQueryForAllScheduledActiveLocalTests();
    }

    public CollectionReference getLocalTestsCollection(){
        return userTestServiceInstance.getLocalTestsCollection();
    }

    public CollectionReference getOnlineTestParticipantsCollectionReference(String testDocumentId){
        return userTestServiceInstance.getOnlineTestParticipantsCollectionReference(testDocumentId);
    }

    public void markAsHidden(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.markAsHidden(testSnapshot, callback);
    }

    public void updateTest(TestDocument updatedTest, DocumentSnapshot updatedTestSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.updateTest(updatedTest, updatedTestSnapshot, callback);
    }

    public void deleteScheduledTest(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.deleteScheduledTest(testSnapshot, callback);
    }

    public void updateDocument(Map<String,Object> updatedInfo, DocumentSnapshot documentSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.updateDocument(updatedInfo, documentSnapshot, callback);
    }

    public void sendOnlineTestMessage(String containerTestDocumentId, GroupChatMessageDocument messageDocument){
        userTestServiceInstance.sendOnlineTestMessage(containerTestDocumentId, messageDocument);
    }


    /* *********************************************************************************************
     *                                   Test generation
     * ********************************************************************************************/

    private <T> boolean validParameters(ArrayList<T> valueList, Test testOptions){
        if(valueList == null || valueList.isEmpty()){
            Timber.w("valueList can not be null or empty");
            return false;
        }

        if(testOptions == null){
            Timber.w("testOptions is null");
            return false;
        }

        return true;
    }

    private boolean isValidSimpleScheduledTest(Test testOptions){
        if(testOptions == null){
            Timber.w("testOptions is null");
            return false;
        }

        if(!testOptions.isScheduled()){
            Timber.w("testOptions are not for scheduled test");
            return false;
        }

        if(testOptions.isUseCustomSelection()){
            Timber.w("This scheduled test can not have a custom selection. Values will be generated when test will be done (when time is up).");
            return false;
        }

        return true;
    }






    /**
     * Use to generate test using specific values.
     *
     * @param valueList values which will be used for generating the test.
     * @param testOptions Options for new test.
     * @param callback Callback to manage onComplete action.
     * */
    public void generateUserWordTest(ArrayList<DocumentSnapshot> valueList, Test testOptions, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(!validParameters(valueList, testOptions)){
            callback.onComplete(NO_TEST_ID);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertWordDocumentsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onComplete(NO_TEST_ID);
                return;
            }

            // this is a custom selection so mark it
            testOptions.setUseCustomSelection(true);
            testOptions.setNrOfValuesForGenerating(valueList.size());

            tryToGenerateUserTest(convertedList, testOptions, valueList.size(), callback);
        });
    }

    /**
     * Use to generate test using specific values.
     *
     * @param valueList values which will be used for generating the test.
     * @param testOptions Options for new test.
     * @param callback Callback to manage onComplete action.
     * */
    public void generateUserExpressionTest(ArrayList<DocumentSnapshot> valueList, Test testOptions, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(!validParameters(valueList, testOptions)){
            callback.onComplete(NO_TEST_ID);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertExpressionDocumentsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onComplete(NO_TEST_ID);
                return;
            }

            // this is a custom selection so mark it
            testOptions.setUseCustomSelection(true);
            testOptions.setNrOfValuesForGenerating(valueList.size());

            tryToGenerateUserTest(convertedList, testOptions, valueList.size(), callback);
        });
    }

    /**
     * Use to generate test using a specific number of values.
     *
     * @param testOptions Options for new test.
     * @param questionsNr How many questions should test have.
     * @param callback Callback to manage onComplete action.
     * */
    public void generateUserTest(Test testOptions, int questionsNr, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(testOptions == null){
            callback.onComplete(NO_TEST_ID);
            Timber.w("testOptions is null");
            return;
        }

        // in order to generate a new test, test should have a positive number of questions
        if(questionsNr <= 0 && questionsNr != Test.USE_ALL){
            callback.onComplete(NO_TEST_ID);
            Timber.w("questions number [" + questionsNr + "] is not valid");
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            switch (testOptions.getType()){
                case Test.Types.WORD_WRITE:
                case Test.Types.WORD_QUIZ:
                case Test.Types.WORD_MIXED_LETTERS:
                    // for a word type test, extract all words for current lesson
                    continueWithWordsExtraction(testOptions, questionsNr, callback);
                    return;
                case Test.Types.EXPRESSION_MIXED_WORDS:
                case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                    // for a expression type test, extract all expressions for current lesson
                    continueWithExpressionsExtraction(testOptions, questionsNr, callback);
                    return;
                default:
                    Timber.w("test type [" + testOptions.getType() + "] is not a valid test");
                    callback.onComplete(NO_TEST_ID);
            }
        });
    }

    private void continueWithWordsExtraction(Test testOptions, int questionsNr, TestService.TestGenerationCallback callback){
        UserWordService.getInstance()
                .getQueryForAllLessonWords(testOptions.getLessonId(), testOptions.isSharedLesson())
                // get only fresh data
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w(task.getException());
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        ArrayList<DocumentSnapshot> wordList = new ArrayList<>(task.getResult().getDocuments());
                        if(wordList.isEmpty()){
                            Timber.w("no words");
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        ArrayList<LessonEntrance> valueList = convertWordDocumentsToLessonEntrance(wordList);
                        if(wordList.size() != valueList.size()){
                            Timber.w("Error at conversion");
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        // this is NOT a custom selection so mark it
                        testOptions.setUseCustomSelection(false);

                        // This is NOT a custom selection and that means user chose a specific number of
                        // questions to be generated, or chose to use all lesson words/expressions.
                        // If USE_ALL is set then update questions number with values size.
                        int newQuestionsNr = questionsNr;
                        if(newQuestionsNr == Test.USE_ALL){
                            newQuestionsNr = valueList.size();
                        }

                        // values were extracted ==> continue with test generating
                        tryToGenerateUserTest(valueList, testOptions, newQuestionsNr, callback);
                    }
                });
    }

    private void continueWithExpressionsExtraction(Test testOptions, int questionsNr, TestService.TestGenerationCallback callback){
        UserExpressionService.getInstance()
                .getQueryForAllLessonExpressions(testOptions.getLessonId(), testOptions.isSharedLesson())
                // get only fresh data
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w(task.getException());
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        // get expressions
                        ArrayList<DocumentSnapshot> expressionsList = new ArrayList<>(task.getResult().getDocuments());
                        if(expressionsList.isEmpty()){
                            Timber.w("no expressions");
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        ArrayList<LessonEntrance> valueList = convertExpressionDocumentsToLessonEntrance(expressionsList);
                        if(expressionsList.size() != valueList.size()){
                            Timber.w("Error at conversion");
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        // this is NOT a custom selection so mark it
                        testOptions.setUseCustomSelection(false);

                        // This is NOT a custom selection and that means user chose a specific number of
                        // questions to be generated, or chose to use all lesson words/expressions.
                        // If USE_ALL is set then update questions number with values size.
                        int newQuestionsNr = questionsNr;
                        if(newQuestionsNr == Test.USE_ALL){
                            newQuestionsNr = valueList.size();
                        }

                        // values were extracted ==> continue with test generating
                        tryToGenerateUserTest(valueList, testOptions, newQuestionsNr, callback);
                    }
                });
    }


    /**
     * Use to save a scheduled test without generating values. Values will be generated when test
     * must be taken (when time is up).
     *
     * @param testOptions Options for new test.
     * @param callback Callback to manage onComplete action.
     * */
    public void saveSimpleUserScheduledTest(Test testOptions, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(!isValidSimpleScheduledTest(testOptions)){
            callback.onComplete(NO_TEST_ID);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> tryToGenerateUserTest(new ArrayList<>(), testOptions, 0, callback));
    }

    private void tryToGenerateUserTest(ArrayList<LessonEntrance> valueList, Test testOptions, int questionsNr, TestService.TestGenerationCallback callback){
        // for online test name generation is not needed because is set by user
        if((testOptions instanceof TestDocument) && ((TestDocument)testOptions).isOnline()){
            continueWithTestGeneration(true, valueList, testOptions, questionsNr, callback);
            return;
        }

        // otherwise generate a name
        UserService.getInstance()
                .getUserDocumentReference()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w(task.getException());
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        UserDocument userDocument = task.getResult().toObject(UserDocument.class);
                        if(userDocument == null){
                            Timber.w("userDocument is null");
                            callback.onComplete(NO_TEST_ID);
                            return;
                        }

                        // generate test name
                        int newTotalScheduledTest;
                        if(testOptions.isScheduled()){
                            newTotalScheduledTest = 1 + Math.toIntExact(userDocument.getNrOfLocalScheduledTests());
                        }
                        else{
                            newTotalScheduledTest = 1 + Math.toIntExact(userDocument.getNrOfLocalUnscheduledFinishedTests()) +
                                    Math.toIntExact(userDocument.getNrOfLocalUnscheduledInProgressTests());
                        }
                        testOptions.setTestName(ApplicationController.getInstance().getString(R.string.test_name) + " " + newTotalScheduledTest);

                        continueWithTestGeneration(true, valueList, testOptions, questionsNr, callback);
                    }
                });

    }







    /**
     * Use to generate test using specific values.
     *
     * @param valueList values which will be used for generating the test.
     * @param testOptions Options for new test.
     * @param callback Callback to manage onComplete action.
     * */
    public void generateGuestWordTest(ArrayList<Word> valueList, Test testOptions, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(!validParameters(valueList, testOptions)){
            callback.onComplete(NO_TEST_ID);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertWordsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onComplete(NO_TEST_ID);
                return;
            }

            // this is a custom selection so mark it
            testOptions.setUseCustomSelection(true);
            testOptions.setNrOfValuesForGenerating(valueList.size());

            tryToGenerateGuestTest(convertedList, testOptions, valueList.size(), callback);
        });
    }

    /**
     * Use to generate test using specific values.
     *
     * @param valueList values which will be used for generating the test.
     * @param testOptions Options for new test.
     * @param callback Callback to manage onComplete action.
     * */
    public void generateGuestExpressionTest(ArrayList<Expression> valueList, Test testOptions, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(!validParameters(valueList, testOptions)){
            callback.onComplete(NO_TEST_ID);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertExpressionsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onComplete(NO_TEST_ID);
                return;
            }

            // this is a custom selection so mark it
            testOptions.setUseCustomSelection(true);
            testOptions.setNrOfValuesForGenerating(valueList.size());

            tryToGenerateGuestTest(convertedList, testOptions, valueList.size(), callback);
        });
    }

    /**
     * Use to generate test using a specific number of values.
     *
     * @param testOptions Options for new test.
     * @param questionsNr How many questions should test have.
     * @param callback Callback to manage onComplete action.
     * */
    public void generateGuestTest(Test testOptions, int questionsNr, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(testOptions == null){
            callback.onComplete(NO_TEST_ID);
            Timber.w("testOptions is null");
            return;
        }

        // in order to generate a new test, test should have a positive number of questions
        if(questionsNr <= 0 && questionsNr != Test.USE_ALL){
            callback.onComplete(NO_TEST_ID);
            Timber.w("questions number [" + questionsNr + "] is not valid");
            return;
        }

        int lessonId;
        try{
            lessonId = Integer.parseInt(testOptions.getLessonId());
        } catch (NumberFormatException ex){
            callback.onComplete(NO_TEST_ID);
            Timber.w(ex);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {


            // For this type of test all values will be taken from db and later these will be used
            // to generate questions.
            ArrayList<LessonEntrance> valueList;

            switch (testOptions.getType()){
                case Test.Types.WORD_WRITE:
                case Test.Types.WORD_QUIZ:
                case Test.Types.WORD_MIXED_LETTERS:
                    // for a word type test, extract all words for current lesson
                    ArrayList<Word> wordList = (ArrayList<Word>) GuestWordService.getInstance()
                            .getLessonWords(lessonId);
                    if(wordList.isEmpty()){
                        Timber.w("no words");
                        callback.onComplete(NO_TEST_ID);
                        return;
                    }
                    valueList = convertWordsToLessonEntrance(wordList);
                    if(wordList.size() != valueList.size()){
                        Timber.w("Error at conversion");
                        callback.onComplete(NO_TEST_ID);
                        return;
                    }
                    break;
                case Test.Types.EXPRESSION_MIXED_WORDS:
                case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                    // for a expression type test, extract all expressions for current lesson
                    ArrayList<Expression> expressionList = (ArrayList<Expression>) GuestExpressionService.getInstance()
                            .getLessonExpressions(lessonId);
                    if(expressionList.isEmpty()){
                        Timber.w("no expressions");
                        callback.onComplete(NO_TEST_ID);
                        return;
                    }
                    valueList = convertExpressionsToLessonEntrance(expressionList);
                    if(expressionList.size() != valueList.size()){
                        Timber.w("Error at conversion");
                        callback.onComplete(NO_TEST_ID);
                        return;
                    }
                    break;
                default:
                    Timber.w("test type [" + testOptions.getType() + "] is not a valid test");
                    callback.onComplete(NO_TEST_ID);
                    return;
            }

            // this is NOT a custom selection so mark it
            testOptions.setUseCustomSelection(false);

            // This is NOT a custom selection and that means user chose a specific number of
            // questions to be generated, or chose to use all lesson words/expressions.
            // If USE_ALL is set then update questions number with values size.
            int newQuestionsNr = questionsNr;
            if(newQuestionsNr == Test.USE_ALL){
                newQuestionsNr = valueList.size();
            }

            // values were extracted ==> continue with test generating
            tryToGenerateGuestTest(valueList, testOptions, newQuestionsNr, callback);
        });
    }

    /**
     * Use to save a scheduled test without generating values. Values will be generated when test
     * must be taken (when time is up).
     *
     * @param testOptions Options for new test.
     * @param callback Callback to manage onComplete action.
     * */
    public void saveSimpleGuestScheduledTest(Test testOptions, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(!isValidSimpleScheduledTest(testOptions)){
            callback.onComplete(NO_TEST_ID);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> tryToGenerateGuestTest(new ArrayList<>(), testOptions, 0, callback));
    }

    private void tryToGenerateGuestTest(ArrayList<LessonEntrance> valueList, Test testOptions, int questionsNr, TestService.TestGenerationCallback callback){
        // generate test name
        int newTotalScheduledTest;
        if(testOptions.isScheduled()){
            newTotalScheduledTest = 1 + guestTestServiceInstance.getNumberOfScheduledTests();
        }
        else{
            newTotalScheduledTest = 1 + guestTestServiceInstance.getNumberOfNonScheduledTests();
        }
        testOptions.setTestName(ApplicationController.getInstance().getString(R.string.test_name) + " " + newTotalScheduledTest);

        continueWithTestGeneration(false, valueList, testOptions, questionsNr, callback);
    }







    /**
     * This method is used also for user and guest local test generation.
     *
     * @param isForUser Must be true if is a test for user, or false otherwise.
     * @param testOptions Options for new test.
     * @param questionsNr How many questions should test have.
     * @param callback Callback to manage onComplete action.
     * */
    private void continueWithTestGeneration(boolean isForUser, ArrayList<LessonEntrance> valueList, Test testOptions,
                                            int questionsNr, TestService.TestGenerationCallback callback){
        // For tests which are NOT scheduled questions must be generated always base on the value
        // list and questionsNr.
        //
        // For tests which are scheduled, questions must be generated only if is custom selection.
        // If not then generation will have place at specific test time.

        // by default for all new scheduled test, alarm is off
        if(testOptions.isScheduled()){
            testOptions.setScheduleActive(false);
            testOptions.setAlarmId(Test.NO_DATE_TIME);
        }

        // if test is scheduled and no custom selection is made, then test can be saved directly in db
        if(testOptions.isScheduled() && !testOptions.isUseCustomSelection()){
            // mark test as not-generated because will be generated at specific time
            testOptions.setGenerated(false);
            if(isForUser){
                saveUserTest(testOptions, callback);
            }
            else{
                saveGuestTest(testOptions, callback);
            }
            return;
        }

        // generate test questions
        String questionsJson;
        int generatedQuestionsNr;
        switch (testOptions.getType()){
            case Test.Types.WORD_WRITE:
                ArrayList<QuestionFullWrite> A = generateQuestionsForWordsFullWriteTest(valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = A.size();
                questionsJson = DataUtilities.General.fromListToJson(A);
                break;
            case Test.Types.WORD_QUIZ:
                ArrayList<QuestionQuiz> B = generateQuestionsForWordsQuizTest(valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = B.size();
                questionsJson = DataUtilities.General.fromListToJson(B);
                break;
            case Test.Types.WORD_MIXED_LETTERS:
                ArrayList<QuestionMixed> C = generateQuestionsForWordsMixedLettersTest(valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = C.size();
                questionsJson = DataUtilities.General.fromListToJson(C);
                break;
            case Test.Types.EXPRESSION_MIXED_WORDS:
                ArrayList<QuestionMixed> D = generateQuestionsForExpressionsMixedWordsTest(valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = D.size();
                questionsJson = DataUtilities.General.fromListToJson(D);
                break;
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                ArrayList<QuestionTrueOrFalse> E = generateQuestionsForExpressionsTrueOrFalseTest(valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = E.size();
                questionsJson = DataUtilities.General.fromListToJson(E);
                break;
            default:
                Timber.w("test type [" + testOptions.getType() + "] is not a valid test");
                callback.onComplete(NO_TEST_ID);
                return;
        }

        if(generatedQuestionsNr < 1){
            Timber.w(" generated questions nr [" + generatedQuestionsNr + "] is not valid");
            callback.onComplete(NO_TEST_ID);
            return;
        }

        // set questions
        testOptions.setQuestionsJson(questionsJson);

        // set initial values
        testOptions.setTotalQuestions(generatedQuestionsNr);
        testOptions.setAnsweredQuestions(0);
        testOptions.setCorrectAnswers(0);
        testOptions.setFinished(false);
        testOptions.setHidden(false);
        testOptions.setTestTotalTime(0);
        testOptions.setTestGenerationDate(System.currentTimeMillis());

        // here, if test is scheduled means that questions were generated, so mark test as generated
        if(testOptions.isScheduled()){
            testOptions.setGenerated(true);
        }

        // finally, save test in db
        if(isForUser){
            saveUserTest(testOptions, callback);
        }
        else{
            saveGuestTest(testOptions, callback);
        }
    }

    private void saveGuestTest(Test test, TestService.TestGenerationCallback callback){
        if(!(test instanceof RoomTest)){
            callback.onComplete(NO_TEST_ID);
            Timber.w("test not instanceof of RoomTest");
            return;
        }

        guestTestServiceInstance.insert((RoomTest) test, new DataCallbacks.RoomInsertionCallback() {
            @Override
            public void onSuccess(long id) {
                callback.onComplete(String.valueOf(Math.toIntExact(id)));
            }

            @Override
            public void onFailure() {
                callback.onComplete(NO_TEST_ID);
            }
        });
    }

    private void saveUserTest(Test test, TestService.TestGenerationCallback callback){
        if(!(test instanceof TestDocument)){
            callback.onComplete(NO_TEST_ID);
            Timber.w("test not instanceof of TestDocument");
            return;
        }

        TestDocument newTest = (TestDocument)test;
        DocumentReference newTestDocumentRef;
        if(newTest.isOnline()){
            newTestDocumentRef = userTestServiceInstance.getOnlineTestsCollection().document();
        }
        else{
            newTestDocumentRef = userTestServiceInstance.getLocalTestsCollection().document();
        }

        userTestServiceInstance.addTest(newTest, newTestDocumentRef, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                callback.onComplete(newTestDocumentRef.getId());
            }

            @Override
            public void onFailure() {
                callback.onComplete(NO_TEST_ID);
            }
        });
    }



    private ArrayList<QuestionFullWrite> generateQuestionsForWordsFullWriteTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return new ArrayList<>();
    }

    private ArrayList<QuestionMixed> generateQuestionsForWordsMixedLettersTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return generateQuestionsForMixedTest(valueList, questionsNr, isCustomSelection);
    }

    private ArrayList<QuestionQuiz> generateQuestionsForWordsQuizTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return new ArrayList<>();
    }

    private ArrayList<QuestionMixed> generateQuestionsForExpressionsMixedWordsTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return generateQuestionsForMixedTest(valueList, questionsNr, isCustomSelection);
    }

    private ArrayList<QuestionTrueOrFalse> generateQuestionsForExpressionsTrueOrFalseTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return new ArrayList<>();
    }

    private ArrayList<QuestionMixed> generateQuestionsForMixedTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return new ArrayList<>();
    }




    @NonNull @NotNull
    private ArrayList<LessonEntrance> convertWordsToLessonEntrance(ArrayList<Word> wordList){
        if(wordList == null || wordList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(Word word : wordList){
            convertedList.add(
                    new LessonEntrance(
                        String.valueOf(word.getWordId()),
                        word.getWord(),
                        word.getTranslations(),
                        word.getStatistics()
                    )
            );
        }
        return convertedList;
    }

    @NonNull @NotNull
    private ArrayList<LessonEntrance> convertExpressionsToLessonEntrance(ArrayList<Expression> expressionList){
        if(expressionList == null || expressionList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(Expression expression : expressionList){
            convertedList.add(
                    new LessonEntrance(
                            String.valueOf(expression.getExpressionId()),
                            expression.getExpression(),
                            expression.getTranslations(),
                            expression.getStatistics()
                    )
            );
        }
        return convertedList;
    }

    @NonNull @NotNull
    private ArrayList<LessonEntrance> convertWordDocumentsToLessonEntrance(ArrayList<DocumentSnapshot> wordList){
        if(wordList == null || wordList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(DocumentSnapshot snapshot : wordList){
            WordDocument word = snapshot.toObject(WordDocument.class);
            if(word == null){
                continue;
            }
            convertedList.add(
                    new LessonEntrance(
                            snapshot.getReference().getPath(), // for id use entire document path
                            word.getWord(),
                            Translation.fromJsonToList(word.getTranslations()),
                            word.getStatistics()
                    )
            );
        }
        return convertedList;
    }

    @NonNull @NotNull
    private ArrayList<LessonEntrance> convertExpressionDocumentsToLessonEntrance(ArrayList<DocumentSnapshot> expressionList){
        if(expressionList == null || expressionList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(DocumentSnapshot snapshot : expressionList){
            ExpressionDocument expression = snapshot.toObject(ExpressionDocument.class);
            if(expression == null){
                continue;
            }
            convertedList.add(
                    new LessonEntrance(
                            snapshot.getReference().getPath(), // for id use entire document path
                            expression.getExpression(),
                            Translation.fromJsonToList(expression.getTranslations()),
                            expression.getStatistics()
                    )
            );
        }
        return convertedList;
    }


    public void createTestFromScheduledTest(Test scheduledTest, boolean forUser, TestService.TestGenerationCallback callback){
        if(callback == null){
            Timber.w("callback is null");
            return;
        }

        if(scheduledTest == null){
            Timber.w("scheduledTest is null");
            callback.onComplete(NO_TEST_ID);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> tryToCreateTestFromScheduledTest(scheduledTest, forUser, callback));
    }

    private void tryToCreateTestFromScheduledTest(Test test, boolean isForUser, TestService.TestGenerationCallback callback){
        if(!test.isScheduled()){
            callback.onComplete(NO_TEST_ID);
            Timber.w("test is not scheduled");
            return;
        }

        // update data because new test will not be a scheduled test
        test.setScheduled(false);
        test.setScheduleActive(false);
        test.setAlarmId(Test.NO_DATE_TIME);
        test.setHour(Test.NO_DATE_TIME);
        test.setHour(Test.NO_DATE_TIME);
        test.setOneTime(false);
        test.setDate(Test.NO_DATE_TIME, Test.NO_DATE_TIME, Test.NO_DATE_TIME);
        test.setTestGenerationDate(System.currentTimeMillis());
        test.setDaysStatus(new ArrayList<>(Collections.nCopies(Test.NR_OF_WEEK_DAYS, false)));

        // Reset id for guest test, because test will be reinserted in Room db with a new id. If id
        // is not reset then insertion will fail because will to tests with same id.
        if(!isForUser && (test instanceof RoomTest)){
            ((RoomTest)test).setTestId(BasicRoomRepository.UNSET_ROW_ID);
        }

        if(!test.isGenerated()){
            if(isForUser){
                generateUserTest(test, test.getNrOfValuesForGenerating(), callback);
            }
            else{
                generateGuestTest(test, test.getNrOfValuesForGenerating(), callback);
            }
            return;
        }

        // TODO: add name generation in one function. In  tryToGenerateGuestTest and tryToGenerateUserTest
        //  also a name generation is used.

        // here test was already generated so add test as new local test
        test.setGenerated(false);
        if(isForUser){
            UserService.getInstance()
                    .getUserDocumentReference()
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(!task.isSuccessful() || task.getResult() == null){
                                Timber.w(task.getException());
                                callback.onComplete(NO_TEST_ID);
                                return;
                            }

                            UserDocument userDocument = task.getResult().toObject(UserDocument.class);
                            if(userDocument == null){
                                Timber.w("userDocument is null");
                                callback.onComplete(NO_TEST_ID);
                                return;
                            }

                            // generate test name
                            int newTotalTests = 1 + Math.toIntExact(userDocument.getNrOfLocalUnscheduledFinishedTests()) +
                                        Math.toIntExact(userDocument.getNrOfLocalUnscheduledInProgressTests());
                            test.setTestName(ApplicationController.getInstance().getString(R.string.test_name) + " " + newTotalTests);
                            saveUserTest(test, callback);
                        }
                    });
        }
        else {
            int newTotalTests = 1 + guestTestServiceInstance.getNumberOfNonScheduledTests();
            test.setTestName(ApplicationController.getInstance().getString(R.string.test_name) + " " + newTotalTests);
            saveGuestTest(test, callback);
        }
    }



    public void updateStatistics(Question question, DataCallbacks.General callback){
        if(question == null){
            Timber.w("question is null");
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback =  DataUtilities.General.generateGeneralCallback("Statistics for question " + question.getId() + " updated",
                    "Statistics for question " + question.getId() + " was NOT updated");
        }

        final DataCallbacks.General finalCallback = callback;
        final int userType = question.getQuestionMetadata().getUserType();
        switch (userType){
            case QuestionMetadata.Users.GUEST:
                ThreadExecutorService.getInstance().execute(() -> updateGuestStatistics(question, finalCallback));
                return;
            case QuestionMetadata.Users.USER_LOGGED_IN:
                ThreadExecutorService.getInstance().execute(() -> updateUserStatistics(question, finalCallback));
                return;
            default:
                Timber.w("userType [" + userType + "] is not valid");
                callback.onFailure();
                //return;
        }
    }

    private void updateUserStatistics(Question question, DataCallbacks.General callback){
        // Update every element but ignore if some element can not be updated. Do not use a transaction
        // for updating all items at once. Update one item at a time because is important to have
        // statistics at least for some items. If transaction will be used and a single item will
        // have a problem then update will fail for all.

        // IMPORTANT: If you decide to use callback.OnSuccess() or callback.onFailure() after update
        // use a transaction. You can not update une item at a time and call every time the callback
        // method because errors will appear. Callback must be called once, after update is made for
        // all.


        // IMPORTANT: If you use callback without taking into account the update, do not forget to
        // call callback.onSuccess() or callback.onFailure() before exiting the function. If you
        // forget errors can appear while caller will wait until callback is called.

        ArrayList<QuestionIdentifier> identifiersList = question.getQuestionMetadata().getQuestionIdentifiers(question.isReversed());
        for(QuestionIdentifier item : identifiersList){
            if(item == null){
                continue;
            }

            final String identifierId = item.getId();
            if(identifierId.isEmpty()){
                Timber.w("identifierId is not selected");
                continue;
            }

            final int identifierType = item.getType();
            switch (identifierType){
                case QuestionIdentifier.Identifiers.WORD:
                    updateUserWordStatistics(identifierId, item.getTranslationsIds(), question);
                    continue;
                case QuestionIdentifier.Identifiers.EXPRESSION:
                    updateUserExpressionStatistics(identifierId, item.getTranslationsIds(), question);
                    continue;
                default:
                    Timber.w("identifierType [" + identifierType + "] is not valid");
                    //continue;
            }
        }

        // Ignore if statistics update is made or not and return directly onSuccess().
        callback.onSuccess();
    }

    private void updateGuestStatistics(Question question, DataCallbacks.General callback){
        // Update every element but ignore if some element can not be updated. Do not use a transaction
        // for updating all items at once. Update one item at a time because is important to have
        // statistics at least for some items. If transaction will be used and a single item will
        // have a problem then update will fail for all.

        // IMPORTANT: if you decide to use callback.OnSuccess() or callback.onFailure() after update
        // use a transaction. You can not update une item at a time and call every time the callback
        // method because errors will appear. Callback must be called once, after update is made for
        // all.

        // IMPORTANT: If you use callback without taking into account the update, do not forget to
        // call callback.onSuccess() or callback.onFailure() before exiting the function. If you
        // forget errors can appear while caller will wait until callback is called.

        ArrayList<QuestionIdentifier> identifiersList = question.getQuestionMetadata().getQuestionIdentifiers(question.isReversed());
        for(QuestionIdentifier item : identifiersList) {
            final int identifierId = item.getIdInteger();
            if (identifierId == QuestionIdentifier.NO_IDENTIFIER_ID_INTEGER) {
                Timber.w("identifierId is not selected");
                continue;
            }

            final int identifierType = item.getType();
            switch (identifierType) {
                case QuestionIdentifier.Identifiers.WORD:
                    updateGuestWordStatistics(identifierId, item.getTranslationsIds(), question);
                    continue;
                case QuestionIdentifier.Identifiers.EXPRESSION:
                    updateGuestExpressionStatistics(identifierId, item.getTranslationsIds(), question);
                    continue;
                default:
                    Timber.w("identifierType [" + identifierType + "] is not valid");
                    //continue;
            }
        }

        // Ignore if statistics update is made or not and return directly onSuccess().
        callback.onSuccess();
    }

    private void updateUserWordStatistics(String documentPath, HashSet<Long> ids, Question question){
        // extract word from db
        FirebaseFirestore.getInstance()
                .document(documentPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w("result is not valid");
                            return;
                        }

                        WordDocument word = task.getResult().toObject(WordDocument.class);
                        if(word == null){
                            Timber.w("word is null");
                            return;
                        }

                        // update word statistic
                        word.getStatistics().updateScore(question.isAnswerCorrect(), question.getAnswerTimeInMilliseconds());
                        // update statistic for every translation from question
                        word.setTranslations(updateTranslationsStatistics(question, ids, word.getTranslations()));
                        // make final update in db
                        UserWordService.getInstance().updateDocument(WordDocument.convertDocumentToHashMap(word), documentPath, new DataCallbacks.General() {
                            @Override
                            public void onSuccess() {
                                // no action needed here
                            }

                            @Override
                            public void onFailure() {
                                Timber.w("word [" + word.toString() + "] was not updated.");
                            }
                        });

                    }
                });
    }

    private void updateUserExpressionStatistics(String documentPath, HashSet<Long> ids, Question question){
        // extract word from db
        FirebaseFirestore.getInstance()
                .document(documentPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w("result is not valid");
                            return;
                        }

                        ExpressionDocument expression = task.getResult().toObject(ExpressionDocument.class);
                        if(expression == null){
                            Timber.w("expression is null");
                            return;
                        }

                        // update word statistic
                        expression.getStatistics().updateScore(question.isAnswerCorrect(), question.getAnswerTimeInMilliseconds());
                        // update statistic for every translation from question
                        expression.setTranslations(updateTranslationsStatistics(question, ids, expression.getTranslations()));
                        // make final update in db
                        UserExpressionService.getInstance().updateDocument(ExpressionDocument.convertDocumentToHashMap(expression),
                                documentPath, new DataCallbacks.General() {
                                    @Override
                                    public void onSuccess() {
                                        // no action needed here
                                    }

                                    @Override
                                    public void onFailure() {
                                        Timber.w("expression [" + expression.toString() + "] was not updated.");
                                    }
                                });
                    }
                });
    }

    private void updateGuestWordStatistics(int identifierId, HashSet<Long> ids, Question question){
        // extract word from db
        Word word = GuestWordService.getInstance().getSampleWord(identifierId);
        if(word == null){
            Timber.w("word is null");
            return;
        }
        // update word statistic
        word.getStatistics().updateScore(question.isAnswerCorrect(), question.getAnswerTimeInMilliseconds());
        // update statistic for every translation from question
        word.setTranslations(updateTranslationsStatistics(question, ids, word.getTranslations()));
        // make final update in db
        GuestWordService.getInstance().update(word, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                // no action needed here
            }

            @Override
            public void onFailure() {
                Timber.w("word [" + word.toString() + "] was not updated.");
            }
        });
    }

    private void updateGuestExpressionStatistics(int identifierId, HashSet<Long> ids, Question question){
        // extract expression from db
        Expression expression = GuestExpressionService.getInstance().getSampleExpression(identifierId);
        if(expression == null){
            Timber.w("expression is null");
            return;
        }
        // update expression statistic
        expression.getStatistics().updateScore(question.isAnswerCorrect(), question.getAnswerTimeInMilliseconds());
        // update statistic for every translation from question
        expression.setTranslations(updateTranslationsStatistics(question, ids, expression.getTranslations()));
        // make final update in db
        GuestExpressionService.getInstance().update(expression, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                // no action needed here
            }

            @Override
            public void onFailure() {
                Timber.w("expression [" + expression.toString() + "] was not updated.");
            }
        });
    }

    private ArrayList<Translation> updateTranslationsStatistics(Question question, HashSet<Long> ids, ArrayList<Translation> translationsList){
        final boolean correctAnswer = question.isAnswerCorrect();
        final long answerTime = question.getAnswerTimeInMilliseconds();
        for(Translation item : translationsList){
            if(ids.contains(item.getId())){
                item.getStatistics().updateScore(correctAnswer, answerTime);
            }
        }

        return translationsList;
    }

    private String updateTranslationsStatistics(Question question, HashSet<Long> ids, String translationsList){
        return Translation.fromListToJson(updateTranslationsStatistics(question, ids, Translation.fromJsonToList(translationsList)));
    }


    /**
     * This will be used to manage onComplete() action when a new test is generated.
     * */
    public interface TestGenerationCallback {
        void onComplete(@NotNull @NonNull String testId);
    }


    /**
     * This will be used to manage alarms related to the scheduled tests.
     *
     * https://www.youtube.com/watch?v=xSrVWFCtgaE&ab_channel=Foxandroid
     * https://developer.android.com/reference/android/app/AlarmManager
     * */
    public static class ScheduledTestAlarmManager {

        private static final String BUNDLE_ARGS_KEY = "BUNDLE_ARGS_KEY";

        private static final String SCHEDULED_TEST_ID_KEY = "SCHEDULED_TEST_ID_KEY";
        private static final String FOR_USER_KEY = "FOR_USER_KEY";
        private static final String USER_UID_KEY = "USER_UID_KEY";
        private static final String ALARM_ID_KEY = "ALARM_ID_KEY";
        private static final String MESSAGE_KEY = "MESSAGE_KEY";

        private static final int NO_ALARM_ID = -1;

        private static ScheduledTestAlarmManager instance;
        private final AlarmManager alarmManager;

        private ScheduledTestAlarmManager() {
            alarmManager = (AlarmManager) ApplicationController.getInstance().getSystemService(Context.ALARM_SERVICE);
        }

        public static ScheduledTestAlarmManager getInstance() {
            if(instance == null){
                instance = new ScheduledTestAlarmManager();
            }
            return instance;
        }

        private int getUniqueAlarmId(){
            // Every alarm must have a unique id.
            // FIXME: if is an overflow from 'long' to 'int', two id's can be the same at some time,
            //  so find another way for getting a unique id.
            return (int) CoreUtilities.General.generateUniqueId();
        }

        public int setExactAlarm(String scheduledTestId, String message, boolean forUser, long time){
            final int id = getUniqueAlarmId();
            setExactAlarm(id, scheduledTestId, message, forUser, time);
            return id;
        }

        public void setExactAlarm(int alarmId, String scheduledTestId, String message, boolean forUser, long time){
            final Context context = ApplicationController.getInstance().getApplicationContext();
            // https://stackoverflow.com/questions/28262650/what-is-the-difference-between-rtc-and-rtc-wakeup-of-alarmmanager?rq=1
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, getPendingIntent(context, alarmId, scheduledTestId, message, forUser));
            Timber.i("Alarm [" + alarmId +  "] was set for time [" + CoreUtilities.General.longToDateTime(time) + "]");
        }

        public int setAlarmRepeatingInSpecificDays(@NotNull @NonNull Test test, String scheduledTestId, String message, boolean forUser){
            // this will be the base id from which the other id's will be constructed
            int baseId = getUniqueAlarmId();
            setAlarmRepeatingInSpecificDays(
                    baseId,
                    test,
                    scheduledTestId,
                    message,
                    forUser
            );
            return baseId;
        }

        public void setAlarmRepeatingInSpecificDays(int baseAlarmId, @NotNull @NonNull Test test, String scheduledTestId,
                                                    String message, boolean forUser){
            // https://stackoverflow.com/questions/8469705/how-to-set-multiple-alarms-using-alarm-manager-in-android
            // https://stackoverflow.com/questions/17894067/set-repeat-days-of-week-alarm-in-android
            // https://stackoverflow.com/questions/28262650/what-is-the-difference-between-rtc-and-rtc-wakeup-of-alarmmanager?rq=1

            Context context = ApplicationController.getInstance().getApplicationContext();

            // for every selected day must be set an alarm with an unique id with repeating interval of one week
            final long weekInterval = 7L * AlarmManager.INTERVAL_DAY;
            final int hour = test.getHour();
            final int minute = test.getMinute();

            if(test.isRepeatActiveMonday()){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.MONDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 1, scheduledTestId, message, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 1 +  "] was set for [MONDAY at " + hour + ":" + minute + "]");
            }

            if(test.isRepeatActiveTuesday()){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.TUESDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 2, scheduledTestId, message, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 2 +  "] was set for [TUESDAY at " + hour + ":" + minute + "]");
            }

            if(test.isRepeatActiveWednesday()){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.WEDNESDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 3, scheduledTestId, message, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 3 +  "] was set for [WEDNESDAY at " + hour + ":" + minute + "]");
            }

            if(test.isRepeatActiveThursday()){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.THURSDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 4, scheduledTestId, message, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 4 +  "] was set for [THURSDAY at " + hour + ":" + minute + "]");
            }

            if(test.isRepeatActiveFriday()){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.FRIDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 5, scheduledTestId, message, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 5 +  "] was set for [FRIDAY at " + hour + ":" + minute + "]");
            }

            if(test.isRepeatActiveSaturday()){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.SATURDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 6, scheduledTestId, message, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 6 +  "] was set for [SATURDAY at " + hour + ":" + minute + "]");
            }

            if(test.isRepeatActiveSunday()){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.SUNDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 7, scheduledTestId, message, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 7 +  "] was set for [SUNDAY at " + hour + ":" + minute + "]");
            }
        }


        public void cancelAlarm(String scheduledTestId, String message, boolean forUser, int id){
            if(id == NO_ALARM_ID){
                Timber.w("Alarm id [ " + id +  "] is not valid. Cannot cancel alarm.");
                return;
            }
            alarmManager.cancel(getPendingIntent(ApplicationController.getInstance().getApplicationContext(), id, scheduledTestId, message, forUser));
            Timber.i("Alarm [" + id +  "] was canceled.");
        }

        public void cancelAlarmRepeatingInSpecificDays(int baseAlarmId, @NotNull @NonNull Test test, String scheduledTestId, String message, boolean forUser){
            if(test.isRepeatActiveMonday()){
                cancelAlarm(scheduledTestId, message, forUser, baseAlarmId + 1);
            }

            if(test.isRepeatActiveTuesday()){
                cancelAlarm(scheduledTestId, message, forUser, baseAlarmId + 2);
            }

            if(test.isRepeatActiveWednesday()){
                cancelAlarm(scheduledTestId, message, forUser, baseAlarmId + 3);
            }

            if(test.isRepeatActiveThursday()){
                cancelAlarm(scheduledTestId, message, forUser, baseAlarmId + 4);
            }

            if(test.isRepeatActiveFriday()){
                cancelAlarm(scheduledTestId, message, forUser, baseAlarmId + 5);
            }

            if(test.isRepeatActiveSaturday()) {
                cancelAlarm(scheduledTestId, message, forUser, baseAlarmId + 6);
            }

            if(test.isRepeatActiveSunday()){
                cancelAlarm(scheduledTestId, message, forUser, baseAlarmId + 7);
            }

        }

        private PendingIntent getPendingIntent(Context context, int alarmId, String scheduledTestId, String message, boolean forUser){
            Intent intent = new Intent(context, ScheduledTestAlarmReceiver.class);
            Bundle args = new Bundle();
            args.putString(SCHEDULED_TEST_ID_KEY, scheduledTestId);
            args.putBoolean(FOR_USER_KEY, forUser);
            args.putInt(ALARM_ID_KEY, alarmId);
            args.putString(MESSAGE_KEY, message);
            if(forUser){
                // This will be used to avoid launching an alarm for user A if user B is logged in.
                args.putString(USER_UID_KEY, UserService.getInstance().getUserUid());
            }
            intent.putExtra(BUNDLE_ARGS_KEY, args);
            // https://stackoverflow.com/questions/67045607/how-to-resolve-missing-pendingintent-mutability-flag-lint-warning-in-android-a
            return PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }

        /**
         * This is the broadcast receiver which will handle actions when alarm is received.
         * */
        public static final class ScheduledTestAlarmReceiver extends BroadcastReceiver {

            private final static String CHANNEL_ID = "smart-learn-alarm-notification-id";
            private final static String CHANNEL_NAME = "smart-learn-alarm-notification-channel";

            private final static String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
            private final static String ACTION_QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON";

            @Override
            public void onReceive(Context context, Intent intent) {
                final boolean deviceRebooted = intent.getAction() != null && (intent.getAction().equals(ACTION_BOOT_COMPLETED) ||
                                                        intent.getAction().equals(ACTION_QUICKBOOT_POWERON));
                if (deviceRebooted){
                    // If device was reset then alarms were eliminated so add for every active scheduled
                    // test a new alarm.
                    // https://stackoverflow.com/questions/12034357/does-alarm-manager-persist-even-after-reboot
                    // https://stackoverflow.com/questions/52578988/alarmmanager-doesnt-work-on-next-day-after-reboot
                    // https://stackoverflow.com/questions/44211576/alarm-manager-doesnt-work-after-phone-restart/44212543
                    Timber.i("Device rebooted ==> resetting alarms.");
                    resetAlarms();
                    return;
                }

                processAlarm(context, intent);
            }

            private void resetAlarms(){
                if(CoreUtilities.Auth.isUserLoggedIn()){
                    ThreadExecutorService.getInstance().execute(this::resetUserAlarms);
                }
                else{
                    ThreadExecutorService.getInstance().execute(this::resetGuestAlarms);
                }
            }

            private void resetUserAlarms(){
                // get all scheduled active tests
                TestService.getInstance()
                        .getQueryForAllScheduledActiveLocalTests()
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(!task.isSuccessful() || task.getResult() == null){
                                    Timber.w("result is not valid");
                                    Timber.w(task.getException());
                                    return;
                                }

                               for(DocumentSnapshot snapshot : task.getResult().getDocuments()){
                                   if(snapshot == null){
                                       continue;
                                   }
                                   TestDocument test = snapshot.toObject(TestDocument.class);
                                   if(test == null){
                                       continue;
                                   }

                                   // reset alarm
                                   test.resetAlarm(snapshot.getId(), true);

                                   // and update test in db
                                   TestService.getInstance().updateTest(test, snapshot, null);
                               }

                                Timber.i("User alarms reset is finished.");
                            }
                        });
            }

            private void resetGuestAlarms(){
                // get all scheduled active tests
                List<RoomTest> activeScheduledTests = TestService.getInstance().getAllNotHiddenScheduledActiveTests();
                if(activeScheduledTests == null || activeScheduledTests.isEmpty()){
                    return;
                }

                for(RoomTest test : activeScheduledTests){
                    // reset alarm
                    test.resetAlarm(String.valueOf(test.getTestId()), false);
                    // and update test in db
                    TestService.getInstance().update(test, null);
                }

                Timber.i("Guest alarms reset is finished.");
            }

            private void processAlarm(Context context, Intent intent){
                Bundle args = intent.getBundleExtra(BUNDLE_ARGS_KEY);
                if(args == null){
                    Timber.w("Bundle args is null. Alarm can not be processed.");
                    return;
                }

                String scheduledTestId = args.getString(SCHEDULED_TEST_ID_KEY);
                if(scheduledTestId == null || scheduledTestId.isEmpty()){
                    Timber.w("scheduledTestId is null. Alarm can not be processed.");
                    return;
                }
                String message = args.getString(MESSAGE_KEY);
                if(message == null){
                    message = "";
                }
                int alarmId = args.getInt(ALARM_ID_KEY, NO_ALARM_ID);
                boolean forUser = args.getBoolean(FOR_USER_KEY);

                if(forUser){
                    final String alarmUserUid = args.getString(USER_UID_KEY);

                    // If is an alarm for user but user is not logged in cancel alarm and return.
                    if(!CoreUtilities.Auth.isUserLoggedIn()){
                        Timber.w("Alarm [" + alarmId + "] is for user [" + alarmUserUid + "] but user is not logged in.");
                        ScheduledTestAlarmManager.getInstance().cancelAlarm(scheduledTestId, message, true, alarmId);
                        return;
                    }

                    // This will be used to avoid launching an alarm for user A if user B is logged in.
                    final String loggedInUserUid = UserService.getInstance().getUserUid();
                    if(loggedInUserUid != null && !loggedInUserUid.equals(alarmUserUid)){
                        Timber.w("Alarm [" + alarmId + "] is for user [" + alarmUserUid + "] while user [" + loggedInUserUid + "] is logged in.");
                        ScheduledTestAlarmManager.getInstance().cancelAlarm(scheduledTestId, message, true, alarmId);
                        return;
                    }

                    // everything is ok
                    checkIfAlarmMustBeCanceled(true, scheduledTestId);
                    launchNotification(context, true, scheduledTestId, message, alarmId);
                }
                else {
                    // If is an alarm for guest but user is logged in cancel alarm and return.
                    if(CoreUtilities.Auth.isUserLoggedIn()){
                        Timber.w("Alarm [" + alarmId + " ] is for guest but [" + UserService.getInstance().getUserUid() + "] user is not logged in.");
                        ScheduledTestAlarmManager.getInstance().cancelAlarm(scheduledTestId, message, false, alarmId);
                        return;
                    }

                    // everything is ok
                    checkIfAlarmMustBeCanceled(false, scheduledTestId);
                    launchNotification(context, false, scheduledTestId, message, alarmId);
                }
            }

            private void checkIfAlarmMustBeCanceled(boolean forUser, String scheduledTestId){
                // If test was 'one time scheduled test' then cancel alarm and update test, in order
                // to disable alarm switch.
                // Canceling alarm will have no effect because it was one time alarm but this will
                // reset values to default.
                if(forUser){
                    ThreadExecutorService.getInstance().execute(() -> checkIfAlarmMustBeCanceledForUser(scheduledTestId));
                }
                else{
                    ThreadExecutorService.getInstance().execute(() -> checkIfAlarmMustBeCanceledForGuest(scheduledTestId));
                }
            }

            private void checkIfAlarmMustBeCanceledForUser(String scheduledTestId){
                TestService.getInstance()
                        .getLocalTestsCollection()
                        .document(scheduledTestId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(!task.isSuccessful() || task.getResult() == null){
                                    Timber.w("result is not valid");
                                    Timber.w(task.getException());
                                    return;
                                }

                                TestDocument scheduledTest = task.getResult().toObject(TestDocument.class);
                                if(scheduledTest == null){
                                    Timber.w("scheduledTest is null");
                                    return;
                                }

                                if(scheduledTest.isOneTime()){
                                    Timber.i("Alarm [" + scheduledTest.getAlarmId() + "] is oneTime alarm and must be " +
                                            "canceled because was already launched.");
                                    // Set an unique id in order to identify device where alarm was updated if user
                                    // is logged on multiple devices.
                                    scheduledTest.setAlarmDeviceId(SettingsService.getInstance().getSimulatedDeviceId());

                                    // Mark that alarm was launched on this device. This flag will be
                                    // used to notify other devices that alarm was already launched
                                    // and should not be canceled.
                                    scheduledTest.setAlarmWasLaunched(true);

                                    // This update must be done before 'scheduledTest.cancelAlarm()'
                                    // and will trigger alarmListener from ApplicationController,
                                    // on modified case. This update is needed in order to notify
                                    // other devices that alarm was launched and should not be removed.
                                    // If update is made after 'scheduledTest.cancelAlarm()' then
                                    // alarmListener from ApplicationController will enter on removed
                                    // case and new updated data will not be available, because in
                                    // removed case only previous document is given.
                                    TestService.getInstance().updateTest(scheduledTest, task.getResult(), new DataCallbacks.General() {
                                        @Override
                                        public void onSuccess() {
                                            // Canceling alarm will set scheduleActive flag with false, and
                                            // after the following update document will be removed from
                                            // active scheduled tests query.
                                            scheduledTest.cancelAlarm(scheduledTestId, true);

                                            // This update will trigger alarmListener from ApplicationController,
                                            // on removed case and because of the previous update, flag
                                            // alarmWasLaunched will be available.
                                            TestService.getInstance().updateTest(scheduledTest, task.getResult(), null);
                                        }

                                        @Override
                                        public void onFailure() {
                                            // If update fails do nothing. Alarm was already canceled
                                            // by alarm manager because is oneTime alarm. Alarm will
                                            // remain in DB as activated but in AlarmManger is not
                                            // set so it will not be triggered. User can deactivate
                                            // alarm manually (by deactivating I mean to turn off
                                            // the switch representing that alarm is active/not active
                                            // on scheduled tests adapter card view).
                                        }
                                    });

                                }
                            }
                        });
            }

            private void checkIfAlarmMustBeCanceledForGuest(String scheduledTestId){
                int idInteger;
                try {
                    idInteger = Integer.parseInt(scheduledTestId);
                }
                catch (NumberFormatException ex){
                    Timber.w(ex);
                    return;
                }

                RoomTest scheduledTest = TestService.getInstance().getTest(idInteger);
                if(scheduledTest == null){
                    Timber.w("scheduledTest is null");
                    return;
                }

                if(scheduledTest.isOneTime()){
                    scheduledTest.cancelAlarm(scheduledTestId, false);
                    TestService.getInstance().update(scheduledTest, null);
                }
            }

            private void launchNotification(Context context, boolean forUser, String scheduledTestId, String message, int alarmId){
                // prepare activity which will be opened when click on notification is made
                Intent testIntent;
                if(forUser){
                    testIntent = new Intent(context, UserTestActivity.class);
                }
                else{
                    testIntent = new Intent(context, GuestTestActivity.class);
                }
                // https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
                testIntent.putExtra(TestActivity.CALLED_BY_SCHEDULED_TEST_KEY, true);
                testIntent.putExtra(TestActivity.SCHEDULED_TEST_ID_KEY, scheduledTestId);
                testIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // https://stackoverflow.com/questions/67045607/how-to-resolve-missing-pendingintent-mutability-flag-lint-warning-in-android-a
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, testIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                // https://developer.android.com/training/notify-user/build-notification.html#java
                // https://www.youtube.com/watch?v=CZ575BuLBo4&ab_channel=CodinginFlow
                // prepare notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(context.getString(R.string.scheduled_test_alarm_notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(notifyPendingIntent);

                // https://developer.android.com/training/notify-user/build-notification.html#java
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this.
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                // notificationId is a unique int for each notification that you must define
                // https://stackoverflow.com/questions/39607856/what-is-notification-id-in-android
                // Alarm id can be used as unique id because must exist one notification per alarm,
                // so every notification will have a different id because every alarm will have a
                // different id.
                notificationManager.notify(alarmId, builder.build());
            }
        }
    }
}
