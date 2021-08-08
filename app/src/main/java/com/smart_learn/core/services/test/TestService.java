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
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.smart_learn.R;
import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserExpressionService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.UserWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.LessonEntrance;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionFullWrite;
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
import java.util.Calendar;
import java.util.Collections;
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

    public void setSchedule(DocumentSnapshot testSnapshot, boolean isScheduleActive, DataCallbacks.General callback){
        userTestServiceInstance.setSchedule(testSnapshot, isScheduleActive, callback);
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
    public void generateUserWordTest(ArrayList<WordDocument> valueList, Test testOptions, TestService.TestGenerationCallback callback){
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
    public void generateUserExpressionTest(ArrayList<ExpressionDocument> valueList, Test testOptions, TestService.TestGenerationCallback callback){
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
        if(valueList == null){
            return new ArrayList<>();
        }

        ArrayList<QuestionFullWrite> questions = new ArrayList<>();
        if(isCustomSelection){
            questionsNr = 10;
        }

        for(int i = 1; i <= questionsNr; i++){
            QuestionFullWrite tmp = new QuestionFullWrite(
                    i,
                    Question.Types.QUESTION_FULL_WRITE,
                    "word " + i  + " full write",
                    "reversed word " + i + " full write",
                    String.valueOf(i), "r" + i);
            questions.add(tmp);
        }

        return questions;
    }

    private ArrayList<QuestionMixed> generateQuestionsForWordsMixedLettersTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return generateQuestionsForMixedTest(valueList, questionsNr, isCustomSelection);
    }

    private ArrayList<QuestionQuiz> generateQuestionsForWordsQuizTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        if(valueList == null){
            return new ArrayList<>();
        }

        ArrayList<QuestionQuiz> questions = new ArrayList<>();
        if(isCustomSelection){
            questionsNr = 10;
        }

        for(int i = 1; i <= questionsNr; i++){

            ArrayList<String> options = new ArrayList<>();
            options.add("word word word word word word word word word word word word word word word word word word " + i);
            options.add("word word word word word word word word word word word word word word word word word word " + i);
            options.add("word word word word word word word word word word word word word word word word word word " + i);
            options.add("word word word word word word word word word word word word word word word word word word " + i);

            ArrayList<String> reversedOption = new ArrayList<>();
            reversedOption.add("A. word reversed: " + i);
            reversedOption.add("B. word reversed: " + i);
            reversedOption.add("C. word reversed: " + i);
            reversedOption.add("D. word reversed: " + i);

            QuestionQuiz tmp = new QuestionQuiz(
                    i,
                    Question.Types.QUESTION_QUIZ,
                    "translated word" + i,
                    "translated word reversed " + i,
                    new ArrayList<>(options),
                    new ArrayList<>(reversedOption),
                    1,
                    2
            );

            questions.add(tmp);
        }

        return questions;
    }

    private ArrayList<QuestionMixed> generateQuestionsForExpressionsMixedWordsTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        return generateQuestionsForMixedTest(valueList, questionsNr, isCustomSelection);
    }

    private ArrayList<QuestionTrueOrFalse> generateQuestionsForExpressionsTrueOrFalseTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        if(valueList == null){
            return new ArrayList<>();
        }

        ArrayList<QuestionTrueOrFalse> questions = new ArrayList<>();
        if(isCustomSelection){
            questionsNr = 10;
        }

        for(int i = 1; i <= questionsNr; i++){
            QuestionTrueOrFalse tmp = new QuestionTrueOrFalse(
                    i,
                    Question.Types.QUESTION_TRUE_OR_FALSE,
                    "expression expression expression expression expression expression expression " +
                            "expression expression expression expression expression expression expression" +
                            "expression expression expression expression expression expression expression" +
                            "expression expression expression expression expression expression expression" +
                            "expression expression expression expression expression expression expression" +
                            "expression expression expression expression expression expression expression" + i,
                    "reversed reversed reversed reversed reversed reversed reversed reversed" +
                            "reversed reversed reversed reversed reversed reversed reversed reversed" +
                            "reversed reversed reversed reversed reversed reversed reversed reversed" +
                            "reversed reversed reversed reversed reversed reversed reversed reversed" +
                            "reversed reversed reversed reversed reversed reversed reversed reversed" +
                            "reversed reversed reversed reversed reversed reversed reversed reversed" +
                            "reversed reversed reversed reversed reversed reversed reversed reversed"
                            + i,
                    "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" +
                            "option option option option option option option option option option" + i,
                    "option reversed option reversed option reversed option reversed option reversed" +
                            "option reversed option reversed option reversed option reversed option reversed" +
                            "option reversed option reversed option reversed option reversed option reversed" +
                            "option reversed option reversed option reversed option reversed option reversed" +
                            "option reversed option reversed option reversed option reversed option reversed" +
                            "option reversed option reversed option reversed option reversed option reversed" +
                            "option reversed option reversed option reversed option reversed option reversed" +
                            "option reversed option reversed option reversed option reversed option reversed" +
                            "" + i,
                    QuestionTrueOrFalse.RESPONSE_TRUE,
                    QuestionTrueOrFalse.RESPONSE_FALSE);
            questions.add(tmp);
        }

        return questions;
    }

    private ArrayList<QuestionMixed> generateQuestionsForMixedTest(ArrayList<LessonEntrance> valueList, int questionsNr, boolean isCustomSelection){
        if(valueList == null){
            return new ArrayList<>();
        }

        ArrayList<QuestionMixed> questions = new ArrayList<>();
        if(isCustomSelection){
            questionsNr = 10;
        }

        ArrayList<String> startOrder = new ArrayList<>();
        startOrder.add("a");
        startOrder.add("d");
        startOrder.add("b");
        startOrder.add("c");
        startOrder.add("e");
        startOrder.add("f");
        startOrder.add("g");


        ArrayList<String> answerOrder = new ArrayList<>();
        answerOrder.add("a");
        answerOrder.add("b");
        answerOrder.add("c");
        answerOrder.add("d");
        answerOrder.add("e");
        answerOrder.add("f");
        answerOrder.add("g");

//        ArrayList<String> startOrder = new ArrayList<>();
//        startOrder.add("word_1");
//        startOrder.add("word_7");
//        startOrder.add("word_3");
//        startOrder.add("word_5");
//        startOrder.add("word_4");
//        startOrder.add("word_6");
//        startOrder.add("word_2");
//        startOrder.add("word_10");
//        startOrder.add("word_9");
//        startOrder.add("word_8");
//        startOrder.add("word_1");
//        startOrder.add("word_7");
//        startOrder.add("word_3");
//        startOrder.add("word_5");
//        startOrder.add("word_4");
//        startOrder.add("word_6");
//        startOrder.add("word_2");
//        startOrder.add("word_10");
//        startOrder.add("word_9");
//        startOrder.add("word_8");
//        startOrder.add("word_1");
//        startOrder.add("word_7");
//        startOrder.add("word_3");
//        startOrder.add("word_5");
//        startOrder.add("word_4");
//        startOrder.add("word_6");
//        startOrder.add("word_2");
//        startOrder.add("word_10");
//        startOrder.add("word_9");
//        startOrder.add("word_8");
//        startOrder.add("word_1");
//        startOrder.add("word_7");
//        startOrder.add("word_3");
//        startOrder.add("word_5");
//        startOrder.add("word_4");
//        startOrder.add("word_6");
//        startOrder.add("word_2");
//        startOrder.add("word_10");
//        startOrder.add("word_9");
//        startOrder.add("word_8");
//
//
//        ArrayList<String> answerOrder = new ArrayList<>();
//        answerOrder.add("word_1");
//        answerOrder.add("word_2");
//        answerOrder.add("word_3");
//        answerOrder.add("word_4");
//        answerOrder.add("word_5");
//        answerOrder.add("word_6");
//        answerOrder.add("word_7");
//        answerOrder.add("word_8");
//        answerOrder.add("word_9");
//        answerOrder.add("word_10");


        for(int i = 1; i <= questionsNr; i++){
            QuestionMixed tmp = new QuestionMixed(
                    i,
                    Question.Types.QUESTION_MIXED,
                    //"a d b c d d d   s s s sd s  sd s d s ds d s ds d s s d s ds ds d s ds d s ds d sd s d sd s d s" + 1,
                    "word_1 word_1 word_1 word_1 word_1 word_1 word_1 word_1 word_1 word_1" + 1,
                    startOrder,
                    answerOrder
                    );
            questions.add(tmp);
        }

        return questions;
    }


    @NonNull
    @NotNull
    private ArrayList<LessonEntrance> convertWordsToLessonEntrance(ArrayList<Word> wordList){
        if(wordList == null || wordList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(Word word : wordList){
            convertedList.add(new LessonEntrance(word.getWord(),word.getTranslations()));
        }
        return convertedList;
    }

    @NonNull
    @NotNull
    private ArrayList<LessonEntrance> convertExpressionsToLessonEntrance(ArrayList<Expression> expressionList){
        if(expressionList == null || expressionList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(Expression expression : expressionList){
            convertedList.add(new LessonEntrance(expression.getExpression(),expression.getTranslations()));
        }
        return convertedList;
    }

    @NonNull
    @NotNull
    private ArrayList<LessonEntrance> convertWordDocumentsToLessonEntrance(ArrayList<WordDocument> wordList){
        if(wordList == null || wordList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(WordDocument word : wordList){
            convertedList.add(new LessonEntrance(word.getWord(), Translation.fromJsonToList(word.getTranslations())));
        }
        return convertedList;
    }

    @NonNull
    @NotNull
    private ArrayList<LessonEntrance> convertExpressionDocumentsToLessonEntrance(ArrayList<ExpressionDocument> expressionList){
        if(expressionList == null || expressionList.isEmpty()){
            return new ArrayList<>();
        }

        ArrayList<LessonEntrance> convertedList = new ArrayList<>();
        for(ExpressionDocument expression : expressionList){
            convertedList.add(new LessonEntrance(expression.getExpression(),  Translation.fromJsonToList(expression.getTranslations())));
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

        public static final String SCHEDULED_TEST_ID_KEY = "SCHEDULED_TEST_ID_KEY";
        public static final String FOR_USER_KEY = "FOR_USER_KEY";
        private static final String USER_UID_KEY = "USER_UID_KEY";
        private static final String ALARM_ID_KEY = "ALARM_ID_KEY";

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
            return (int) System.currentTimeMillis();
        }

        public int setExactAlarm(String scheduledTestId, boolean forUser, long time){
            final int id = getUniqueAlarmId();
            setExactAlarm(id, scheduledTestId, forUser, time);
            return id;
        }

        public void setExactAlarm(int alarmId, String scheduledTestId, boolean forUser, long time){
            final Context context = ApplicationController.getInstance().getApplicationContext();
            // https://stackoverflow.com/questions/28262650/what-is-the-difference-between-rtc-and-rtc-wakeup-of-alarmmanager?rq=1
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, getPendingIntent(context, alarmId, scheduledTestId, forUser));
            Timber.i("Alarm [" + alarmId +  "] was set for time [" + CoreUtilities.General.longToDateTime(time) + "]");
        }

        public int setAlarmRepeatingInSpecificDays(String scheduledTestId, boolean forUser, int hour, int minute,
                                                   boolean monday, boolean tuesday, boolean wednesday,
                                                   boolean thursday, boolean friday, boolean sunday,
                                                   boolean saturday){
            // this will be the base id from which the other id's will be constructed
            int baseId = getUniqueAlarmId();
            setAlarmRepeatingInSpecificDays(
                    baseId,
                    scheduledTestId,
                    forUser,
                    hour,
                    minute,
                    monday,
                    tuesday,
                    wednesday,
                    thursday,
                    friday,
                    saturday,
                    sunday
            );
            return baseId;
        }

        public void setAlarmRepeatingInSpecificDays(int baseAlarmId, String scheduledTestId, boolean forUser, int hour, int minute,
                                                   boolean monday, boolean tuesday, boolean wednesday,
                                                   boolean thursday, boolean friday, boolean sunday,
                                                   boolean saturday){
            // https://stackoverflow.com/questions/8469705/how-to-set-multiple-alarms-using-alarm-manager-in-android
            // https://stackoverflow.com/questions/17894067/set-repeat-days-of-week-alarm-in-android
            // https://stackoverflow.com/questions/28262650/what-is-the-difference-between-rtc-and-rtc-wakeup-of-alarmmanager?rq=1

            Context context = ApplicationController.getInstance().getApplicationContext();

            // for every selected day must be set an alarm with an unique id with repeating interval of one week
            long weekInterval = 7 * AlarmManager.INTERVAL_DAY;

            if(monday){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.MONDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 1, scheduledTestId, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 1 +  "] was set for [MONDAY at " + hour + ":" + minute + "]");
            }

            if(tuesday){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.TUESDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 2, scheduledTestId, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 2 +  "] was set for [TUESDAY at " + hour + ":" + minute + "]");
            }

            if(wednesday){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.WEDNESDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 3, scheduledTestId, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 3 +  "] was set for [WEDNESDAY at " + hour + ":" + minute + "]");
            }

            if(thursday){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.THURSDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 4, scheduledTestId, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 4 +  "] was set for [THURSDAY at " + hour + ":" + minute + "]");
            }

            if(friday){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.FRIDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 5, scheduledTestId, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 5 +  "] was set for [FRIDAY at " + hour + ":" + minute + "]");
            }

            if(saturday){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.SATURDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 6, scheduledTestId, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 6 +  "] was set for [SATURDAY at " + hour + ":" + minute + "]");
            }

            if(sunday){
                long time = CoreUtilities.General.timeToLong(hour, minute, Calendar.SUNDAY);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, weekInterval, getPendingIntent(context, baseAlarmId + 7, scheduledTestId, forUser));
                Timber.i("Repeating alarm [" + baseAlarmId + 7 +  "] was set for [SUNDAY at " + hour + ":" + minute + "]");
            }
        }


        public void cancelAlarm(String scheduledTestId, boolean forUser, int id){
            if(id == NO_ALARM_ID){
                Timber.w("Alarm id [ " + id +  "] is not valid. Cannot cancel alarm.");
                return;
            }
            alarmManager.cancel(getPendingIntent(ApplicationController.getInstance().getApplicationContext(), id, scheduledTestId, forUser));
            Timber.i("Alarm [" + id +  "] was canceled.");
        }

        public void cancelAlarmRepeatingInSpecificDays(String scheduledTestId, boolean forUser, int baseId,
                                                       boolean monday, boolean tuesday, boolean wednesday,
                                                       boolean thursday, boolean friday, boolean sunday,
                                                       boolean saturday){
            if(monday){
                cancelAlarm(scheduledTestId, forUser, baseId + 1);
            }

            if(tuesday){
                cancelAlarm(scheduledTestId, forUser, baseId + 2);
            }

            if(wednesday){
                cancelAlarm(scheduledTestId, forUser, baseId + 3);
            }

            if(thursday){
                cancelAlarm(scheduledTestId, forUser, baseId + 4);
            }

            if(friday){
                cancelAlarm(scheduledTestId, forUser, baseId + 5);
            }

            if(saturday) {
                cancelAlarm(scheduledTestId, forUser, baseId + 6);
            }

            if(sunday){
                cancelAlarm(scheduledTestId, forUser, baseId + 7);
            }

        }

        private PendingIntent getPendingIntent(Context context, int alarmId, String scheduledTestId, boolean forUser){
            Intent intent = new Intent(context, ScheduledTestAlarmReceiver.class);
            intent.putExtra(SCHEDULED_TEST_ID_KEY, scheduledTestId);
            intent.putExtra(FOR_USER_KEY, forUser);
            intent.putExtra(ALARM_ID_KEY, alarmId);
            if(forUser){
                // This will be used to avoid launching an alarm for user A if user B is logged in.
                intent.putExtra(USER_UID_KEY, UserService.getInstance().getUserUid());
            }
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
                Bundle args = intent.getExtras();
                if(args == null){
                    Timber.w("Bundle args is null. Alarm can not be processed.");
                    return;
                }

                String scheduledTestId = args.getString(SCHEDULED_TEST_ID_KEY);
                if(scheduledTestId == null || scheduledTestId.isEmpty()){
                    Timber.w("scheduledTestId is null. Alarm can not be processed.");
                    return;
                }
                int alarmId = args.getInt(ALARM_ID_KEY, NO_ALARM_ID);
                boolean forUser = args.getBoolean(FOR_USER_KEY);

                if(forUser){
                    final String alarmUserUid = args.getString(USER_UID_KEY);

                    // If is an alarm for user but user is not logged in cancel alarm and return.
                    if(!CoreUtilities.Auth.isUserLoggedIn()){
                        Timber.w("Alarm [" + alarmId + "] is for user [" + alarmUserUid + "] but user is not logged in.");
                        ScheduledTestAlarmManager.getInstance().cancelAlarm(scheduledTestId, true, alarmId);
                        return;
                    }

                    // This will be used to avoid launching an alarm for user A if user B is logged in.
                    final String loggedInUserUid = UserService.getInstance().getUserUid();
                    if(loggedInUserUid != null && !loggedInUserUid.equals(alarmUserUid)){
                        Timber.w("Alarm [" + alarmId + "] is for user [" + alarmUserUid + "] while user [" + loggedInUserUid + "] is logged in.");
                        ScheduledTestAlarmManager.getInstance().cancelAlarm(scheduledTestId, true, alarmId);
                        return;
                    }

                    // everything is ok
                    checkIfAlarmMustBeCanceled(true, scheduledTestId);
                    launchNotification(context, true, scheduledTestId);
                }
                else {
                    // If is an alarm for guest but user is logged in cancel alarm and return.
                    if(CoreUtilities.Auth.isUserLoggedIn()){
                        Timber.w("Alarm [" + alarmId + " ] is for guest but [" + UserService.getInstance().getUserUid() + "] user is not logged in.");
                        ScheduledTestAlarmManager.getInstance().cancelAlarm(scheduledTestId, false, alarmId);
                        return;
                    }

                    // everything is ok
                    checkIfAlarmMustBeCanceled(false, scheduledTestId);
                    launchNotification(context, false, scheduledTestId);
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
                                    scheduledTest.cancelAlarm(scheduledTestId, true);

                                    // TODO: This update will trigger alarmListener from ApplicationController,
                                    //  on removed case, and alarm will be canceled again. try to find
                                    //  another way to do that.
                                    //
                                    // TODO: Important: alarm must be canceled even if application
                                    //  is not open, so if alarmListener is not in a background service
                                    //  cancel must be done here, when alarm is triggered.
                                    TestService.getInstance().updateTest(scheduledTest, task.getResult(), null);
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

            private void launchNotification(Context context, boolean forUser, String scheduledTestId){
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
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.scheduled_test_alarm_notification_message)))
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
                notificationManager.notify(1, builder.build());
            }
        }
    }
}
