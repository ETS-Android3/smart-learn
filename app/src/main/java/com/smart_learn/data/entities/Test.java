package com.smart_learn.data.entities;

import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    // Used for Firestore. At boolean fields if 'is' appears in front leave fields without 'is'  in
    // order to work with Firestore.
    public interface Fields {
        String TYPE_FIELD_NAME = "type";
        String TEST_NAME_FIELD_NAME = "testName";
        String CUSTOM_TEST_NAME_FIELD_NAME = "customTestName";
        String TEST_GENERATION_DATE_FIELD_NAME = "testGenerationDate";
        String SUCCESS_RATE_FIELD_NAME = "successRate";
        String IS_HIDDEN_FIELD_NAME = "hidden";
        String IS_FINISHED_FIELD_NAME = "finished";
        String IS_GENERATED_FIELD_NAME = "generated";
        String QUESTIONS_JSON_FIELD_NAME = "questionsJson";
        String TOTAL_QUESTIONS_FIELD_NAME = "totalQuestions";
        String ANSWERED_QUESTIONS_FIELD_NAME = "answeredQuestions";
        String CORRECT_ANSWERS_FIELD_NAME = "correctAnswers";
        String TEST_TOTAL_TIME_FIELD_NAME = "testTotalTime";
        String USE_CUSTOM_SELECTION_FIELD_NAME = "useCustomSelection";
        String NR_OF_VALUES_FOR_GENERATING_FIELD_NAME = "nrOfValuesForGenerating";
        String QUESTION_COUNTER_FIELD_NAME = "questionCounter";
        String IS_SCHEDULED_FIELD_NAME = "scheduled";
        String IS_SCHEDULE_ACTIVE_FIELD_NAME = "scheduleActive";
        String HOUR_FIELD_NAME = "hour";
        String MINUTE_FIELD_NAME = "minute";
        String ONE_TIME_FIELD_NAME = "oneTime";
        String DAY_OF_MONTH_FIELD_NAME = "dayOfMonth";
        String MONTH_FIELD_NAME = "month";
        String YEAR_FIELD_NAME = "year";
        String ALARM_ID_FIELD_NAME = "alarmId";
        String DAYS_STATUS_FIELD_NAME = "daysStatus";
        String LESSON_ID_FIELD_NAME = "lessonId";
        String LESSON_NAME_FIELD_NAME = "lessonName";
        String IS_SHARED_LESSON_FIELD_NAME = "sharedLesson";
    }

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
    // correct questions/total questions (used for average calculus in queries)
    private float successRate;

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
    // used to manage alarms by AlarmService
    private int alarmId;

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
    private boolean isSharedLesson;

    public Test(){
        // initial values
        type = Types.NO_TYPE;
        nrOfValuesForGenerating = USE_ALL;
        questionCounter = NO_COUNTER;
        daysStatus = new ArrayList<>(Collections.nCopies(NR_OF_WEEK_DAYS, false));
        dayOfMonth = NO_DATE_TIME;
        month = NO_DATE_TIME;
        year = NO_DATE_TIME;
        alarmId = NO_DATE_TIME;
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

    public void setAlarm(String scheduledTestId, boolean forUser){
        // if is already set do not set again
        if(isScheduleActive){
            return;
        }
        isScheduleActive = true;
        if(oneTime){
            alarmId = TestService.ScheduledTestAlarmManager.getInstance().setExactAlarm(
                    scheduledTestId,
                    getAlarmNotificationMessage(),
                    forUser,
                    CoreUtilities.General.timeToLong(hour, minute, dayOfMonth, month, year)
            );
            return;
        }

        alarmId = TestService.ScheduledTestAlarmManager.getInstance().setAlarmRepeatingInSpecificDays(
                scheduledTestId,
                getAlarmNotificationMessage(),
                forUser,
                hour,
                minute,
                daysStatus.get(0),
                daysStatus.get(1),
                daysStatus.get(2),
                daysStatus.get(3),
                daysStatus.get(4),
                daysStatus.get(5),
                daysStatus.get(6)
        );
    }

    private void setAlarm(int alarmId, String scheduledTestId, boolean forUser){
        // if is already set do not set again
        if(isScheduleActive){
            return;
        }
        isScheduleActive = true;
        if(oneTime){
            TestService.ScheduledTestAlarmManager.getInstance().setExactAlarm(
                    alarmId,
                    scheduledTestId,
                    getAlarmNotificationMessage(),
                    forUser,
                    CoreUtilities.General.timeToLong(hour, minute, dayOfMonth, month, year)
            );
            return;
        }

        TestService.ScheduledTestAlarmManager.getInstance().setAlarmRepeatingInSpecificDays(
                alarmId,
                scheduledTestId,
                getAlarmNotificationMessage(),
                forUser,
                hour,
                minute,
                daysStatus.get(0),
                daysStatus.get(1),
                daysStatus.get(2),
                daysStatus.get(3),
                daysStatus.get(4),
                daysStatus.get(5),
                daysStatus.get(6)
        );
    }

    public void resetAlarm(String scheduledTestId, boolean forUser){
        final int sameAlarmId = alarmId;
        // if alarm is already set deactivate it
        if(isScheduleActive){
            cancelAlarm(scheduledTestId, forUser);
        }
        // and set alarm again with same id
        setAlarm(sameAlarmId, scheduledTestId, forUser);
    }

    public void cancelAlarm(String scheduledTestId, boolean forUser){
        // if is already stopped, not cancel again
        if(!isScheduleActive){
            return;
        }
        isScheduleActive = false;
        if(oneTime){
            TestService.ScheduledTestAlarmManager.getInstance().cancelAlarm(
                    scheduledTestId,
                    getAlarmNotificationMessage(),
                    forUser,
                    alarmId
            );
            alarmId = NO_DATE_TIME;
            return;
        }

        TestService.ScheduledTestAlarmManager.getInstance().cancelAlarmRepeatingInSpecificDays(
                scheduledTestId,
                getAlarmNotificationMessage(),
                forUser,
                alarmId,
                daysStatus.get(0),
                daysStatus.get(1),
                daysStatus.get(2),
                daysStatus.get(3),
                daysStatus.get(4),
                daysStatus.get(5),
                daysStatus.get(6)
        );
        alarmId = NO_DATE_TIME;
    }

    private String getAlarmNotificationMessage(){
        if(customTestName.isEmpty()){
            return ApplicationController.getInstance().getString(R.string.scheduled_test_alarm_notification_message) + " " +
                    ApplicationController.getInstance().getString(R.string.from_lesson) + " " + lessonName;
        }
        return ApplicationController.getInstance().getString(R.string.scheduled_test_alarm_notification_message) + " " + customTestName + " "
                + ApplicationController.getInstance().getString(R.string.from_lesson) + " " + lessonName;
    }

    public static HashMap<String, Object> convertDocumentToHashMap(Test test){
        if(test == null){
            return new HashMap<>();
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put(Fields.TYPE_FIELD_NAME, test.getType());
        data.put(Fields.TEST_NAME_FIELD_NAME, test.getTestName());
        data.put(Fields.CUSTOM_TEST_NAME_FIELD_NAME, test.getCustomTestName());
        data.put(Fields.TEST_GENERATION_DATE_FIELD_NAME, test.getTestGenerationDate());
        data.put(Fields.SUCCESS_RATE_FIELD_NAME, test.getSuccessRate());
        data.put(Fields.IS_HIDDEN_FIELD_NAME, test.isHidden());
        data.put(Fields.IS_FINISHED_FIELD_NAME, test.isFinished());
        data.put(Fields.IS_GENERATED_FIELD_NAME, test.isGenerated());
        data.put(Fields.QUESTIONS_JSON_FIELD_NAME, test.getQuestionsJson());
        data.put(Fields.TOTAL_QUESTIONS_FIELD_NAME, test.getTotalQuestions());
        data.put(Fields.ANSWERED_QUESTIONS_FIELD_NAME, test.getAnsweredQuestions());
        data.put(Fields.CORRECT_ANSWERS_FIELD_NAME, test.getCorrectAnswers());
        data.put(Fields.TEST_TOTAL_TIME_FIELD_NAME, test.getTestTotalTime());
        data.put(Fields.USE_CUSTOM_SELECTION_FIELD_NAME, test.isUseCustomSelection());
        data.put(Fields.NR_OF_VALUES_FOR_GENERATING_FIELD_NAME, test.getNrOfValuesForGenerating());
        data.put(Fields.QUESTION_COUNTER_FIELD_NAME, test.getQuestionCounter());
        data.put(Fields.IS_SCHEDULED_FIELD_NAME, test.isScheduled());
        data.put(Fields.IS_SCHEDULE_ACTIVE_FIELD_NAME, test.isScheduleActive());
        data.put(Fields.HOUR_FIELD_NAME, test.getHour());
        data.put(Fields.MINUTE_FIELD_NAME, test.getMinute());
        data.put(Fields.ONE_TIME_FIELD_NAME, test.isOneTime());
        data.put(Fields.DAY_OF_MONTH_FIELD_NAME, test.getDayOfMonth());
        data.put(Fields.MONTH_FIELD_NAME, test.getMonth());
        data.put(Fields.YEAR_FIELD_NAME, test.getYear());
        data.put(Fields.ALARM_ID_FIELD_NAME, test.getAlarmId());
        data.put(Fields.DAYS_STATUS_FIELD_NAME, test.getDaysStatus());
        data.put(Fields.LESSON_ID_FIELD_NAME, test.getLessonId());
        data.put(Fields.LESSON_NAME_FIELD_NAME, test.getLessonName());
        data.put(Fields.IS_SHARED_LESSON_FIELD_NAME, test.isSharedLesson());

        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;

        Test test = (Test) o;

        if (getType() != test.getType()) return false;
        if (getTestGenerationDate() != test.getTestGenerationDate()) return false;
        if (Float.compare(test.getSuccessRate(), getSuccessRate()) != 0) return false;
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
        if (getAlarmId() != test.getAlarmId()) return false;
        if (isSharedLesson() != test.isSharedLesson()) return false;
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
        result = 31 * result + (getSuccessRate() != +0.0f ? Float.floatToIntBits(getSuccessRate()) : 0);
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
        result = 31 * result + getAlarmId();
        result = 31 * result + getDaysStatus().hashCode();
        result = 31 * result + getLessonId().hashCode();
        result = 31 * result + getLessonName().hashCode();
        result = 31 * result + (isSharedLesson() ? 1 : 0);
        return result;
    }
}
