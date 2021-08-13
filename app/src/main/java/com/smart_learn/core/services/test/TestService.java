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
import com.smart_learn.core.services.expression.GuestExpressionService;
import com.smart_learn.core.services.word.GuestWordService;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.helpers.ThreadExecutorService;
import com.smart_learn.core.services.expression.UserExpressionService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.word.UserWordService;
import com.smart_learn.core.helpers.CoreUtilities;
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
import com.smart_learn.core.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import timber.log.Timber;

public class TestService {

    public static final int SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS = 1;
    public static final int SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS = 2;
    public static final int SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS = 3;
    public static final int SHOW_ONLY_LOCAL_SCHEDULED_TESTS = 4;
    public static final int SHOW_ONLY_ONLINE_TESTS = 5;

    private static TestService instance;

    private final String errorGeneralCanNotGenerateTest;
    private final String errorInvalidNumberOfQuestions;
    private final String errorNoGeneratedQuestions;
    private final String errorInvalidTestType;
    private final String errorInvalidConnexion;
    private final String errorCouldNotExtractWords;
    private final String errorCouldNotExtractExpressions;
    private final String errorNoWords;
    private final String errorNoExpressions;
    private final String errorNoValues;
    private final String errorNotEnoughWordsForQuizTest;
    private final String errorCanNotSaveTest;

    @NonNull @NotNull
    private final GuestTestService guestTestServiceInstance;
    @NonNull @NotNull
    private final UserTestService userTestServiceInstance;
    @NonNull @NotNull
    private final SecureRandom secureRandom;

    private TestService() {
        guestTestServiceInstance = GuestTestService.getInstance();
        userTestServiceInstance = UserTestService.getInstance();
        secureRandom = CoreUtilities.General.getSecureRandomInstance();

        Context context = ApplicationController.getInstance().getApplicationContext();
        errorGeneralCanNotGenerateTest  = context == null ? "" : context.getString(R.string.error_can_not_generate_test);
        errorInvalidNumberOfQuestions   = context == null ? "" : context.getString(R.string.error_invalid_number_of_questions);
        errorNoGeneratedQuestions       = context == null ? "" : context.getString(R.string.error_no_generated_questions);
        errorInvalidTestType            = context == null ? "" : context.getString(R.string.error_invalid_test_type);
        errorInvalidConnexion           = context == null ? "" : context.getString(R.string.error_invalid_conexion);
        errorCouldNotExtractWords       = context == null ? "" : context.getString(R.string.error_could_not_extract_words);
        errorCouldNotExtractExpressions = context == null ? "" : context.getString(R.string.error_could_not_extract_expressions);
        errorNoWords                    = context == null ? "" : context.getString(R.string.error_no_words);
        errorNoExpressions              = context == null ? "" : context.getString(R.string.error_no_expressions);
        errorNoValues                   = context == null ? "" : context.getString(R.string.error_no_values_for_test);
        errorNotEnoughWordsForQuizTest  = context == null ? "" : context.getString(R.string.error_no_values_for_quiz_test);
        errorCanNotSaveTest             = context == null ? "" : context.getString(R.string.error_can_not_save_generated_test);
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

    public LiveData<Integer> getLiveNumberOfInProgressTests(){
        return guestTestServiceInstance.getLiveNumberOfInProgressTests();
    }

    public LiveData<Integer> getLiveNumberOfFinishedTests(){
        return guestTestServiceInstance.getLiveNumberOfFinishedTests();
    }

    public LiveData<Float> getLiveSuccessRate(){
        return guestTestServiceInstance.getLiveSuccessRate();
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertWordDocumentsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onFailure(errorCouldNotExtractWords);
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertExpressionDocumentsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onFailure(errorCouldNotExtractExpressions);
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
            Timber.w("testOptions is null");
            return;
        }

        // in order to generate a new test, test should have a positive number of questions
        if(questionsNr <= 0 && questionsNr != Test.USE_ALL){
            callback.onFailure(errorInvalidNumberOfQuestions);
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
                    callback.onFailure(errorInvalidTestType);
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
                            // TODO: check exception and show specific error messages.
                            callback.onFailure(errorInvalidConnexion);
                            return;
                        }

                        ArrayList<DocumentSnapshot> wordList = new ArrayList<>(task.getResult().getDocuments());
                        if(wordList.isEmpty()){
                            Timber.w("no words");
                            callback.onFailure(errorNoWords);
                            return;
                        }

                        ArrayList<LessonEntrance> valueList = convertWordDocumentsToLessonEntrance(wordList);
                        if(wordList.size() != valueList.size()){
                            Timber.w("Error at conversion");
                            callback.onFailure(errorCouldNotExtractWords);
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
                            // TODO: check exception and show specific error messages.
                            callback.onFailure(errorInvalidConnexion);
                            return;
                        }

                        // get expressions
                        ArrayList<DocumentSnapshot> expressionsList = new ArrayList<>(task.getResult().getDocuments());
                        if(expressionsList.isEmpty()){
                            Timber.w("no expressions");
                            callback.onFailure(errorNoExpressions);
                            return;
                        }

                        ArrayList<LessonEntrance> valueList = convertExpressionDocumentsToLessonEntrance(expressionsList);
                        if(expressionsList.size() != valueList.size()){
                            Timber.w("Error at conversion");
                            callback.onFailure(errorCouldNotExtractExpressions);
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
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
                            // TODO: check exception and show specific error messages.
                            callback.onFailure(errorGeneralCanNotGenerateTest);
                            return;
                        }

                        UserDocument userDocument = task.getResult().toObject(UserDocument.class);
                        if(userDocument == null){
                            Timber.w("userDocument is null");
                            callback.onFailure(errorGeneralCanNotGenerateTest);
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertWordsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onFailure(errorCouldNotExtractWords);
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> {
            ArrayList<LessonEntrance> convertedList = convertExpressionsToLessonEntrance(valueList);
            if(valueList.size() != convertedList.size()){
                Timber.w("Error at conversion");
                callback.onFailure(errorCouldNotExtractExpressions);
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
            Timber.w("testOptions is null");
            return;
        }

        // in order to generate a new test, test should have a positive number of questions
        if(questionsNr <= 0 && questionsNr != Test.USE_ALL){
            callback.onFailure(errorInvalidNumberOfQuestions);
            Timber.w("questions number [" + questionsNr + "] is not valid");
            return;
        }

        int lessonId;
        try{
            lessonId = Integer.parseInt(testOptions.getLessonId());
        } catch (NumberFormatException ex){
            callback.onFailure(errorGeneralCanNotGenerateTest);
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
                        callback.onFailure(errorNoWords);
                        return;
                    }
                    valueList = convertWordsToLessonEntrance(wordList);
                    if(wordList.size() != valueList.size()){
                        Timber.w("Error at conversion");
                        callback.onFailure(errorCouldNotExtractWords);
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
                        callback.onFailure(errorNoExpressions);
                        return;
                    }
                    valueList = convertExpressionsToLessonEntrance(expressionList);
                    if(expressionList.size() != valueList.size()){
                        Timber.w("Error at conversion");
                        callback.onFailure(errorCouldNotExtractExpressions);
                        return;
                    }
                    break;
                default:
                    Timber.w("test type [" + testOptions.getType() + "] is not a valid test");
                    callback.onFailure(errorInvalidTestType);
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
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

        // make a filter in order to keep only valid items
        valueList = filterValues(valueList);
        if(valueList.isEmpty()){
            callback.onFailure(errorNoValues);
            return;
        }

        // questionNr are nr of questions which will be generated, so maximum for generated questions
        // can be valueList.size()
        if(questionsNr > valueList.size()){
            questionsNr = valueList.size();
        }

        int userType;
        if(isForUser){
            userType = QuestionMetadata.Users.USER_LOGGED_IN;
        }
        else{
            userType = QuestionMetadata.Users.GUEST;
        }

        String questionsJson;
        int generatedQuestionsNr;
        switch (testOptions.getType()){
            case Test.Types.WORD_WRITE:
                ArrayList<QuestionFullWrite> A = generateQuestionsForWordsFullWriteTest(userType, valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = A.size();
                questionsJson = DataUtilities.General.fromListToJson(A);
                break;
            case Test.Types.WORD_QUIZ:
                // For this type test are needed minimum MIN_ITEMS_NECESSARY_FOR_GENERATION words in
                // order to generate the test.
                if(valueList.size() < QuestionQuiz.MIN_ITEMS_NECESSARY_FOR_GENERATION){
                    callback.onFailure(errorNotEnoughWordsForQuizTest + " " + QuestionQuiz.MIN_ITEMS_NECESSARY_FOR_GENERATION);
                    return;
                }
                ArrayList<QuestionQuiz> B = generateQuestionsForWordsQuizTest(userType, valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = B.size();
                questionsJson = DataUtilities.General.fromListToJson(B);
                break;
            case Test.Types.WORD_MIXED_LETTERS:
                ArrayList<QuestionMixed> C = generateQuestionsForWordsMixedLettersTest(userType, valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = C.size();
                questionsJson = DataUtilities.General.fromListToJson(C);
                break;
            case Test.Types.EXPRESSION_MIXED_WORDS:
                ArrayList<QuestionMixed> D = generateQuestionsForExpressionsMixedWordsTest(userType, valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = D.size();
                questionsJson = DataUtilities.General.fromListToJson(D);
                break;
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                ArrayList<QuestionTrueOrFalse> E = generateQuestionsForExpressionsTrueOrFalseTest(userType, valueList, questionsNr, testOptions.isUseCustomSelection());
                generatedQuestionsNr = E.size();
                questionsJson = DataUtilities.General.fromListToJson(E);
                break;
            default:
                Timber.w("test type [" + testOptions.getType() + "] is not a valid test");
                callback.onFailure(errorInvalidTestType);
                return;
        }

        if(generatedQuestionsNr < 1){
            Timber.w(" generated questions nr [" + generatedQuestionsNr + "] is not valid");
            callback.onFailure(errorNoGeneratedQuestions);
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

    private ArrayList<LessonEntrance> filterValues(ArrayList<LessonEntrance> valueList){
        // get items which have at least 1 translation
        ArrayList<LessonEntrance> newList = new ArrayList<>();
        for(LessonEntrance item : valueList){
            if(item.getTranslations().isEmpty()){
                continue;
            }
            newList.add(item);
        }
        return newList;
    }

    private void saveGuestTest(Test test, TestService.TestGenerationCallback callback){
        if(!(test instanceof RoomTest)){
            callback.onFailure(errorCanNotSaveTest);
            Timber.w("test not instanceof of RoomTest");
            return;
        }

        guestTestServiceInstance.insert((RoomTest) test, new DataCallbacks.RoomInsertionCallback() {
            @Override
            public void onSuccess(long id) {
                callback.onSuccess(String.valueOf(Math.toIntExact(id)));
            }

            @Override
            public void onFailure() {
                callback.onFailure(errorCanNotSaveTest);
            }
        });
    }

    private void saveUserTest(Test test, TestService.TestGenerationCallback callback){
        if(!(test instanceof TestDocument)){
            callback.onFailure(errorCanNotSaveTest);
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
                callback.onSuccess(newTestDocumentRef.getId());
            }

            @Override
            public void onFailure() {
                callback.onFailure(errorCanNotSaveTest);
            }
        });
    }




    @NonNull @NotNull
    private ArrayList<LessonEntrance> extractValues(ArrayList<LessonEntrance> valueList, int questionsNr){
        if(valueList == null || valueList.isEmpty()){
            return new ArrayList<>();
        }

        // If number of questions which should be generated is bigger or the same as the actually
        // list size, return the entire list.
        if(questionsNr >= valueList.size()) {
            return valueList;
        }

        // Here list contains more values that we need, so extract only 'questionsNr' values.

        // Add all values in an HashMap with (key, value) = (score, all values with that score)
        HashMap<Double, ArrayList<LessonEntrance>> hashMap = new HashMap<>();
        for(LessonEntrance item : valueList){
            if(item == null){
                continue;
            }

            double key = item.getStatistics().getScore();
            if(hashMap.containsKey(key)){
                ArrayList<LessonEntrance> currentList = hashMap.get(key);
                if(currentList == null){
                    currentList = new ArrayList<>();
                }
                currentList.add(item);
                hashMap.put(key, currentList);
            }
            else {
                hashMap.put(key, new ArrayList<>(Collections.singletonList(item)));
            }
        }

        // Sort hash map keys (score) ascending.
        ArrayList<Double> keySet = new ArrayList<>(hashMap.keySet());
        Collections.sort(keySet);

        // And extract only 'questionsNr' values.
        ArrayList<LessonEntrance> finalList = new ArrayList<>();
        for(Double score : keySet){
            // If all 'questionsNr' where extracted then is no need to continue.
            if(questionsNr < 1){
                break;
            }

            // Get all values for current score.
            ArrayList<LessonEntrance> currentList = hashMap.get(score);

            // If are no values then is nothing to extract so continue.
            if(currentList == null || currentList.isEmpty()){
                continue;
            }

            // If 'questionsNr' is bigger then currentList values that means all currentList values
            // can be added to the final list.
            int lim = currentList.size();
            if(questionsNr >= lim){
                finalList.addAll(currentList);
                questionsNr -= lim;
                continue;
            }

            // Here values from 'currentList' are more then we need to complete finalList so extract
            // only 'questionsNr' values.

            // Add all currentList indexes into a list and shuffle list in order to give a randomness.
            ArrayList<Integer> shuffledIndexes = new ArrayList<>();
            for(int i = 0; i < lim; i++){
                shuffledIndexes.add(i);
            }
            Collections.shuffle(shuffledIndexes, secureRandom);

            // Extract first 'questionsNr' values using shuffled indexes.
            for(int i = 0; i < questionsNr; i++){
                LessonEntrance extractedItem = currentList.get(shuffledIndexes.get(i));
                finalList.add(extractedItem);
            }
        }

        return finalList;
    }

    @NonNull @NotNull
    private LinkedList<LessonEntrance> generateAllValuesQueue(ArrayList<LessonEntrance> valueList){
        if(valueList == null || valueList.isEmpty()){
            throw new UnsupportedOperationException("Here valueList can not be null or empty");
        }

        LinkedList<LessonEntrance> queue = new LinkedList<>(valueList);
        // Queue will be shuffles in order to give a randomness.
        Collections.shuffle(queue, secureRandom);
        return queue;
    }

    @NonNull @NotNull
    private LinkedList<Pair<String,Translation>> generateAllTranslationsQueue(ArrayList<LessonEntrance> valueList){
        if(valueList == null || valueList.isEmpty()){
            throw new UnsupportedOperationException("Here translation can not be null or empty");
        }

        // Translations will be associated with values in order to know from what item is it, using
        // a pair of (valueId, translation).

        // Add all translations in an HashMap with (key, value) where:
        //   - key   --> score
        //   - value --> all translation with same score stored as Pair(itemId,translation)
        HashMap<Double, ArrayList<Pair<String,Translation>>> hashMap = new HashMap<>();

        // Go to every item.
        for(LessonEntrance item : valueList){
            if(item == null){
                continue;
            }

            // And save every item translation.
            for(Translation translation : item.getTranslations()){
                if(translation == null || translation.getTranslation().isEmpty()){
                    continue;
                }

                double key = translation.getStatistics().getScore();
                if(hashMap.containsKey(key)){
                    ArrayList<Pair<String,Translation>> currentList = hashMap.get(key);
                    if(currentList == null){
                        currentList = new ArrayList<>();
                    }
                    currentList.add(new Pair<>(item.getId(), translation));
                    hashMap.put(key, currentList);
                }
                else {
                    hashMap.put(key, new ArrayList<>(Collections.singletonList(new Pair<>(item.getId(), translation))));
                }
            }
        }

        // Shuffle all translations with same score in order to give some randomness.
        for(Double key : hashMap.keySet()){
            ArrayList<Pair<String,Translation>> currentList = hashMap.get(key);
            if(currentList == null){
                hashMap.remove(key);
                continue;
            }
            Collections.shuffle(currentList, secureRandom);
            hashMap.put(key, currentList);
        }

        // Sort hash map keys (score) ascending.
        ArrayList<Double> sortedKeySet = new ArrayList<>(hashMap.keySet());
        Collections.sort(sortedKeySet);

        // Add final values in a queue.
        LinkedList<Pair<String,Translation>> queue = new LinkedList<>();
        for(Double key : sortedKeySet){
            ArrayList<Pair<String,Translation>> currentList = hashMap.get(key);
            if(currentList == null){
                continue;
            }
            queue.addAll(currentList);
        }

        return queue;
    }

    @NonNull @NotNull
    private LinkedList<Translation> generateItemTranslationsQueue(LessonEntrance item){
        if(item == null){
            throw new UnsupportedOperationException("Here item can not be null or empty");
        }
        // Process all item translations.
        LinkedList<Pair<String, Translation>> tmp = generateAllTranslationsQueue(new ArrayList<>(Collections.singletonList(item)));

        // And get only translations because is no need for item id.
        LinkedList<Translation> queue = new LinkedList<>();
        for(Pair<String, Translation> pair : tmp){
            queue.add(pair.second);
        }

        return queue;
    }

    @NonNull @NotNull
    private Translation getNextItemTranslation(LinkedList<Translation> translations){
        if(translations == null || translations.isEmpty()){
            throw new UnsupportedOperationException("Here translation can not be null or empty");
        }

        // Extract first translation and add it at final in order to simulate a circular queue.
        Translation extractedTranslation = translations.poll();
        translations.add(extractedTranslation);

        // Before returning check if extracted translation value is valid.
        if(extractedTranslation == null){
            throw new UnsupportedOperationException("Here translation can not be null");
        }

        return extractedTranslation;
    }

    @NonNull @NotNull
    private Pair<String,Translation> getNextGeneralTranslation(LinkedList<Pair<String,Translation>> translations){
        if(translations == null || translations.isEmpty()){
            throw new UnsupportedOperationException("Here translation can not be null or empty");
        }

        // Extract first pair of (itemId,translation) from queue and add it at final in order to
        // simulate a circular queue.
        Pair<String,Translation> extractedTranslation = translations.poll();
        translations.add(extractedTranslation);

        // Before returning check if extracted translation value is valid.
        if(extractedTranslation == null){
            throw new UnsupportedOperationException("Here extractedTranslation can not be null");
        }
        if(extractedTranslation.first == null || extractedTranslation.first.isEmpty()){
            throw new UnsupportedOperationException("Here id can not be null or empty");
        }
        if(extractedTranslation.second == null || extractedTranslation.second.getTranslation().isEmpty()){
            throw new UnsupportedOperationException("Here Translation can not be null or with an empty value");
        }

        return extractedTranslation;
    }

    @NonNull @NotNull
    private Pair<String,Translation> getNextDifferentGeneralTranslation(LinkedList<Pair<String,Translation>> translations, HashSet<Long> ids){
        if(translations == null || translations.isEmpty()){
            throw new UnsupportedOperationException("Here translation can not be null or empty");
        }

        if(ids == null){
            ids = new HashSet<>();
        }

        // Try to get a translations which is not already in id's. If is not possible return any
        // translation. Max number of trials will be the actually size of the translations queue.
        int currentTry = 0;
        int nrOfTrials = translations.size();
        Pair<String,Translation> tmp;
        while (true){
            // Extract value.
            tmp = getNextGeneralTranslation(translations);

            // If number of trials is over return current value.
            if(currentTry > nrOfTrials){
                return tmp;
            }

            // Otherwise if translation is not different continue.
            if(ids.contains(tmp.second.getId())){
                currentTry++;
                continue;
            }

            // But if translation is different return it.
            return tmp;
        }
    }

    @NonNull @NotNull
    private LessonEntrance getNextGeneralItem(LinkedList<LessonEntrance> items){
        if(items == null || items.isEmpty()){
            throw new UnsupportedOperationException("Here items can not be null or empty");
        }

        // Extract first item from queue and add it at final in order to simulate a circular queue.
        LessonEntrance extractedItem = items.poll();
        items.add(extractedItem);

        // Before returning check if extracted translation value is valid.
        if(extractedItem == null){
            throw new UnsupportedOperationException("Here extractedItem can not be null");
        }

        return extractedItem;
    }

    @NonNull @NotNull
    private LessonEntrance getNextDifferentGeneralItem(LinkedList<LessonEntrance> items, HashSet<String> ids){
        if(items == null || items.isEmpty()){
            throw new UnsupportedOperationException("Here items can not be null or empty");
        }

        if(ids == null){
            ids = new HashSet<>();
        }

        // Try to get an item which is not already in id's. If is not possible return any
        // item. Max number of trials will be the actually size of the items queue.
        int currentTry = 0;
        int nrOfTrials = items.size();
        LessonEntrance tmp;
        while (true){
            // Extract value.
            tmp = getNextGeneralItem(items);

            // If number of trials is over return current value.
            if(currentTry > nrOfTrials){
                return tmp;
            }

            // Otherwise if item is not different continue.
            if(ids.contains(tmp.getId())){
                currentTry++;
                continue;
            }

            // But if item is different return it.
            return tmp;
        }
    }

    private int generateRandomIndex(int lim){
        // generate in interval [0, lim)
        return secureRandom.nextInt(lim);
    }







    private ArrayList<QuestionFullWrite> generateQuestionsForWordsFullWriteTest(int usertype, ArrayList<LessonEntrance> valueList,
                                                                                int questionsNr, boolean isCustomSelection){
        if(isCustomSelection){
            // If is custom selection will be used all values from valueList because these values were
            // selected by user/guest.
           return tryToGenerateQuestionsForWordsFullWriteTest(usertype, valueList);
        }

        // Otherwise use extracted values.
        return tryToGenerateQuestionsForWordsFullWriteTest(usertype, extractValues(valueList, questionsNr));
    }

    private ArrayList<QuestionFullWrite> tryToGenerateQuestionsForWordsFullWriteTest(int userType, ArrayList<LessonEntrance> valueList){
        // Go to every entrance and generate question.
        ArrayList<QuestionFullWrite> questions = new ArrayList<>();
        // for every test will be 'size' questions so 'idx' can be used as unique id for the question.
        int size = valueList.size();
        for(int idx = 0; idx < size; idx++){
            LessonEntrance word = valueList.get(idx);
            LinkedList<Translation> wordTranslationsQueue = generateItemTranslationsQueue(word);

            // 1.1 Normal value is actually current word.
            String questionValue = word.getValue();

            // 1.2 Reversed value will be a random translation of the current word.
            Translation qvrTranslation = getNextItemTranslation(wordTranslationsQueue);
            String questionValueReversed = qvrTranslation.getTranslation();

            // 1.3 Add correct normal answers (a correct normal answer will be any of the translations
            //     of the current word). Also at this full iteration save translations id's.
            ArrayList<String> correctAnswers = new ArrayList<>();
            HashSet<Long> translationIds = new HashSet<>();
            for(Translation translation : word.getTranslations()){
                correctAnswers.add(translation.getTranslation());
                // Also add translation id to identifiers.
                translationIds.add(translation.getId());
            }

            // 1.4 Add reversed correct answer (reversed correct answer will be actually the current word).
            ArrayList<String> correctAnswersReversed = new ArrayList<>(Collections.singletonList(word.getValue()));

            // 2. Create question

            // 2.1  Extract identifiers.
            ArrayList<QuestionIdentifier> identifiersArrayList = new ArrayList<>(
                    Collections.singletonList(
                            new QuestionIdentifier(
                                    QuestionIdentifier.Identifiers.WORD,
                                    word.getId(),
                                    translationIds
                            )
                    )
            );

            ArrayList<QuestionIdentifier> reversedIdentifiersArrayList = new ArrayList<>(
                    Collections.singletonList(
                            new QuestionIdentifier(
                                    QuestionIdentifier.Identifiers.WORD,
                                    word.getId(),
                                    new HashSet<>(Collections.singletonList(qvrTranslation.getId()))
                            )
                    )
            );

            // 2.2 Create metadata values.
            QuestionMetadata metadata = new QuestionMetadata(
                    userType,
                    identifiersArrayList,
                    reversedIdentifiersArrayList
            );

            // 2.3 Create current type question
            QuestionFullWrite tmp = new QuestionFullWrite(
                    idx, // counter 'idx' can be used as unique id
                    Question.Types.QUESTION_FULL_WRITE,
                    questionValue,
                    questionValueReversed,
                    metadata,
                    correctAnswers,
                    correctAnswersReversed
            );

            // 2.4 And finally add the question at questions list.
            questions.add(tmp);
        }

        return questions;
    }


    private ArrayList<QuestionQuiz> generateQuestionsForWordsQuizTest(int usertype, ArrayList<LessonEntrance> valueList,
                                                                      int questionsNr, boolean isCustomSelection){
        if(isCustomSelection){
            // If is custom selection will be used all values from valueList because these values were
            // selected by user/guest.
            return tryToGenerateQuestionsForWordsQuizTest(usertype, valueList);
        }

        // Otherwise use extracted values.
        return tryToGenerateQuestionsForWordsQuizTest(usertype, extractValues(valueList, questionsNr));
    }

    private ArrayList<QuestionQuiz> tryToGenerateQuestionsForWordsQuizTest(int userType, ArrayList<LessonEntrance> valueList){
        // 1. Add 2 queues:
        //      - For storing all words from value list.
        //      - For storing all translations from every word.
        LinkedList<LessonEntrance> allWordsQueue = generateAllValuesQueue(valueList);
        LinkedList<Pair<String,Translation>> allTranslationsQueue = generateAllTranslationsQueue(valueList);

        // 2. Go to every entrance and generate question.
        ArrayList<QuestionQuiz> questions = new ArrayList<>();
        // for every test will be 'size' questions so 'idx' can be used as unique id for the question.
        int size = valueList.size();
        for(int idx = 0; idx < size; idx++){
            LessonEntrance word = valueList.get(idx);
            LinkedList<Translation> wordTranslationsQueue = generateItemTranslationsQueue(word);

            // Used to save identifiers: (key, value) = (wordId, associated translations id's)
            HashMap<String, HashSet<Long>> normalIdentifiers = new HashMap<>();
            HashMap<String, HashSet<Long>> reversedIdentifiers = new HashMap<>();

            // 2.1 Set question values.

            // 2.1.1 Normal value is actually current word.
            String questionValue = word.getValue();

            // 2.1.2 Reversed value will be a random translation of the current word.
            Translation qvrTranslation = getNextItemTranslation(wordTranslationsQueue);
            String questionValueReversed = qvrTranslation.getTranslation();
            reversedIdentifiers.put(word.getId(), new HashSet<>(Collections.singletonList(qvrTranslation.getId())));

            // 2.1.3 Add normal answer options.
            ArrayList<Pair<String, Translation>> normalOptionsPairs = new ArrayList<>();

            // Correct answer value will be a random translation of the current word.
            Translation cavTranslation = getNextItemTranslation(wordTranslationsQueue);
            normalOptionsPairs.add(new Pair<>(word.getId(), cavTranslation));
            normalIdentifiers.put(word.getId(), new HashSet<>(Collections.singletonList(cavTranslation.getId())));

            // Add the rest of the normal options. Options should be all different but accept same
            // options after a number of trials in order to avoid an infinite loop. In order to check
            // translations a set of the selected translations id's will be used.
            HashSet<Long> normalOptionIds = new HashSet<>();
            normalOptionIds.add(cavTranslation.getId());
            for(int i = 0; i < QuestionQuiz.OPTIONS_NR - 1; i++){
                // Extract and add option (here option will be actually a translation).
                Pair<String, Translation> tmp = getNextDifferentGeneralTranslation(allTranslationsQueue, normalOptionIds);
                normalOptionsPairs.add(new Pair<>(tmp.first, tmp.second));
                normalOptionIds.add(tmp.second.getId());

                // Add translation id associated with word id.
                HashSet<Long> currentList = normalIdentifiers.get(tmp.first);
                if(currentList == null){
                    normalIdentifiers.put(tmp.first, new HashSet<>(Collections.singletonList(tmp.second.getId())));
                }
                else{
                    currentList.add(tmp.second.getId());
                    normalIdentifiers.put(tmp.first, currentList);
                }
            }

            // 2.1.4 Add reversed answer options.
            ArrayList<String> reversedOptions = new ArrayList<>();
            // Correct reversed value will be current word.
            reversedOptions.add(word.getValue());

            // Add the rest of the reversed options. Options should be all different but accept same
            // options after a number of trials in order to avoid an infinite loop. In order to check
            // items a set of the selected items id's will be used.
            HashSet<String> reversedOptionIds = new HashSet<>();
            reversedOptionIds.add(word.getId());
            for(int i = 0; i < QuestionQuiz.OPTIONS_NR - 1; i++){
                // Extract and add reversed option (here reversed option will be another word).
                LessonEntrance tmp = getNextDifferentGeneralItem(allWordsQueue, reversedOptionIds);
                reversedOptions.add(tmp.getValue());
                reversedOptionIds.add(tmp.getId());

                // And add word identifier id if does not exists already.
                if(!reversedIdentifiers.containsKey(tmp.getId())){
                    reversedIdentifiers.put(tmp.getId(), new HashSet<>());
                }
            }


            // 2.1.5 Shuffle normal options and save correct answers.
            // Get first option before shuffle in order to avoid index lost.
            String firstOption = normalOptionsPairs.get(0).second.getTranslation().trim().toLowerCase();
            Collections.shuffle(normalOptionsPairs, secureRandom);
            ArrayList<Integer> correctAnswers = new ArrayList<>();
            int lim = normalOptionsPairs.size();
            for(int i = 0; i < lim; i++){
                // Every correct translation which is from current word or is equal with first option
                // will be added, so if two or more options are the same user can choose any of them.
                boolean isSameValue = normalOptionsPairs.get(i).second.getTranslation().trim().toLowerCase().equals(firstOption);
                boolean isFromSameWord = word.getId().equals(normalOptionsPairs.get(i).first);
                if(isSameValue || isFromSameWord){
                    correctAnswers.add(i);
                }
            }

            // Extract normal options from pairs.
            ArrayList<String> normalOptions = new ArrayList<>();
            normalOptionsPairs.forEach(pair -> normalOptions.add(pair.second.getTranslation()));

            // 2.1.6 Shuffle reversed options and save correct reversed answers.
            // Get first reversed option before shuffle in order to avoid index lost.
            String firstReversedOption = reversedOptions.get(0).trim().toLowerCase();
            Collections.shuffle(reversedOptions, secureRandom);
            ArrayList<Integer> correctAnswersReversed = new ArrayList<>();
            lim = reversedOptions.size();
            for(int i = 0; i < lim; i++){
                // Every correct option will be added, so if two options are the same user can choose
                // any of them. Hre every correct option means every word which has same value as
                // firstReversedOption (which is current word).
                if(reversedOptions.get(i).trim().toLowerCase().equals(firstReversedOption)){
                    correctAnswersReversed.add(i);
                }
            }

            // 2.2 Create question

            // 2.2.1 Extract identifiers.
            ArrayList<QuestionIdentifier> normalIdentifiersArrayList = new ArrayList<>();
            normalIdentifiers.forEach((key,value) -> normalIdentifiersArrayList.add(new QuestionIdentifier(QuestionIdentifier.Identifiers.WORD, key, value)));

            ArrayList<QuestionIdentifier> reversedIdentifiersArrayList = new ArrayList<>();
            reversedIdentifiers.forEach((key,value) -> reversedIdentifiersArrayList.add(new QuestionIdentifier(QuestionIdentifier.Identifiers.WORD, key, value)));

            // 2.2.2 Create metadata values
            QuestionMetadata metadata = new QuestionMetadata(
                    userType,
                    normalIdentifiersArrayList,
                    reversedIdentifiersArrayList
            );

            // 2.2.3 Create current type question.
            QuestionQuiz tmp = new QuestionQuiz(
                    idx, // counter 'idx' can be used as unique id
                    Question.Types.QUESTION_QUIZ,
                    questionValue,
                    questionValueReversed,
                    metadata,
                    normalOptions,
                    reversedOptions,
                    correctAnswers,
                    correctAnswersReversed
            );

            // 2.2.4 And finally add the question at questions list.
            questions.add(tmp);
        }

        return questions;
    }


    private ArrayList<QuestionTrueOrFalse> generateQuestionsForExpressionsTrueOrFalseTest(int usertype, ArrayList<LessonEntrance> valueList,
                                                                                          int questionsNr, boolean isCustomSelection){
        if(isCustomSelection){
            // If is custom selection will be used all values from valueList because these values were
            // selected by user/guest.
            return tryToGenerateQuestionsForExpressionsTrueOrFalseTest(usertype, valueList);
        }

        // Otherwise use extracted values.
        return tryToGenerateQuestionsForExpressionsTrueOrFalseTest(usertype, extractValues(valueList, questionsNr));
    }

    private ArrayList<QuestionTrueOrFalse> tryToGenerateQuestionsForExpressionsTrueOrFalseTest(int userType, ArrayList<LessonEntrance> valueList){
        // 1. Add 2 queues:
        //      - For storing all expressions from value list.
        //      - For storing all translations from every expression.
        LinkedList<LessonEntrance> allExpressionsQueue = generateAllValuesQueue(valueList);
        LinkedList<Pair<String,Translation>> allTranslationsQueue = generateAllTranslationsQueue(valueList);

        // 2. Go to every entrance and generate question.
        ArrayList<QuestionTrueOrFalse> questions = new ArrayList<>();
        // for every test will be 'size' questions so 'idx' can be used as unique id for the question.
        int size = valueList.size();
        for(int idx = 0; idx < size; idx++){
            LessonEntrance expression = valueList.get(idx);
            LinkedList<Translation> expressionTranslationsQueue = generateItemTranslationsQueue(expression);

            // Used to save identifiers.
            ArrayList<QuestionIdentifier> normalIdentifiers = new ArrayList<>();
            ArrayList<QuestionIdentifier> reversedIdentifiers = new ArrayList<>();

            // 2.1 Set question values.

            // 2.1.1 Normal question value is actually the current expression.
            String questionValue = expression.getValue();

            // 2.1.2 Reversed question value will be a random translation of the current expression.
            Translation qvrTranslation = getNextItemTranslation(expressionTranslationsQueue);
            String questionValueReversed = qvrTranslation.getTranslation();
            reversedIdentifiers.add(
                    new QuestionIdentifier(
                            QuestionIdentifier.Identifiers.EXPRESSION,
                            expression.getId(),
                            new HashSet<>(Collections.singletonList(qvrTranslation.getId()))
                    )
            );

            // 2.1.3 Add normal option. This Question type is True or False so answer can be true or
            //       false. Use a uniform variable in order to determine from where to get option. If
            //       option will be taken from queue that are big chances to have correctResponse with
            //       False value, but if option is not from queue then are 100 % chances to have
            //       correctResponse with True value.
            boolean getFromQueue = secureRandom.nextBoolean();
            String normalOption;
            int correctAnswer;
            if(getFromQueue){
                // Extract option (here option will be actually a translation).
                Pair<String,Translation> tmp = getNextDifferentGeneralTranslation(
                        allTranslationsQueue,
                        new HashSet<>(Collections.singletonList(qvrTranslation.getId()))
                );
                normalOption = tmp.second.getTranslation();

                // Add normal identifiers and set correctAnswer value.

                // If extracted translation is for the current expression then set 1 single
                // normal identifier ans set response to True.
                if(tmp.first.equals(expression.getId())){
                    correctAnswer = QuestionTrueOrFalse.RESPONSE_TRUE;
                    normalIdentifiers.add(
                            new QuestionIdentifier(
                                    QuestionIdentifier.Identifiers.EXPRESSION,
                                    expression.getId(),
                                    new HashSet<>(Collections.singletonList(tmp.second.getId()))
                            )
                    );
                }
                else {
                    // Here means that the extracted translation is from another expression, so
                    // set response to False and 2 normal identifiers:
                    //  a. for current expression which will serve as question answer value
                    //  b. for current extracted translation which will serve as normal option
                    correctAnswer = QuestionTrueOrFalse.RESPONSE_FALSE;

                    normalIdentifiers.add(
                            new QuestionIdentifier(
                                    QuestionIdentifier.Identifiers.EXPRESSION,
                                    expression.getId(),
                                    new HashSet<>()
                            )
                    );

                    normalIdentifiers.add(
                            new QuestionIdentifier(
                                    QuestionIdentifier.Identifiers.EXPRESSION,
                                    tmp.first,
                                    new HashSet<>(Collections.singletonList(tmp.second.getId()))
                            )
                    );
                }
            }
            else {
                // Here normalOption will be a random translation of the current expression.
                Translation noTranslation = getNextItemTranslation(expressionTranslationsQueue);
                normalOption = noTranslation.getTranslation();

                // In this case response will be True.
                correctAnswer = QuestionTrueOrFalse.RESPONSE_TRUE;

                // Add normal identifier. For this question type in that case will be a single identifier
                // (current expression and associated expression).
                normalIdentifiers.add(new QuestionIdentifier(
                        QuestionIdentifier.Identifiers.EXPRESSION,
                        expression.getId(),
                        new HashSet<>(Collections.singletonList(noTranslation.getId()))
                ));
            }


            // 2.1.4 Add reversed option. This Question type is True or False so answer can be true or
            //       false. Use a uniform variable in order to determine from where to get option. If
            //       option will be taken from queue that are big chances to have correctReversedResponse
            //       with False value, but if option  is not from queue then are 100 % chances to have
            //       correctReversedResponse with True value.
            getFromQueue = secureRandom.nextBoolean();
            String reversedOption;
            int correctAnswerReversed;
            if(getFromQueue){
                // Extract and add reversed option (here reversed option will be another expression).
                LessonEntrance tmp = getNextDifferentGeneralItem(
                        allExpressionsQueue,
                        new HashSet<>(Collections.singletonList(expression.getId()))
                );
                reversedOption = tmp.getValue();

                // Add reversed identifiers and set correctAnswerReversed value.

                // If extracted expression is actually the current expression set response to True.
                // Is no need to set a reversed identifier because it was already set when reversed
                // option was generated.
                if(tmp.getId().equals(expression.getId())){
                    correctAnswerReversed = QuestionTrueOrFalse.RESPONSE_TRUE;
                }
                else{
                    // Here means that the extracted expression is NOT current expression so
                    // translation from the reversed question value is not for the extracted expression.
                    // Set response to False and set 1 reversed identifier for extracted expression.
                    // For current expression (not extracted one) reversed identifier was already
                    // set when reversed option was generated.
                    correctAnswerReversed = QuestionTrueOrFalse.RESPONSE_FALSE;

                    reversedIdentifiers.add(new QuestionIdentifier(
                            QuestionIdentifier.Identifiers.EXPRESSION,
                            tmp.getId(),
                            new HashSet<>()
                    ));
                }
            }
            else {
                // Here reversedOption will be actually the current expression. Set response to True,
                // and is no need to add a reversed identifier because it was already set when reversed
                // option was generated.
                reversedOption = expression.getValue();
                correctAnswerReversed = QuestionTrueOrFalse.RESPONSE_TRUE;
            }

            // 2.2 Create question

            // 2.2.1 Create metadata values.
            QuestionMetadata metadata = new QuestionMetadata(
                    userType,
                    normalIdentifiers,
                    reversedIdentifiers
            );

            // 2.2.2  Create current type question.
            QuestionTrueOrFalse tmp = new QuestionTrueOrFalse(
                    idx, // counter 'idx' can be used as unique id
                    Question.Types.QUESTION_TRUE_OR_FALSE,
                    questionValue,
                    questionValueReversed,
                    metadata,
                    normalOption,
                    reversedOption,
                    correctAnswer,
                    correctAnswerReversed
            );

            // 2.2.3 And finally add the question at questions list.
            questions.add(tmp);
        }

        return questions;
    }


    private ArrayList<QuestionMixed> generateQuestionsForWordsMixedLettersTest(int usertype, ArrayList<LessonEntrance> valueList,
                                                                               int questionsNr, boolean isCustomSelection){
        return generateQuestionsForMixedTest(true, usertype, valueList, questionsNr, isCustomSelection);
    }

    private ArrayList<QuestionMixed> generateQuestionsForExpressionsMixedWordsTest(int usertype, ArrayList<LessonEntrance> valueList,
                                                                                   int questionsNr, boolean isCustomSelection){
        return generateQuestionsForMixedTest(false, usertype, valueList, questionsNr, isCustomSelection);
    }

    private ArrayList<QuestionMixed> generateQuestionsForMixedTest(boolean isWordTest, int usertype, ArrayList<LessonEntrance> valueList,
                                                                   int questionsNr, boolean isCustomSelection){
        if(isCustomSelection){
            // If is custom selection will be used all values from valueList because these values were
            // selected by user/guest.
            return tryToGenerateQuestionsForMixedTest(isWordTest, usertype, valueList);
        }

        // Otherwise use extracted values.
        return tryToGenerateQuestionsForMixedTest(isWordTest, usertype, extractValues(valueList, questionsNr));
    }

    private ArrayList<QuestionMixed> tryToGenerateQuestionsForMixedTest(boolean isWordTest, int userType, ArrayList<LessonEntrance> valueList){
        // Go to every entrance and generate question.
        ArrayList<QuestionMixed> questions = new ArrayList<>();
        // for every test will be 'size' questions so 'idx' can be used as unique id for the question.
        int size = valueList.size();
        for(int idx = 0; idx < size; idx++){
            LessonEntrance item = valueList.get(idx);
            // 1. Set question values.

            // 1.1 Remove all two or more adjacent spaces/new lines and split item value in words.
            String[] values = CoreUtilities.General.removeAdjacentSpacesAndNewLines(item.getValue()).split(" ");
            ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(values));

            // 1.2 Create question value and correct answer order.
            ArrayList<String> correctAnswerOrder = new ArrayList<>();
            if(isWordTest){
                // For words test, questionValue will be a random word from the split and should be
                // an words with more than 1 letter.
                List<String> tmp = wordsList.stream()
                        .filter(s -> s != null && s.length() > 1)
                        .collect(Collectors.toList());

                // If no word with more that '1' letter exist ignore current item, because test has
                // no sens with one letter.
                if(tmp.isEmpty()){
                    continue;
                }

                // Here words with more than '1' letter existed so extract a random word.
                String randomWord = tmp.get(generateRandomIndex(tmp.size()));

                // Correct answer order will be randomWord split in letters.
                for(Character c : randomWord.toCharArray()){
                    correctAnswerOrder.add(String.valueOf(c));
                }
            }
            else {
                // For expressions test, questionValue will be a number of adjacent words from the split.
                // This number will be MAX_WORDS_FOR_MIXING if are more than MAX_WORDS_FOR_MIXING words in
                // the expression split or a number in interval [MIN_WORDS_FOR_MIXING, MAX_WORDS_FOR_MIXING]
                // if words number is <= MAX_WORDS_FOR_MIXING.

                // If are not enough words in the expression ignore item.
                if(wordsList.size() < QuestionMixed.MIN_WORDS_FOR_MIXING){
                    continue;
                }

                // If in the expression are more words that we need, choose only 'MAX_WORDS_FOR_MIXING'
                // adjacent words. Otherwise all words will be extracted.
                if(wordsList.size() > QuestionMixed.MAX_WORDS_FOR_MIXING){
                    // startIdx will be in interval [0, wordsList.size() - QuestionMixed.MAX_WORDS_FOR_MIXING)
                    int startIdx = secureRandom.nextInt(wordsList.size() - QuestionMixed.MAX_WORDS_FOR_MIXING);
                    // Extract sublist: [startIdx, startIdx + QuestionMixed.MAX_WORDS_FOR_MIXING)
                    wordsList = new ArrayList<>(wordsList.subList(startIdx, startIdx + QuestionMixed.MAX_WORDS_FOR_MIXING));
                }

                // Construct correct answer order which will be actually list of extracted words.
                correctAnswerOrder.addAll(wordsList);
            }

            // 1.3 Create start order:
            //     For words test, start order will be correctAnswerOrder with letters shuffled.
            //     For expressions test, start order will be correctAnswerOrder with words shuffled.

            // Start order should be different than correct answer oder.
            ArrayList<String> startOrder = new ArrayList<>(correctAnswerOrder);
            int currentTry = 0;
            int nrOfTrials = 2 * startOrder.size();
            boolean isSameOrder = true;
            while (true){
                if(currentTry > nrOfTrials){
                    break;
                }
                currentTry++;

                Collections.shuffle(startOrder, secureRandom);
                if(!startOrder.equals(correctAnswerOrder)){
                    isSameOrder = false;
                    break;
                }
            }

            // If startOrder is same as correctOrder is no sense for question so ignore it.
            if(isSameOrder){
                continue;
            }

            // 1.4 Create question value:
            //     For words test, questionValue will contain all letters from startOrder.
            //     For expressions test, questionValue will contain all words from startOrder.
            StringBuilder stringBuilder = new StringBuilder();
            for(String element : startOrder){
                stringBuilder.append(element).append(" ");
            }
            // Get value without last space.
            String questionValue = stringBuilder.substring(0, stringBuilder.length() - 1);


            // 2. Create question

            // 2.1 Extract identifiers.
            ArrayList<QuestionIdentifier> identifiersArrayList = new ArrayList<>();
            if(isWordTest){
                identifiersArrayList.add(new QuestionIdentifier(QuestionIdentifier.Identifiers.WORD, item.getId(), new HashSet<>()));
            }
            else{
                identifiersArrayList.add(new QuestionIdentifier(QuestionIdentifier.Identifiers.EXPRESSION, item.getId(), new HashSet<>()));
            }

            // 2.2 Create metadata values.
            QuestionMetadata metadata = new QuestionMetadata(
                    userType,
                    // At this question type ere normal identifiers are same as reversed identifiers.
                    identifiersArrayList,
                    identifiersArrayList
            );

            // 2.3 Create current type question.
            QuestionMixed tmp = new QuestionMixed(
                    idx, // counter 'idx' can be used as unique id
                    Question.Types.QUESTION_MIXED,
                    questionValue,
                    metadata,
                    startOrder,
                    correctAnswerOrder
            );

            // 2.4 And finally add the question at questions list.
            questions.add(tmp);
        }

        return questions;
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
            callback.onFailure(errorGeneralCanNotGenerateTest);
            return;
        }

        ThreadExecutorService.getInstance().execute(() -> tryToCreateTestFromScheduledTest(scheduledTest, forUser, callback));
    }

    private void tryToCreateTestFromScheduledTest(Test test, boolean isForUser, TestService.TestGenerationCallback callback){
        if(!test.isScheduled()){
            callback.onFailure(errorGeneralCanNotGenerateTest);
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
                                // TODO: check exception and show specific error messages.
                                callback.onFailure(errorGeneralCanNotGenerateTest);
                                return;
                            }

                            UserDocument userDocument = task.getResult().toObject(UserDocument.class);
                            if(userDocument == null){
                                Timber.w("userDocument is null");
                                callback.onFailure(errorGeneralCanNotGenerateTest);
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
     * This will be used to manage onComplete actions when a new test is generated or not.
     * */
    public interface TestGenerationCallback {
        void onSuccess(@NotNull @NonNull String testId);
        void onFailure(@NotNull @NonNull String error);
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
