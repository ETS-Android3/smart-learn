package com.smart_learn.presenter.activities.test.helpers.fragments.scheduled_test_info;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.dialogs.SingleLineEditableLayoutDialog;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

@Getter
@Setter
public abstract class ScheduledTestInfoViewModel extends BasicAndroidViewModel {

    // mark if a test must be updated or not
    private boolean isForUpdate;
    // if isForUpdate is true, then testId will be used to retrieve test from db
    private String testId;
    // if isForUpdate is true, then this will test retrieved from db
    private Test updatedTest;

    // used to get values from DatePicker and TimePicker dialog and also from test is isForUpdate
    private int dayOfMonth;
    private int month;
    private int year;
    private int hour;
    private int minute;

    // used to show data in layout
    private final MutableLiveData<String> liveTime;
    private final MutableLiveData<String> liveDate;
    private final MutableLiveData<String> liveCustomName;
    private final MutableLiveData<String> liveLessonName;
    private final MutableLiveData<String> liveTestTypeDescription;
    private final MutableLiveData<String> liveValuesSelectionDescription;
    private final MutableLiveData<String> liveUseCounterDescription;
    private final MutableLiveData<Boolean> liveIsRepeatMonday;
    private final MutableLiveData<Boolean> liveIsRepeatTuesday;
    private final MutableLiveData<Boolean> liveIsRepeatWednesday;
    private final MutableLiveData<Boolean> liveIsRepeatThursday;
    private final MutableLiveData<Boolean> liveIsRepeatFriday;
    private final MutableLiveData<Boolean> liveIsRepeatSaturday;
    private final MutableLiveData<Boolean> liveIsRepeatSunday;

    public ScheduledTestInfoViewModel(@NonNull @NotNull Application application) {
        super(application);
        isForUpdate = false;
        testId = "";
        updatedTest = null;

        dayOfMonth = Test.NO_DATE_TIME;
        month = Test.NO_DATE_TIME;
        year = Test.NO_DATE_TIME;
        hour = Test.NO_DATE_TIME;
        minute = Test.NO_DATE_TIME;

        liveTime = new MutableLiveData<>("");
        liveDate = new MutableLiveData<>("");
        liveCustomName = new MutableLiveData<>("");
        liveLessonName = new MutableLiveData<>("");
        liveTestTypeDescription = new MutableLiveData<>("");
        liveValuesSelectionDescription = new MutableLiveData<>("");
        liveUseCounterDescription = new MutableLiveData<>("");
        liveIsRepeatMonday = new MutableLiveData<>(false);
        liveIsRepeatTuesday = new MutableLiveData<>(false);
        liveIsRepeatWednesday = new MutableLiveData<>(false);
        liveIsRepeatThursday = new MutableLiveData<>(false);
        liveIsRepeatFriday = new MutableLiveData<>(false);
        liveIsRepeatSaturday = new MutableLiveData<>(false);
        liveIsRepeatSunday = new MutableLiveData<>(false);
    }

    public LiveData<String> getLiveDate(){
        return liveDate;
    }

    public LiveData<String> getLiveTime(){
        return liveTime;
    }

    public LiveData<String> getLiveLessonName(){
        return liveLessonName;
    }

    private void setDate(int dayOfMonth, int month, int year){
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
        if(!isDateSet()){
            resetAllDateFields();
            return;
        }
        // here date is valid so uncheck repeat checkboxes and set new date
        uncheckAllRepeatValues();
        liveDate.setValue(CoreUtilities.General.getDateStringValue(dayOfMonth, month, year, " "));
    }

    private void resetAllDateFields(){
        dayOfMonth = Test.NO_DATE_TIME;
        month = Test.NO_DATE_TIME;
        year = Test.NO_DATE_TIME;
    }

    protected boolean isDateSet(){
        return dayOfMonth != Test.NO_DATE_TIME &&
                dayOfMonth >= CoreUtilities.General.MIN_MONTH_DAY &&
                dayOfMonth <= CoreUtilities.General.MAX_MONTH_DAY &&
                month != Test.NO_DATE_TIME &&
                month >= CoreUtilities.General.MIN_MONTH &&
                month <= CoreUtilities.General.MAX_MONTH &&
                year != Test.NO_DATE_TIME &&
                year >= CoreUtilities.General.MIN_YEAR &&
                year <= CoreUtilities.General.MAX_YEAR;
    }

    protected void setRepeatValuesDescription(){
        String value = getRepeatValueDescription();
        if(value == null){
            value = "";
        }
        if(value.isEmpty() && isDateSet()){
            liveDate.setValue(CoreUtilities.General.getDateStringValue(dayOfMonth, month, year, " "));
            return;
        }
        liveDate.setValue(value);
    }

    private void setTime(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
        if(!isTimeSet()){
            resetAllTimeFields();
            return;
        }
        String hourString = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minuteString = minute < 10 ? "0" + minute : String.valueOf(minute);
        liveTime.setValue(hourString + ":" + minuteString);
    }

    protected boolean isTimeSet(){
        return hour != Test.NO_DATE_TIME &&
                hour >= CoreUtilities.General.MIN_HOUR &&
                hour <= CoreUtilities.General.MAX_HOUR &&
                minute != Test.NO_DATE_TIME &&
                minute >= CoreUtilities.General.MIN_MINUTE &&
                minute <= CoreUtilities.General.MAX_MINUTE;
    }

    private void resetAllTimeFields(){
        hour = Test.NO_DATE_TIME;
        minute = Test.NO_DATE_TIME;
    }

    private void setCustomName(String name){
        liveCustomName.setValue(name);
    }

    private void setLessonName(String name){
        liveLessonName.setValue(name);
    }

    protected String getCustomName(){
        String customName = liveCustomName.getValue();
        if(customName == null){
            return "";
        }
        return customName;
    }

    private void setTestTypeDescription(int type){
       liveTestTypeDescription.setValue(Test.getTestTypeDescription(type));
    }

    private void setValuesSelectionDescription(boolean useCustomSelection, int nrOfValues){
       String value;
       if(nrOfValues == Test.USE_ALL){
           if(useCustomSelection){
               value = ApplicationController.getInstance().getString(R.string.custom_selection);
           }
           else{
               value = ApplicationController.getInstance().getString(R.string.use_all);
           }
       }
       else{
           value = ApplicationController.getInstance().getString(R.string.use_specific_number_of_selections) + " (" + nrOfValues + ")";
       }
       liveValuesSelectionDescription.setValue(value);
    }

    private void setUseCounterDescription(int time){
        String value;
        if(time != Test.NO_COUNTER){
             value = ApplicationController.getInstance().getString(R.string.yes) + " (" + time + ")";
        }
        else{
            value = ApplicationController.getInstance().getString(R.string.no);
        }
        liveUseCounterDescription.setValue(value);
    }

    private void setRepeatValues(ArrayList<Boolean> repeatValues){
        if(repeatValues == null){
            Timber.w("repeatValues is null");
            uncheckAllRepeatValues();
            return;
        }

        if(repeatValues.size() != Test.NR_OF_WEEK_DAYS){
            Timber.w("repeatValues.size() [" + repeatValues.size() + "] is not valid");
            uncheckAllRepeatValues();
            return;
        }

        // set values
        if(Test.NR_OF_WEEK_DAYS != 7){
            Timber.w("NR_OF_WEEK_DAYS " + Test.NR_OF_WEEK_DAYS + "] is not valid");
            uncheckAllRepeatValues();
            return;
        }
        liveIsRepeatMonday.setValue(repeatValues.get(0));
        liveIsRepeatTuesday.setValue(repeatValues.get(1));
        liveIsRepeatWednesday.setValue(repeatValues.get(2));
        liveIsRepeatThursday.setValue(repeatValues.get(3));
        liveIsRepeatFriday.setValue(repeatValues.get(4));
        liveIsRepeatSaturday.setValue(repeatValues.get(5));
        liveIsRepeatSunday.setValue(repeatValues.get(6));
    }

    protected void setUpdatedTest(Test test){
        if(test == null){
            Timber.w("test is null");
            return;
        }

        updatedTest = test;
        setDate(test.getDayOfMonth(), test.getMonth(), test.getYear());
        setTime(test.getHour(), test.getMinute());
        setCustomName(test.getCustomTestName());
        setLessonName(test.getLessonName());
        setRepeatValues(test.getDaysStatus());
        setTestTypeDescription(test.getType());
        setValuesSelectionDescription(test.isUseCustomSelection(), test.getNrOfValuesForGenerating());
        setUseCounterDescription(test.getQuestionCounter());
    }

    protected void updateCustomName(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                    @NonNull @NotNull SingleLineEditableLayoutDialog.Listener listener) {
        if(newValue == null){
            newValue = "";
        }

        if(!newValue.isEmpty() && newValue.equals(oldValue)){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_name_is_same));
            return;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(newValue.length() > DataUtilities.Limits.MAX_TEST_CUSTOM_NAME){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_test_custom_name_too_long));
            return;
        }

        textInputLayout.setError(null);
        listener.onSuccessCheck();
        setCustomName(newValue);
    }

    private void uncheckAllRepeatValues() {
        liveIsRepeatMonday.setValue(false);
        liveIsRepeatTuesday.setValue(false);
        liveIsRepeatWednesday.setValue(false);
        liveIsRepeatThursday.setValue(false);
        liveIsRepeatFriday.setValue(false);
        liveIsRepeatSaturday.setValue(false);
        liveIsRepeatSunday.setValue(false);
    }

    private boolean allRepeatsAreSet(){
        final boolean mondayIsChecked = liveIsRepeatMonday.getValue() != null && liveIsRepeatMonday.getValue();
        final boolean tuesdayIsChecked = liveIsRepeatTuesday.getValue() != null && liveIsRepeatTuesday.getValue();
        final boolean wednesdayIsChecked = liveIsRepeatWednesday.getValue() != null && liveIsRepeatWednesday.getValue();
        final boolean thursdayIsChecked = liveIsRepeatThursday.getValue() != null && liveIsRepeatThursday.getValue();
        final boolean fridayIsChecked = liveIsRepeatFriday.getValue() != null && liveIsRepeatFriday.getValue();
        final boolean saturdayIsChecked = liveIsRepeatSaturday.getValue() != null && liveIsRepeatSaturday.getValue();
        final boolean sundayIsChecked = liveIsRepeatSunday.getValue() != null && liveIsRepeatSunday.getValue();
        return mondayIsChecked && tuesdayIsChecked && wednesdayIsChecked && thursdayIsChecked && fridayIsChecked && saturdayIsChecked && sundayIsChecked;
    }

    private boolean anyRepeatIsSet(){
        final boolean mondayIsChecked = liveIsRepeatMonday.getValue() != null && liveIsRepeatMonday.getValue();
        final boolean tuesdayIsChecked = liveIsRepeatTuesday.getValue() != null && liveIsRepeatTuesday.getValue();
        final boolean wednesdayIsChecked = liveIsRepeatWednesday.getValue() != null && liveIsRepeatWednesday.getValue();
        final boolean thursdayIsChecked = liveIsRepeatThursday.getValue() != null && liveIsRepeatThursday.getValue();
        final boolean fridayIsChecked = liveIsRepeatFriday.getValue() != null && liveIsRepeatFriday.getValue();
        final boolean saturdayIsChecked = liveIsRepeatSaturday.getValue() != null && liveIsRepeatSaturday.getValue();
        final boolean sundayIsChecked = liveIsRepeatSunday.getValue() != null && liveIsRepeatSunday.getValue();
        return mondayIsChecked || tuesdayIsChecked || wednesdayIsChecked || thursdayIsChecked || fridayIsChecked || saturdayIsChecked || sundayIsChecked;
    }

    private String getRepeatValueDescription() {
        if(allRepeatsAreSet()){
            return ApplicationController.getInstance().getString(R.string.every_day);
        }

        if(!anyRepeatIsSet()){
            return "";
        }

        final boolean mondayIsChecked = liveIsRepeatMonday.getValue() != null && liveIsRepeatMonday.getValue();
        final boolean tuesdayIsChecked = liveIsRepeatTuesday.getValue() != null && liveIsRepeatTuesday.getValue();
        final boolean wednesdayIsChecked = liveIsRepeatWednesday.getValue() != null && liveIsRepeatWednesday.getValue();
        final boolean thursdayIsChecked = liveIsRepeatThursday.getValue() != null && liveIsRepeatThursday.getValue();
        final boolean fridayIsChecked = liveIsRepeatFriday.getValue() != null && liveIsRepeatFriday.getValue();
        final boolean saturdayIsChecked = liveIsRepeatSaturday.getValue() != null && liveIsRepeatSaturday.getValue();
        final boolean sundayIsChecked = liveIsRepeatSunday.getValue() != null && liveIsRepeatSunday.getValue();
        String value = "";

        if(mondayIsChecked){
            value += ApplicationController.getInstance().getString(R.string.monday) + ", ";
        }
        if(tuesdayIsChecked){
            value += ApplicationController.getInstance().getString(R.string.tuesday) + ", ";
        }
        if(wednesdayIsChecked){
            value += ApplicationController.getInstance().getString(R.string.wednesday) + ", ";
        }
        if(thursdayIsChecked){
            value += ApplicationController.getInstance().getString(R.string.thursday) + ", ";
        }
        if(fridayIsChecked){
            value += ApplicationController.getInstance().getString(R.string.friday) + ", ";
        }
        if(saturdayIsChecked){
            value += ApplicationController.getInstance().getString(R.string.saturday) + ", ";
        }
        if(sundayIsChecked){
            value += ApplicationController.getInstance().getString(R.string.sunday) + ", ";
        }
        // remove last space and last comma
        value = value.trim();
        value = value.substring(0, value.length() - 1);
        return value;
    }

    private ArrayList<Boolean> getRepeatValues(){
        final boolean mondayIsChecked = liveIsRepeatMonday.getValue() != null && liveIsRepeatMonday.getValue();
        final boolean tuesdayIsChecked = liveIsRepeatTuesday.getValue() != null && liveIsRepeatTuesday.getValue();
        final boolean wednesdayIsChecked = liveIsRepeatWednesday.getValue() != null && liveIsRepeatWednesday.getValue();
        final boolean thursdayIsChecked = liveIsRepeatThursday.getValue() != null && liveIsRepeatThursday.getValue();
        final boolean fridayIsChecked = liveIsRepeatFriday.getValue() != null && liveIsRepeatFriday.getValue();
        final boolean saturdayIsChecked = liveIsRepeatSaturday.getValue() != null && liveIsRepeatSaturday.getValue();
        final boolean sundayIsChecked = liveIsRepeatSunday.getValue() != null && liveIsRepeatSunday.getValue();

        ArrayList<Boolean> tmp = new ArrayList<>();
        tmp.add(mondayIsChecked);
        tmp.add(tuesdayIsChecked);
        tmp.add(wednesdayIsChecked);
        tmp.add(thursdayIsChecked);
        tmp.add(fridayIsChecked);
        tmp.add(saturdayIsChecked);
        tmp.add(sundayIsChecked);

        return tmp;
    }

    protected void onDateSetClick(int year, int month, int dayOfMonth){
        if(!isDateValid(year, month, dayOfMonth)){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_date_is_in_past));
            return;
        }
        setDate(dayOfMonth, month, year);
    }

    protected void onTimeSetClick(int hourOfDay, int minute){
        // ignore validity if repeat is on
        if(!isTimeValid(hourOfDay, minute) && !anyRepeatIsSet()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_time_is_in_past));
            return;
        }
        setTime(hourOfDay, minute);
    }

    private boolean isSetDateValid(){
        return isDateValid(year, month, dayOfMonth);
    }

    private boolean isSetTimeValid(){
        return isTimeValid(hour, minute);
    }

    private boolean isDateValid(int year, int month, int dayOfMonth){
        // date must be in future not in past
        if(isTimeSet()){
            return CoreUtilities.General.isDateAndTimeInFuture(hour, minute, dayOfMonth, month, year);
        }
        else {
            return CoreUtilities.General.isDateInFutureOrEqual(dayOfMonth, month, year);
        }
    }

    private boolean isTimeValid(int hourOfDay, int minute) {
        if(isDateSet()){
            // time must be in future not in past
            return CoreUtilities.General.isDateAndTimeInFuture(hourOfDay, minute, dayOfMonth, month, year);
        }
        else {
            // time must be in future not in past (compare with current time)
            int dayOfMonth = CoreUtilities.General.getDayOfMonth();
            int month = CoreUtilities.General.getMonth();
            int year = CoreUtilities.General.getYear();
            return CoreUtilities.General.isDateAndTimeInFuture(hourOfDay, minute, dayOfMonth, month, year);
        }
    }

    protected void processSelections(@NonNull @NotNull ScheduledTestInfoFragment<?> fragment, boolean forUpdate){
        // check values
        if(!isTimeSet()){
            liveToastMessage.setValue(fragment.getString(R.string.error_time_is_not_set));
            return;
        }

        if(!isDateSet() && !anyRepeatIsSet()){
            liveToastMessage.setValue(fragment.getString(R.string.error_date_is_not_set));
            return;
        }

        // check if time and date are valid in moment of processing (if repeat is on then ignore)
        if(!isSetTimeValid() && !anyRepeatIsSet()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_time_is_in_past));
            return;
        }
        // if repeat is set ignore date
        if(isDateSet() && !anyRepeatIsSet() && !isSetDateValid()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_date_is_in_past));
            return;
        }

        // extract specific test
        Test newTest;
        if(forUpdate){
            newTest = updatedTest;
        }
        else{
            newTest = fragment.getSharedViewModel().getGeneratedTest();
        }

        if(newTest == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            return;
        }

        // if is for update disable old alarm
        if(isForUpdate){
            newTest.cancelAlarm(testId, newTest instanceof TestDocument);
        }

        // set test values
        // this will be a scheduled test so mark that
        newTest.setScheduled(true);

        // set date or repeat (repeat has bigger priority)
        if(anyRepeatIsSet()){
            // here no date will be set
            newTest.setDate(Test.NO_DATE_TIME, Test.NO_DATE_TIME, Test.NO_DATE_TIME);
            // oneTime must be false here
            newTest.setOneTime(false);

            newTest.setDaysStatus(getRepeatValues());
        }
        else {
            newTest.setDate(dayOfMonth, month, year);
            // because date is set, then one time must be set tot true
            newTest.setOneTime(true);

            // all repeat values will be false here
            int lim = newTest.getDaysStatus().size();
            for(int i = 0; i < lim; i++){
                newTest.getDaysStatus().set(i, false);
            }
        }

        // set time
        newTest.setHour(hour);
        newTest.setMinute(minute);

        // set custom name
        newTest.setCustomTestName(getCustomName());


        if(forUpdate){
            // if is an user oneTime scheduled test then mark that alarm was not triggered on device
            if((newTest instanceof TestDocument) && newTest.isOneTime()){
                ((TestDocument) newTest).setAlarmWasLaunched(false);
            }
            // here test already exists so update it in db
            fragment.updateTest(newTest);
        }
        else{
            // here a new scheduled test will be generated and options must be set
            fragment.navigateToSelectLessonFragment();
        }
    }
}
