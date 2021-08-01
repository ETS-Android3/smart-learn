package com.smart_learn.core.services.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.smart_learn.data.entities.LessonEntrance;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionFullWrite;
import com.smart_learn.data.entities.QuestionMixed;
import com.smart_learn.data.entities.QuestionQuiz;
import com.smart_learn.data.entities.QuestionTrueOrFalse;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    public CollectionReference getLocalTestsCollection(){
        return userTestServiceInstance.getLocalTestsCollection();
    }

    public CollectionReference getOnlineTestsCollection(){
        return userTestServiceInstance.getOnlineTestsCollection();
    }

    public void markAsHidden(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.markAsHidden(testSnapshot, callback);
    }

    public void setSchedule(DocumentSnapshot testSnapshot, boolean isScheduleActive, DataCallbacks.General callback){
        userTestServiceInstance.setSchedule(testSnapshot, isScheduleActive, callback);
    }

    public void addLocalTest(TestDocument testDocument, DataCallbacks.General callback){
        userTestServiceInstance.addLocalTest(testDocument, callback);
    }

    public void updateLocalTest(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.updateLocalTest(testSnapshot, callback);
    }

    public void deleteScheduledTest(DocumentSnapshot testSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.deleteScheduledTest(testSnapshot, callback);
    }

    public void updateDocument(Map<String,Object> updatedInfo, DocumentSnapshot documentSnapshot, DataCallbacks.General callback){
        userTestServiceInstance.updateDocument(updatedInfo, documentSnapshot, callback);
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
                .getQueryForAllLessonWords(testOptions.getLessonId())
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
                .getQueryForAllLessonExpressions(testOptions.getLessonId())
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
        UserService.getInstance()
                .getUserDocumentReference()
                // get data only from server in order to be fresh data
                .get(Source.SERVER)
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

                        continueWithLocalTestGeneration(true, valueList, testOptions, questionsNr, callback);
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

        continueWithLocalTestGeneration(false, valueList, testOptions, questionsNr, callback);
    }







    /**
     * This method is used also for user and guest local test generation.
     *
     * @param isForUser Must be true if is a test for user, or false otherwise.
     * @param testOptions Options for new test.
     * @param questionsNr How many questions should test have.
     * @param callback Callback to manage onComplete action.
     * */
    private void continueWithLocalTestGeneration(boolean isForUser, ArrayList<LessonEntrance> valueList, Test testOptions,
                                                 int questionsNr, TestService.TestGenerationCallback callback){
        // For tests which are NOT scheduled questions must be generated always base on the value
        // list and questionsNr.
        //
        // For tests which are scheduled, questions must be generated only if is custom selection.
        // If not then generation will have place at specific test time.

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
        if(!newTest.isOnline()){
            final DocumentReference newTestDocumentRef = userTestServiceInstance.getLocalTestsCollection().document();
            userTestServiceInstance.addLocalTest(newTest, newTestDocumentRef, new DataCallbacks.General() {
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

    public interface TestGenerationCallback {
        void onComplete(@NotNull @NonNull String testId);
    }
}
