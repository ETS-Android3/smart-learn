package com.smart_learn.presenter.common.fragments.test.test_types;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.common.entities.question.Question;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

@Getter
@Setter
public abstract class BasicTestTypeViewModel extends BasicAndroidViewModel {

    // used to avoid counters to run in background
    private AtomicBoolean fragmentIsActive;

    // testId will be used to retrieve test from db
    private String testId;
    // this will be test retrieved from db from which questions will be extracted
    private Test extractedTest;
    // mark that retrieved test is online or not
    private boolean isOnline;
    // here will be stored all test questions for a faster access and update
    private ArrayList<Question> allTestQuestions;
    // here will be stored questions for a faster access
    private LinkedList<Question> remainingQuestions;

    @Nullable
    private AtomicLong totalTestTime;
    private CountDownTimer totalTestTimeCounter;
    private CountDownTimer currentQuestionTimeCounter;
    // for round time (used for question statistics)
    private AtomicLong roundTimeMilliseconds;

    // current questions shown on view
    private Question currentQuestion;

    // for loading data in views
    private final MutableLiveData<Boolean> liveShowQuestionCounter;
    private final MutableLiveData<String> liveTotalTestTime;
    private final MutableLiveData<String> liveProgress;
    private final MutableLiveData<String> liveCounter;
    private final MutableLiveData<String> liveQuestionDescription;
    private final MutableLiveData<String> liveQuestion;
    private final MutableLiveData<Boolean> liveIsReverseChecked;


    public BasicTestTypeViewModel(@NonNull @NotNull Application application) {
        super(application);
        fragmentIsActive = new AtomicBoolean(false);
        testId = "";
        extractedTest = null;
        allTestQuestions = new ArrayList<>();
        remainingQuestions = new LinkedList<>();
        roundTimeMilliseconds = new AtomicLong(0);

        liveShowQuestionCounter = new MutableLiveData<>(false);
        liveTotalTestTime = new MutableLiveData<>("");
        liveProgress = new MutableLiveData<>("");
        liveCounter = new MutableLiveData<>("");
        liveQuestion = new MutableLiveData<>("");
        liveQuestionDescription = new MutableLiveData<>("");
        liveIsReverseChecked = new MutableLiveData<>(false);

        currentQuestion = null;
    }

    protected abstract void extractTest(@NonNull @NotNull BasicTestTypeFragment<?> fragment);
    protected abstract void updateTest(@NonNull @NotNull Test test, @NonNull @NotNull DataCallbacks.General callback);
    protected abstract Question getProcessedQuestion(@NonNull @NotNull Question question, boolean isReversed);
    protected abstract ArrayList<Question> getAllTestQuestions(@NotNull @NonNull Test test);
    protected abstract String getQuestionsJson(@NotNull @NonNull ArrayList<Question> questions);
    protected abstract void showCustomValues(@NonNull @NotNull Question currentQuestion, boolean isReversed);

    protected void setFragmentIsActive(boolean isActive){
        fragmentIsActive.set(isActive);
        // avoid counters on background (and test progress) if fragment is not active
        if(!isActive){
            stopTestTotalTimeCounter();
            stopCurrentQuestionTimeCounter();
        }
    }

    protected void setExtractedTest(@NonNull @NotNull BasicTestTypeFragment<?> fragment, Test test, boolean isOnlineTest){
        if(test == null){
            Timber.w("test is null");
            fragment.goBack();
            return;
        }

        isOnline = isOnlineTest;
        extractedTest = test;
        if(extractedTest.getTestTotalTime() >= Test.MAX_TEST_TIME_SECONDS){
            liveToastMessage.setValue(fragment.getString(R.string.test_time_finished));
            fragment.goBack();
            return;
        }

        if(extractedTest.isFinished()){
            fragment.goToTestFinalizeFragment(testId, extractedTest.getType(), extractedTest.getCorrectAnswers(), extractedTest.getTotalQuestions());
            return;
        }

        // here test is not finished, so extract unanswered question
        allTestQuestions = getAllTestQuestions(extractedTest);
        if(allTestQuestions == null){
            Timber.w("allTestQuestions is null");
            fragment.goBack();
            return;
        }

        int correctAnswers = 0;
        for(Question question : allTestQuestions){
            if(!question.isAnswered()){
                remainingQuestions.add(question);
                continue;
            }
            if(question.isAnswerCorrect()){
                correctAnswers++;
            }
        }

        // Here update test question counter just to fix a previous error on counter (if existed)
        // because on this fragment real time sync is not applied.
        boolean isModified = false;
        if(extractedTest.getTotalQuestions() != allTestQuestions.size()){
            extractedTest.setTotalQuestions(allTestQuestions.size());
            isModified = true;
        }
        if(extractedTest.getAnsweredQuestions() != (extractedTest.getTotalQuestions() - remainingQuestions.size())){
            extractedTest.setAnsweredQuestions(extractedTest.getTotalQuestions() - remainingQuestions.size());
            isModified = true;
        }
        if(extractedTest.getCorrectAnswers() != correctAnswers){
            extractedTest.setCorrectAnswers(correctAnswers);
            isModified = true;
        }

        if(isModified){
            updateTest(extractedTest, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    fragment.requireActivity().runOnUiThread(()-> continueWithTestExtraction(fragment));
                }
                @Override
                public void onFailure() {
                    fragment.requireActivity().runOnUiThread(fragment::goBack);
                }
            });

            return;
        }

        // no update was needed so continue with remaining steps
        continueWithTestExtraction(fragment);
    }

    private void continueWithTestExtraction(@NonNull @NotNull BasicTestTypeFragment<?> fragment){
        // if no remaining questions existed there is no need to continue with test
        if(remainingQuestions.isEmpty()){
            Timber.w("no remaining questions found");
            fragment.goBack();
            return;
        }

        // counter view must be set only once
        liveShowQuestionCounter.setValue(extractedTest.getQuestionCounter() != Test.NO_COUNTER);

        prepareAndShowNextQuestion(fragment, true);
    }

    public void showNextQuestion(@NonNull @NotNull BasicTestTypeFragment<?> fragment){
        prepareAndShowNextQuestion(fragment, false);
    }

    private void prepareAndShowNextQuestion(@NonNull @NotNull BasicTestTypeFragment<?> fragment, boolean isInitialQuestion){
        // First process previous response (questionResponse is related to currentQuestion which at
        // this call wil be actually previous question). If is initial question, then no previous
        // response will exists.
        if(!isInitialQuestion){
            processCurrentQuestionResponse(fragment, new Callback() {
                @Override
                public void onSuccessFinishProcessing() {
                    continueWithPrepareAndShowNextQuestion(fragment);
                }
                @Override
                public void onFailureFinishProcessing() {
                    fragment.goBack();
                }
            });

            return;
        }

        // start test counter because here is initial setup
        startTestTotalTimeCounter(fragment);
        // and prepare first question
        continueWithPrepareAndShowNextQuestion(fragment);
    }

    private void continueWithPrepareAndShowNextQuestion(@NonNull @NotNull BasicTestTypeFragment<?> fragment){
        // then prepare next question if any
        if(remainingQuestions.isEmpty()){
            fragment.goToTestFinalizeFragment(testId, extractedTest.getType(), extractedTest.getCorrectAnswers(), extractedTest.getTotalQuestions());
            return;
        }

        currentQuestion = remainingQuestions.poll();
        if(currentQuestion == null){
            Timber.w("question can not be extracted");
            fragment.goBack();
            return;
        }

        // set view values
        liveQuestionDescription.setValue(Question.getQuestionDescription(currentQuestion.getType()));
        liveProgress.setValue(getProgressDescription());

        final boolean isReverseChecked = liveIsReverseChecked.getValue() != null && liveIsReverseChecked.getValue();
        prepareAndShowQuestion(isReverseChecked);
        // if is a counter time test then prepare counter
        if(extractedTest.getQuestionCounter() != Test.NO_COUNTER){
            startCurrentQuestionTimeCounter(fragment);
        }

        // reset round time counter
        roundTimeMilliseconds.set(0);
    }

    protected void prepareAndShowQuestion(boolean isReverseChecked){
        // currentQuestion can be null if is not set or switch trigger is called before loading current
        // question
        if(currentQuestion == null){
            return;
        }
        if(isReverseChecked){
            currentQuestion.setReversed(true);
            liveQuestion.setValue(currentQuestion.getQuestionValueReversed());

        }
        else{
            currentQuestion.setReversed(false);
            liveQuestion.setValue(currentQuestion.getQuestionValue());
        }
        // this will make custom settings for different types of test types
        showCustomValues(currentQuestion, isReverseChecked);
    }

    private String getProgressDescription(){
        if(extractedTest == null){
            return "";
        }
        return extractedTest.getAnsweredQuestions() +  1 + "/" + extractedTest.getTotalQuestions();
    }

    private String getTotalTestTimeDescription(long currentTime){
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime);
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds - minutes * 60;
        minutes = minutes - hours * 60;
        String hourString = hours < 10 ? "0" + hours : String.valueOf(hours);
        String minutesString = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String secondsString = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        if(hours < 1){
            return minutesString + ":" + secondsString;
        }
        return hourString + ":" + minutesString + ":" + secondsString + " (h:m:s)";

    }

    private void startTestTotalTimeCounter(@NonNull @NotNull BasicTestTypeFragment<?> fragment){
        // be sure that counter is stopped
        stopTestTotalTimeCounter();

        // and fragment is active
        if(!fragmentIsActive.get()){
            return;
        }

        totalTestTime = new AtomicLong(extractedTest.getTestTotalTime());

        // step is 1 second (1000 mls)
        final long step = 1000;
        totalTestTimeCounter = new CountDownTimer(Test.MAX_TEST_TIME_SECONDS * 1000, step){
            @Override
            public void onTick(long millisUntilFinished) {
                if(!fragmentIsActive.get()){
                    this.cancel();
                    return;
                }
                liveTotalTestTime.postValue(getTotalTestTimeDescription(totalTestTime.addAndGet(step)));
                // update round time also
                roundTimeMilliseconds.getAndAdd(step);
            }

            @Override
            public void onFinish() {
                if(!fragmentIsActive.get()){
                    return;
                }
                liveToastMessage.postValue(fragment.getString(R.string.test_time_finished));
                fragment.requireActivity().runOnUiThread(fragment::goBack);

            }
        }.start();
    }

    private void stopTestTotalTimeCounter(){
        if(totalTestTimeCounter != null){
            totalTestTimeCounter.cancel();
            totalTestTimeCounter = null;
        }
    }

    private void startCurrentQuestionTimeCounter(@NonNull @NotNull BasicTestTypeFragment<?> fragment){
        // be sure that counter is stopped
        stopCurrentQuestionTimeCounter();

        // and fragment is active
        if(!fragmentIsActive.get()){
            return;
        }

        currentQuestionTimeCounter = new CountDownTimer(extractedTest.getQuestionCounter() * 1000L, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if(!fragmentIsActive.get()){
                    this.cancel();
                    return;
                }
                liveCounter.postValue(getTotalTestTimeDescription(millisUntilFinished));
            }
            @Override
            public void onFinish() {
                if(!fragmentIsActive.get()){
                    return;
                }
                fragment.requireActivity().runOnUiThread(() -> showNextQuestion(fragment));
            }
        }.start();
    }

    private void stopCurrentQuestionTimeCounter(){
        if(currentQuestionTimeCounter != null){
            currentQuestionTimeCounter.cancel();
            currentQuestionTimeCounter = null;
        }
    }

    private void processCurrentQuestionResponse(@NonNull @NotNull BasicTestTypeFragment<?> fragment, @NonNull @NotNull BasicTestTypeViewModel.Callback callback){
        // Set round time on question. Round time will be reset when next question will be extracted,
        // so is no need to reset here.
        currentQuestion.setAnswerTimeInMilliseconds(roundTimeMilliseconds.get());

        // extract processed question (will be checked if answer is correct and statistics will be
        // updated)
        Question processedQuestion = getProcessedQuestion(currentQuestion, currentQuestion.isReversed());

        // update question in totalQuestionsArray
        boolean isQuestionUpdated = false;
        int lim = allTestQuestions.size();
        for(int i = 0; i < lim; i++){
            // every question should have an unique id
            if(processedQuestion.getId() == allTestQuestions.get(i).getId()){
                allTestQuestions.set(i, processedQuestion);
                isQuestionUpdated = true;
                continue;
            }

            // if question is updated and other question has same id, means that an error occurred
            if(isQuestionUpdated && processedQuestion.getId() == allTestQuestions.get(i).getId()){
                Timber.w("Question id [" + processedQuestion.getId() + "] with value [" + processedQuestion.toString() +
                        "] has multiple apparitions in array [" + allTestQuestions.toString() + "]");
                callback.onFailureFinishProcessing();
                return;
            }
        }

        if(!isQuestionUpdated){
            Timber.w("Question [" + processedQuestion.toString()+ "] could not be found in array [" + allTestQuestions.toString() + "]");
            callback.onFailureFinishProcessing();
            return;
        }

        // save questions
        extractedTest.setQuestionsJson(getQuestionsJson(allTestQuestions));

        // update counters
        extractedTest.setAnsweredQuestions(extractedTest.getAnsweredQuestions() + 1);
        if(processedQuestion.isAnswerCorrect()){
            extractedTest.setCorrectAnswers(extractedTest.getCorrectAnswers() + 1);
        }
        if(totalTestTime != null){
            extractedTest.setTestTotalTime(totalTestTime.get());
        }

        // if no question remains then test is finished
        if(remainingQuestions.isEmpty()){
            // stop counters
            stopTestTotalTimeCounter();
            stopCurrentQuestionTimeCounter();
            // an mark test as finished
            extractedTest.setFinished(true);
        }

        // update success rate
        if(extractedTest.getTotalQuestions() != 0){
            extractedTest.setSuccessRate(((float)extractedTest.getCorrectAnswers() / (float)extractedTest.getTotalQuestions()) * 100);
        }

        // finally update test and save progress
        updateTest(extractedTest, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                // here progress is saved

                // If test is online or test is from a shared lesson, is no need to update statistics
                // so go directly to the next question.
                if(isOnline || extractedTest.isSharedLesson()){
                    fragment.requireActivity().runOnUiThread(callback::onSuccessFinishProcessing);
                    return;
                }

                // Here is local test so update statistics.
                TestService.getInstance().updateStatistics(processedQuestion, new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        fragment.requireActivity().runOnUiThread(callback::onSuccessFinishProcessing);
                    }

                    @Override
                    public void onFailure() {
                        liveToastMessage.postValue(fragment.getString(R.string.error_progress_saved));
                        fragment.requireActivity().runOnUiThread(callback::onFailureFinishProcessing);
                    }
                });
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(fragment.getString(R.string.error_progress_saved));
                fragment.requireActivity().runOnUiThread(callback::onFailureFinishProcessing);
            }
        });
    }

    private interface Callback {
        void onSuccessFinishProcessing();
        void onFailureFinishProcessing();
    }
}
