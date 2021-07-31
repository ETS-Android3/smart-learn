package com.smart_learn.data.entities;

import com.smart_learn.R;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import timber.log.Timber;

@Getter
@Setter
@ToString
public abstract class Test {

    public interface Types {
        int NO_TYPE = 0;
        int WORD_WRITE = 1;
        int WORD_QUIZ = 2;
        int WORD_MIXED_LETTERS = 3;
        int EXPRESSION_TRUE_OR_FALSE = 4;
        int EXPRESSION_MIXED_WORDS = 5;
    }

    // if you change nr of days check indexes also
    public static final int NR_OF_WEEK_DAYS = 7;
    private interface DaysIndexes {
        int MONDAY = 0;
        int TUESDAY = 1;
        int WEDNESDAY = 2;
        int THURSDAY = 3;
        int FRIDAY = 4;
        int SATURDAY = 5;
        int SUNDAY = 6;
    }

    // 60 sec/min * 60 min/h * 24h
    public static final long MAX_TEST_TIME_SECONDS = 60 * 60 * 24;
    public static final int NO_DATE_TIME = -1;
    public static final int USE_ALL = -1;
    public static final int NO_COUNTER = -1;
    public static final int NO_INTEGER_TEST_ID = -1;

    public static final int MIN_CUSTOM_SELECTED_VALUES = 1;

    // seconds
    public static final int MIN_QUESTION_COUNTER_TIME = 5;
    public static final int MAX_QUESTION_COUNTER_TIME = 60;

    private int type;
    // this will be generated name
    @NonNull
    @NotNull
    private String testName;
    // this wil be name given by user if this want to give a name
    @NonNull
    @NotNull
    private String customTestName;
    protected long testGenerationDate;

    // is hidden is used to hide test from user is this chose this option
    private boolean isHidden;

    private boolean isFinished;
    private boolean isGenerated;

    // questions will be stored as JSON for compatibility between Firestore and Room
    @NonNull
    @NotNull
    private String questionsJson;
    private int totalQuestions;
    // marks the current progress
    private int answeredQuestions;
    // at how many questions user answer was correct
    private int correctAnswers;

    // test time length in milliseconds
    private long testTotalTime;

    // used if user want to select specific values for testing
    private boolean useCustomSelection;
    // used for generating a specific number of values
    private int nrOfValuesForGenerating;
    // how much time has user for answering at one question
    private int questionCounter;

    // mark that a test is scheduled.
    private boolean isScheduled;
    // mark that test scheduled test is active (should be followed in background in order to give
    // notification  when time is up)
    private boolean isScheduleActive;
    // at what hour and what minute is test scheduled
    private int hour;
    private int minute;

    // mark that test is scheduled but is not recurrent and will be launched only once at specified
    // date and specified hour
    private boolean oneTime;
    // this dates will be used if test si not recurrent
    private int dayOfMonth;
    private int month;
    private int year;

    // If test is recurrent mark in what days at specified hour, test should start. This will be
    // actioned by type converter in Room, while Firestore can handle Array of Boolean values.
    @NonNull
    @NotNull
    protected ArrayList<Boolean> daysStatus;

    // test will be linked with a lesson
    @NonNull
    @NotNull
    private String lessonId;
    @NonNull
    @NotNull
    private String lessonName;

    public Test(){
        // initial values
        type = Types.NO_TYPE;
        nrOfValuesForGenerating = USE_ALL;
        questionCounter = NO_COUNTER;
        daysStatus = new ArrayList<>(Collections.nCopies(NR_OF_WEEK_DAYS, false));
        dayOfMonth = NO_DATE_TIME;
        month = NO_DATE_TIME;
        year = NO_DATE_TIME;
        // avoid null values
        testName = "";
        customTestName = "";
        questionsJson = "";
        lessonId = "";
        lessonName = "";
    }

    public void setTestName(String name) {
        this.testName = name == null ? "" : name;
    }

    public void setCustomTestName(String name) {
        this.customTestName = name == null ? "" : name;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson == null ? "" : questionsJson;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId == null ? "" : lessonId;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName == null ? "" : lessonName;
    }

    public void setDaysStatus(ArrayList<Boolean> daysStatus) {
        if(daysStatus == null || daysStatus.size() != NR_OF_WEEK_DAYS){
            daysStatus = new ArrayList<>(Collections.nCopies(NR_OF_WEEK_DAYS, false));
        }
        this.daysStatus = daysStatus;
    }

    public void setDate(int day, int month, int year){
        this.dayOfMonth = day;
        this.month = month;
        this.year = year;
    }

    public void setQuestionCounter(int questionCounter) {
        if(questionCounter != Test.NO_COUNTER && (questionCounter < MIN_QUESTION_COUNTER_TIME || questionCounter > MAX_QUESTION_COUNTER_TIME)){
            questionCounter = Test.NO_COUNTER;
        }
        this.questionCounter = questionCounter;
    }

    public void setNrOfValuesForGenerating(int nrOfValuesForGenerating) {
        if(nrOfValuesForGenerating != USE_ALL && nrOfValuesForGenerating < 1){
            nrOfValuesForGenerating = USE_ALL;
        }
        this.nrOfValuesForGenerating = nrOfValuesForGenerating;
    }

    public static int getTestIdInteger(String testId){
        int testIdInteger;
        try{
            testIdInteger = Integer.parseInt(testId);
        } catch (NumberFormatException ex){
            Timber.w(ex);
            return NO_INTEGER_TEST_ID;
        }
        return testIdInteger;
    }

    public static String getTestTypeDescription(int type){
        switch (type){
            case Test.Types.WORD_WRITE:
                return ApplicationController.getInstance().getString(R.string.word_full_write);
            case Test.Types.WORD_QUIZ:
                return ApplicationController.getInstance().getString(R.string.word_quiz);
            case Test.Types.WORD_MIXED_LETTERS:
                return ApplicationController.getInstance().getString(R.string.word_mixed_letters);
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                return ApplicationController.getInstance().getString(R.string.expression_true_or_false);
            case Test.Types.EXPRESSION_MIXED_WORDS:
                return ApplicationController.getInstance().getString(R.string.expression_mixed_words);
        }
        return "";
    }

    public String getTotalTimeDescription(){
        long hours = TimeUnit.MILLISECONDS.toHours(testTotalTime);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(testTotalTime);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(testTotalTime);

        if(hours > 0){
            return hours + ":" + minutes + ":" + seconds + " (h:min:sec)";
        }
        if(minutes > 0 ){
            return minutes + ":" + seconds + " (min:sec)";
        }
        return seconds + " sec";
    }

    private boolean allRepeatsAreSet(){
        for(Boolean value : daysStatus){
            if(!value){
                return false;
            }
        }
        return true;
    }

    private boolean anyRepeatIsSet(){
        for(Boolean value : daysStatus){
            if(value){
                return true;
            }
        }
        return false;
    }

    public String getRepeatValuesDescription() {
        if(allRepeatsAreSet()){
            return ApplicationController.getInstance().getString(R.string.every_day);
        }

        if(!anyRepeatIsSet()){
            return "";
        }

        if(daysStatus.size() < NR_OF_WEEK_DAYS){
            return "";
        }

        String value = "";
        if(daysStatus.get(DaysIndexes.MONDAY)){
            value += ApplicationController.getInstance().getString(R.string.monday) + ", ";
        }
        if(daysStatus.get(DaysIndexes.TUESDAY)){
            value += ApplicationController.getInstance().getString(R.string.tuesday) + ", ";
        }
        if(daysStatus.get(DaysIndexes.WEDNESDAY)){
            value += ApplicationController.getInstance().getString(R.string.wednesday) + ", ";
        }
        if(daysStatus.get(DaysIndexes.THURSDAY)){
            value += ApplicationController.getInstance().getString(R.string.thursday) + ", ";
        }
        if(daysStatus.get(DaysIndexes.FRIDAY)){
            value += ApplicationController.getInstance().getString(R.string.friday) + ", ";
        }
        if(daysStatus.get(DaysIndexes.SATURDAY)){
            value += ApplicationController.getInstance().getString(R.string.saturday) + ", ";
        }
        if(daysStatus.get(DaysIndexes.SUNDAY)){
            value += ApplicationController.getInstance().getString(R.string.sunday) + ", ";
        }

        // remove last space and last comma
        value = value.trim();
        value = value.substring(0, value.length() - 1);
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;

        Test test = (Test) o;

        if (getType() != test.getType()) return false;
        if (getTestGenerationDate() != test.getTestGenerationDate()) return false;
        if (isHidden() != test.isHidden()) return false;
        if (isFinished() != test.isFinished()) return false;
        if (isGenerated() != test.isGenerated()) return false;
        if (getTotalQuestions() != test.getTotalQuestions()) return false;
        if (getAnsweredQuestions() != test.getAnsweredQuestions()) return false;
        if (getCorrectAnswers() != test.getCorrectAnswers()) return false;
        if (getTestTotalTime() != test.getTestTotalTime()) return false;
        if (isUseCustomSelection() != test.isUseCustomSelection()) return false;
        if (getNrOfValuesForGenerating() != test.getNrOfValuesForGenerating()) return false;
        if (getQuestionCounter() != test.getQuestionCounter()) return false;
        if (isScheduled() != test.isScheduled()) return false;
        if (isScheduleActive() != test.isScheduleActive()) return false;
        if (getHour() != test.getHour()) return false;
        if (getMinute() != test.getMinute()) return false;
        if (isOneTime() != test.isOneTime()) return false;
        if (getDayOfMonth() != test.getDayOfMonth()) return false;
        if (getMonth() != test.getMonth()) return false;
        if (getYear() != test.getYear()) return false;
        if (!getTestName().equals(test.getTestName())) return false;
        if (!getCustomTestName().equals(test.getCustomTestName())) return false;
        if (!getQuestionsJson().equals(test.getQuestionsJson())) return false;
        if (!getDaysStatus().equals(test.getDaysStatus())) return false;
        if (!getLessonId().equals(test.getLessonId())) return false;
        return getLessonName().equals(test.getLessonName());
    }

    @Override
    public int hashCode() {
        int result = getType();
        result = 31 * result + getTestName().hashCode();
        result = 31 * result + getCustomTestName().hashCode();
        result = 31 * result + (int) (getTestGenerationDate() ^ (getTestGenerationDate() >>> 32));
        result = 31 * result + (isHidden() ? 1 : 0);
        result = 31 * result + (isFinished() ? 1 : 0);
        result = 31 * result + (isGenerated() ? 1 : 0);
        result = 31 * result + getQuestionsJson().hashCode();
        result = 31 * result + getTotalQuestions();
        result = 31 * result + getAnsweredQuestions();
        result = 31 * result + getCorrectAnswers();
        result = 31 * result + (int) (getTestTotalTime() ^ (getTestTotalTime() >>> 32));
        result = 31 * result + (isUseCustomSelection() ? 1 : 0);
        result = 31 * result + getNrOfValuesForGenerating();
        result = 31 * result + getQuestionCounter();
        result = 31 * result + (isScheduled() ? 1 : 0);
        result = 31 * result + (isScheduleActive() ? 1 : 0);
        result = 31 * result + getHour();
        result = 31 * result + getMinute();
        result = 31 * result + (isOneTime() ? 1 : 0);
        result = 31 * result + getDayOfMonth();
        result = 31 * result + getMonth();
        result = 31 * result + getYear();
        result = 31 * result + getDaysStatus().hashCode();
        result = 31 * result + getLessonId().hashCode();
        result = 31 * result + getLessonName().hashCode();
        return result;
    }

}
