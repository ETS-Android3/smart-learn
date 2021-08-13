package com.smart_learn.core.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.smart_learn.R;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Main utilities class for core layer.
 *
 * https://projectlombok.org/features/experimental/UtilityClass
 * https://stackoverflow.com/questions/25223553/how-can-i-create-an-utility-class
 * */
public abstract class CoreUtilities {

    /** Use a private constructor in order to avoid instantiation. */
    private CoreUtilities(){
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    /** General utilities. */
    public abstract static class General {

        /**
         * Used while filtering in adapters in order to give a list off all elements.
          */
        public static final String DEFAULT_VALUE_FOR_SEARCH = " ";

        // used for calendars
        public static final int MIN_HOUR = 0;
        public static final int MAX_HOUR = 23;
        public static final int MIN_MINUTE = 0;
        public static final int MAX_MINUTE = 59;
        public static final int MIN_MONTH_DAY = 1;
        public static final int MAX_MONTH_DAY = 31;
        public static final int MIN_MONTH = 0;
        public static final int MAX_MONTH = 11;
        public static final int MIN_YEAR = 2021;
        public static final int MAX_YEAR = 2100;
        public static final int STANDARD_YEAR_DAYS = 365;
        public static final int STANDARD_MONTH_DAYS = 30;


        /**
         * Use this in order to convert millisecond in a standard locale date format.
         *
         * @param milliseconds Value to be converted.
         *
         * @return Format which will include only day, month and year.
         * */
        public static String longToDate(long milliseconds){
            //https://www.geeksforgeeks.org/program-to-convert-milliseconds-to-a-date-format-in-java/
            return DateFormat.getDateInstance().format(new Date(milliseconds));
        }


        /**
         * Use this in order to convert millisecond in a standard locale date and time format.
         *
         * @param milliseconds Value to be converted.
         *
         * @return Format which will include day, month, year and time (hour, minutes, seconds, AM/PM).
         * */
        public static String longToDateTime(long milliseconds){
            return DateFormat.getDateTimeInstance().format(new Date(milliseconds));
        }

        /**
         * @return Current hour of day.
         * */
        public static int getCurrentHour(){
            return Calendar.getInstance(TimeZone.getDefault()).get(Calendar.HOUR_OF_DAY);
        }

        /**
         * @return Current minute of current hour.
         * */
        public static int getCurrentMinute(){
            return Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MINUTE);
        }

        /**
         * @return Current day of month.
         * */
        public static int getDayOfMonth(){
            // https://beginnersbook.com/2014/01/how-to-get-current-day-month-year-day-of-weekmonthyear-in-java/
            return Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DATE);
        }

        /**
         * @return Current month.
         * */
        public static int getMonth(){
            // https://beginnersbook.com/2014/01/how-to-get-current-day-month-year-day-of-weekmonthyear-in-java/
            return Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH);
        }

        /**
         * @return Current year.
         * */
        public static int getYear(){
            // https://beginnersbook.com/2014/01/how-to-get-current-day-month-year-day-of-weekmonthyear-in-java/
            return Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR);
        }

        /**
         * Check if a year is leap or not.
         *
         * @param year Year to be checked.
         *
         * @return true if is a leap year, false otherwise.
         * */
        public static boolean isLeapYear(int year){
            // https://stackoverflow.com/questions/1021324/java-code-for-calculating-leap-year
            return new GregorianCalendar().isLeapYear(year);
        }

        /**
         * Format date with format 'dayOfMonth X month X year' where X is a specified separator.
         *
         * @param dayOfMonth DayOfMonth [1,31]
         * @param month [0,11]
         * @param year [1,2100]
         * @param separator Separator to be applied (e.g. '-', '/' ...).
         *
         * @return A formatted date with specified format, or a, empty value if parameters are not
         *      valid.
         * */
        @NonNull
        @NotNull
        public static String getDateStringValue(int dayOfMonth, int month, int year, String separator){
            final String emptyValue = "";

            if(dayOfMonth < MIN_MONTH_DAY || dayOfMonth > MAX_MONTH_DAY ||
                    month < MIN_MONTH || month > MAX_MONTH ||
                    year < MIN_YEAR || year > MAX_YEAR){
                return emptyValue;
            }

            switch (month){
                case Calendar.JANUARY:
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.january) + separator + year;
                case Calendar.FEBRUARY:
                    final boolean isLeapYear = isLeapYear(year);
                    if(isLeapYear && dayOfMonth > 29){
                        return emptyValue;
                    }
                    if(!isLeapYear && dayOfMonth > 28){
                        return emptyValue;
                    }
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.ferbruary) + separator + year;
                case Calendar.MARCH:
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.march) + separator + year;
                case Calendar.APRIL:
                    if(dayOfMonth > 30){
                        return emptyValue;
                    }
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.april) + separator + year;
                case Calendar.MAY:
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.may) + separator + year;
                case Calendar.JUNE:
                    if(dayOfMonth > 30){
                        return emptyValue;
                    }
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.june) + separator + year;
                case Calendar.JULY:
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.july) + separator + year;
                case Calendar.AUGUST:
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.august) + separator + year;
                case Calendar.SEPTEMBER:
                    if(dayOfMonth > 30){
                        return emptyValue;
                    }
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.september) + separator + year;
                case Calendar.OCTOBER:
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.october) + separator + year;
                case Calendar.NOVEMBER:
                    if(dayOfMonth > 30){
                        return emptyValue;
                    }
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.november) + separator + year;
                case Calendar.DECEMBER:
                    return dayOfMonth + separator + ApplicationController.getInstance().getString(R.string.december) + separator + year;
                default:
                    return emptyValue;
            }
        }


        /**
         * Check if hour is in INTERVAL [MIN_HOUR, MAX_HOUR]
         *
         * @param hour Hour to be checked.
         *
         * @return true if is in interval, false otherwise.
         * */
        private static boolean isHourValid(int hour){
            return hour >= MIN_HOUR && hour <= MAX_HOUR;
        }


        /**
         * Check if minute is in INTERVAL [MIN_MINUTE, MAX_MINUTE]
         *
         * @param minute Minute to be checked.
         *
         * @return true if is in interval, false otherwise.
         * */
        private static boolean isMinuteValid(int minute){
            return minute >= MIN_MINUTE && minute <= MAX_MINUTE;
        }


        /**
         * Check if day is in INTERVAL [MIN_MONTH_DAY, MAX_MONTH_DAY]
         *
         * @param day Day to be checked.
         *
         * @return true if is in interval, false otherwise.
         * */
        private static boolean isDayValid(int day){
            return day >= MIN_MONTH_DAY && day <= MAX_MONTH_DAY;
        }

        /**
         * Check if day is a day of week.
         *
         * @param day Day to be checked.
         *
         * @return true if is valid, false otherwise.
         * */
        private static boolean isDayOfWeekValid(int day){
            return day == Calendar.MONDAY ||
                    day == Calendar.TUESDAY ||
                    day == Calendar.WEDNESDAY ||
                    day == Calendar.THURSDAY ||
                    day == Calendar.FRIDAY ||
                    day == Calendar.SATURDAY ||
                    day == Calendar.SUNDAY;
        }


        /**
         * Check if month is in INTERVAL [MIN_MONTH, MAX_MONTH]
         *
         * @param month Month to be checked.
         *
         * @return true if is in interval, false otherwise.
         * */
        private static boolean isMonthValid(int month){
            return month >= MIN_MONTH && month <= MAX_MONTH;
        }


        /**
         * Check if year is in INTERVAL [MIN_YEAR, MAX_YEAR]
         *
         * @param year Year to be checked.
         *
         * @return true if is in interval, false otherwise.
         * */
        private static boolean isYearValid(int year){
            return year >= MIN_YEAR && year <= MAX_YEAR;
        }


        /**
         * Check if given date and time is in future compared with current time.
         *
         * @param hour Hour to be compared.
         * @param minute Minute to be compared.
         * @param dayOfMonth Day to be compared.
         * @param month Month to pe compared.
         * @param year Year to be compared.
         *
         * @return true if date is in future, false otherwise.
         * */
        public static boolean isDateAndTimeInFuture(int hour, int minute, int dayOfMonth, int month, int year){
            if(!isHourValid(hour) || !isMinuteValid(minute) || !isDayValid(dayOfMonth) || !isMonthValid(month) || !isYearValid(year)){
                // TODO: here an exception throw can be made
                return false;
            }

            // If 'Date currentDate = new GregorianCalendar().getTime()' is used comparison will fail
            // because will set an entire time (including millisecond).
            // In order to have a correct comparison set only needed values.
            int currentHour = getCurrentHour();
            int currentMinute = getCurrentMinute();
            int currentYear = getYear();
            int currentMonth = getMonth();
            int currentDayOfMonth = getDayOfMonth();

            Date currentDate = new GregorianCalendar(currentYear, currentMonth, currentDayOfMonth, currentHour, currentMinute).getTime();
            Date givenDate = new GregorianCalendar(year, month, dayOfMonth, hour, minute).getTime();
            return currentDate.compareTo(givenDate) < 0;
        }


        /**
         * Check if given date is in future or equal compared with current time.
         *
         * @param dayOfMonth Day to be compared.
         * @param month Month to pe compared.
         * @param year Year to be compared.
         *
         * @return true if date is in future or equal, false otherwise.
         * */
        public static boolean isDateInFutureOrEqual(int dayOfMonth, int month, int year){
            if(!isDayValid(dayOfMonth) || !isMonthValid(month) || !isYearValid(year)){
                // TODO: here an exception throw can be made
                return false;
            }

            // If 'Date currentDate = new GregorianCalendar().getTime()' is used comparison will fail
            // because will set an entire time (including minute/seconds/millisecond).
            // In order to have a correct comparison set only needed values.
            int currentYear = getYear();
            int currentMonth = getMonth();
            int currentDayOfMonth = getDayOfMonth();

            Date currentDate = new GregorianCalendar(currentYear, currentMonth, currentDayOfMonth).getTime();
            Date givenDate = new GregorianCalendar(year, month, dayOfMonth).getTime();
            return currentDate.compareTo(givenDate) <= 0;
        }

        /**
         * Convert date to long.
         *
         * @param hour Hour to be converted.
         * @param minute Minute to be converted.
         * @param dayOfMonth Day to be converted.
         * @param month Month to pe converted.
         * @param year Year to be converted.
         *
         * @return long value in milliseconds.
         * */
        public static long timeToLong(int hour, int minute, int dayOfMonth, int month, int year){
            if(!isHourValid(hour) || !isMinuteValid(minute) || !isDayValid(dayOfMonth) || !isMonthValid(month) || !isYearValid(year)){
                // TODO: here an exception throw can be made
                return 0;
            }
            return new GregorianCalendar(year, month, dayOfMonth, hour, minute).getTimeInMillis();
        }

        /**
         * Convert custom time of a day in long.
         *
         * @param hour Hour to be converted.
         * @param minute Minute to be converted.
         * @param dayOfWeek Day of week to be converted.
         *
         * @return long value in milliseconds.
         * */
        public static long timeToLong(int hour, int minute, int dayOfWeek){
            if(!isHourValid(hour) || !isMinuteValid(minute) || !isDayOfWeekValid(dayOfWeek)){
                // TODO: here an exception throw can be made
                return 0;
            }

            // Calendar.getInstance() returns a new object of type GregorianCalendar.
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            calendar.setFirstDayOfWeek(Calendar.MONDAY);

            // This function was created when alarm for scheduled test was added so check this info
            // if necessary.
            // https://stackoverflow.com/questions/17894067/set-repeat-days-of-week-alarm-in-android
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            return calendar.getTimeInMillis();
        }

        /**
         * Get a formatted difference between a past time and current time. Format will be:
         *   - 'one year ago / ... years ago' if are >= 1 years in difference
         *   - 'one month ago / ... months ago' if are >= 1 months in difference
         *   - 'one day ago / ... days ago' if are >= 1 days in difference
         *   - 'one hour ago / ... hours ago' if are >= 1 hours in difference
         *   - 'one minute ago / ... minutes ago' if are >= 1 minutes in difference
         *   - 'one second ago / ... seconds ago' if are >= 1 seconds in difference
         *   - 'now' if is no difference
         *   - "" is time is bigger than current time.
         *
         * @param time Value for which will be calculated difference.
         *
         * @return Formatted value.
         * */
        @NonNull @NotNull
        public static String getFormattedTimeDifferenceFromPastToPresent(long time){
            long currentTime = System.currentTimeMillis();
            long difference = currentTime - time;
            // check if given time was not in future
            if(difference < 0){
                return "";
            }
            if(difference == 0){
                return ApplicationController.getInstance().getString(R.string.now);
            }

            // check years
            long days = TimeUnit.MILLISECONDS.toDays(difference);
            if(days >= STANDARD_YEAR_DAYS){
                if(days / STANDARD_YEAR_DAYS > 1){
                    return (days / STANDARD_YEAR_DAYS) + " " + ApplicationController.getInstance().getString(R.string.years_ago);
                }
                return ApplicationController.getInstance().getString(R.string.one_year_ago);
            }

            // check months
            if(days >= STANDARD_MONTH_DAYS){
                if(days / STANDARD_MONTH_DAYS > 1){
                    return (days / STANDARD_MONTH_DAYS) + " " + ApplicationController.getInstance().getString(R.string.months_ago);
                }
                return ApplicationController.getInstance().getString(R.string.one_month_ago);
            }

            // check days
            if(days > 0){
                if(days > 1){
                    return days + " " + ApplicationController.getInstance().getString(R.string.days_ago);
                }
                return ApplicationController.getInstance().getString(R.string.one_day_ago);
            }

            // check hours
            long hours = TimeUnit.MILLISECONDS.toHours(difference);
            if(hours > 0){
                if(hours > 1){
                    return hours + " " + ApplicationController.getInstance().getString(R.string.hours_ago);
                }
                return ApplicationController.getInstance().getString(R.string.one_hour_ago);
            }

            // check minutes
            long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
            if(minutes > 0){
                if(minutes > 1){
                    return minutes + " " + ApplicationController.getInstance().getString(R.string.minutes_ago);
                }
                return ApplicationController.getInstance().getString(R.string.one_minute_ago);
            }

            // check seconds
            long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);
            if(seconds > 0){
                if(seconds > 1){
                    return seconds + " " + ApplicationController.getInstance().getString(R.string.seconds_ago);
                }
                return ApplicationController.getInstance().getString(R.string.one_second_ago);
            }

            return ApplicationController.getInstance().getString(R.string.now);
        }


        /**
         * Use to remove all adjacent spaces and new lines from a string.
         * All adjacent spaces/new lines will be replaced with a single space.
         *
         * @param value String to be processed.
         *
         * @return Processed string.
         * */
        @NonNull @NotNull
        public static String removeAdjacentSpacesAndNewLines(String value){
            if(value == null || value.isEmpty()){
                return "";
            }

            // https://stackoverflow.com/questions/2932392/java-how-to-replace-2-or-more-spaces-with-single-space-in-string-and-delete-lead/2932439#2932439
            // https://stackoverflow.com/questions/2163045/how-to-remove-line-breaks-from-a-file-in-java/2163204#2163204
            return value
                    .trim()
                    .replaceAll("\\R+", " ")
                    .replaceAll(" +", " ");
        }


        /**
         * Use to remove spaces from a string.
         *
         * @param value String for which space removal is made.
         *
         * @return String without spaces.
         * */
        @NotNull
        @NonNull
        private static String removeSpaces(String value){
            if(value == null || value.isEmpty()){
                return "";
            }

            // https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
            return value.replaceAll("\\s","");
        }


        /**
         * Use to split a String in all possible substrings.
         *
         * @param value String to be split.
         *
         * @return ArrayList which will contain all substring, or an empty ArrayList if no substring
         * is found.
         * */
        @NotNull
        @NonNull
        private static ArrayList<String> splitStringInSubstrings(String value){
            /*
             For example, string 'value' will be split as follows:
                  v
                  va
                  val
                  valu
                  value
                  a
                  al
                  alu
                  alue
                  l
                  lu
                  lue
                  u
                  ue
                  e
             */

            if(value == null || value.isEmpty()){
                return new ArrayList<>();
            }

            // https://www.geeksforgeeks.org/program-print-substrings-given-string/
            // Use set`s to avoid duplicates
            HashSet<String> hashSet = new HashSet<>();
            int lim = value.length();
            for(int i = 0; i < lim; i++){
                for(int j = i + 1; j <= lim; j++){
                    hashSet.add(value.substring(i,j));
                }
            }

            return new ArrayList<>(hashSet);
        }


        /**
         * Use to create a special String for url search.
         *
         * @param value String to be split.
         *
         * @return Url type string.
         * */
        @NotNull
        @NonNull
        public static String getStringForUrlSearch(String value){
            /*
              Example:
                - From value "the   red    nice apple" will result "the+red+nice+apple"
             */
            if(value == null || value.isEmpty()){
                return "";
            }

            // https://stackoverflow.com/questions/2932392/java-how-to-replace-2-or-more-spaces-with-single-space-in-string-and-delete-lead/2932439#2932439
            value = value.trim().replaceAll(" +", " ");
            value = value.replace(' ','+');
            return value;
        }


        /**
         * Use to generate a search list array for a Firestore document.
         *
         * @param valueList The strings on the basis of which the generation will be made.
         *
         * @return An ArrayList which will contain search list.
         * */
        @NotNull
        @NonNull
        public static ArrayList<String> generateSearchListForFirestoreDocument(ArrayList<String> valueList){
            if(valueList == null || valueList.isEmpty()){
                return new ArrayList<>();
            }

            // Use set`s to avoid duplicates
            HashSet<String> resultList = new HashSet<>();
            for(String value : valueList){
                value = removeSpaces(value);
                value = value.toLowerCase();
                resultList.addAll(splitStringInSubstrings(value));
            }

            // Add by default a special value for search, which will be used in order to get all
            // data, because all data will contain that value.
            resultList.add(DEFAULT_VALUE_FOR_SEARCH);

            // sort values in order to speed up debugging process if necessary
            ArrayList<String> tmp = new ArrayList<>(resultList);
            tmp.sort(String::compareTo);
            return tmp;
        }


        /**
         * Use to get all pairs of indexes for substring apparitions in fullString.
         * Pair will be as (startPosition, finalPosition) with finalPosition exclusive and will
         * contain all substring apparitions in the fullString.
         *
         * @param fullString String for which indexes will be calculated.
         * @param substring Substring to be searched.
         *
         * @return An ArrayList of index pairs, or a empty ArrayList if substring does not appear in
         *          fullString.
         * */
        @NotNull
        @NonNull
        public static ArrayList<Pair<Integer, Integer>> getSubstringIndexes(String fullString, String substring){

            /*
            * For example for fullString 'magic m mac' and substring 'ma' then the following indexes
            * will be generated:
            *       (0,2)  ==> 'ma' starts in position 0 on fullString and is finished on position
            *                   1 (2 exclusive)
            *      (8,10)  ==> 'ma' starts in position 8 on fullString and is finished on position
            *                   9 (10 exclusive)
            *
            *       So pair (0,2) and (8,10) will be generated.
            *
            *   Position 6 where 'm' appear is ignored because after that 'm' does not came an 'a'
            * */

            if(fullString == null || fullString.isEmpty() || substring == null || substring.isEmpty()){
                return new ArrayList<>();
            }

            ArrayList<Pair<Integer, Integer>> indexes = new ArrayList<>();

            char[] charArrayFullString = fullString.toCharArray();
            char[] charArraySubstring = substring.toCharArray();

            int j = 0;
            int start = 0;
            for(int i = 0; i < charArrayFullString.length; i++){
                // Here parts of 'substring' are contained in 'fullString'.
                if(charArrayFullString[i] == charArraySubstring[j]){
                    // If 'substring' starts in 'fullString' mark start index.
                    if(j == 0){
                        start = i;

                        // special case if substring is formed from one single element
                        if(charArraySubstring.length == 1){
                            indexes.add(new Pair<>(start, start + 1));
                            j = 0;
                            continue;
                        }

                        j++;
                        continue;
                    }
                    // If 'substring' is finished then is contained in the 'fullString' so add pair
                    // and reset values for the next search.
                    if(j == charArraySubstring.length - 1){
                        indexes.add(new Pair<>(start, i + 1));
                        start = 0;
                        j = 0;
                        continue;
                    }

                    // Here we progress in 'substring' because 'substring' is not finished.
                    j++;
                    continue;
                }

                // Reset values in order to keep searching on the rest of the 'fullString'.
                start = 0;
                j = 0;
            }

            return indexes;
        }


        /**
         * Use to check if objects are the same. Object should have equals() implemented.
         *
         * @param objectA First object for comparison.
         * @param objectB Second object for comparison.
         *
         * @return true if objectA has content the same as objectB, or false otherwise.
         * */
        public static boolean areObjectsTheSame(Object objectA, Object objectB){
            if(objectA == null && objectB == null){
                return true;
            }

            if(objectA == null || objectB == null){
                return false;
            }

            return objectA.equals(objectB);
        }


        /**
         * Use to check if item is not null.
         *
         * @param item Item to be checked.
         *
         * @return true if item is not null, false otherwise.
         * */
        public static <T> boolean isItemNotNull(T item){
            if(item == null){
                Timber.w("item is null");
                return false;
            }
            return true;
        }

        /**
         * Use to format a decimal value with pattern '#.##'
         *
         * @param value Value to be formatted.
         *
         * @return String value representing formatted value.
         * */
        public static String formatFloatValue(double value){
            return formatFloatValue(value, "#.##");
        }


        /**
         * Use to format a decimal value with specific pattern (e.g. '#.##')
         *
         * @param value Value to be formatted.
         *
         * @return String value representing formatted value.
         * */
        public static String formatFloatValue(double value, String pattern){
            // https://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(value);
        }

        /**
         * Use to get an instance of a secure random number generator (RNG).
         *
         * @return An instance of a secure random number generator (RNG)
         * */
        public static SecureRandom getSecureRandomInstance(){
            // There is no need for seed, because it be seeded automatically when a value is requested.
            // https://stackoverflow.com/questions/30012295/java-8-lambda-filter-by-lists
            return new SecureRandom();
        }

        /**
         * Use to generate an uniqueId.
         *
         * @return An unique id.
         * */
        public static long generateUniqueId(){
            // TODO: try to find a better way to generate a unique long id.
            return System.currentTimeMillis();
        }
    }


    /** All utilities related to Authentication. */
    public abstract static class Auth {

        // provider id to specific that a user is not logged in
        public static final int PROVIDER_NONE = 0;
        // provider id to specific that a user is logged in with email and password
        public static final int PROVIDER_EMAIL = 1;
        // provider id to specific that a user is logged in with a Google account
        public static final int PROVIDER_GOOGLE = 2;
        // used to set a UID string if user is not logged in
        public static final String GUEST_UID = "guest_uid";


        /**
         * Use this in order to get user providerId.
         *
         * @return CoreUtilities.Auth.PROVIDER_NONE if user is not logged in or some error occurred,
         *         CoreUtilities.Auth.PROVIDER_EMAIL if user is logged using an email and password,
         *         CoreUtilities.Auth.PROVIDER_GOOGLE if user is logged using a Google account.
         * */
        public static int getProvider(){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser == null){
                return PROVIDER_NONE;
            }

            // https://stackoverflow.com/questions/46253226/detect-firebase-auth-provider-for-loged-in-user
            // https://github.com/firebase/FirebaseUI-Android/issues/329
            List<? extends UserInfo> providerData = firebaseUser.getProviderData();
            for (UserInfo userInfo : providerData) {
                if (userInfo.getProviderId().toLowerCase().equals("password")) {
                    return PROVIDER_EMAIL;
                }
                if (userInfo.getProviderId().toLowerCase().equals("google.com")) {
                    return PROVIDER_GOOGLE;
                }
            }
            return PROVIDER_NONE;

        }

        /**
         * Use this in order to check if user is logged in or not.
         *
         * @return true if user is logged in, false otherwise.
         * */
        public static boolean isUserLoggedIn(){
            SharedPreferences preferences = ApplicationController.getInstance()
                    .getSharedPreferences(ApplicationController.LOGIN_STATUS_KEY, Context.MODE_PRIVATE);
            return preferences.getBoolean(ApplicationController.LOGGED_IN, false);
        }


        /**
         * Use this in order to get user display name.
         *
         * @return User display name if user is logged in or "" is user is not logged in.
         * */
        @Deprecated
        public static String getUserDisplayName(){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser == null){
                return "";
            }
            return firebaseUser.getDisplayName();
        }


        /**
         * Use this in order to get user unique id (UID) .
         *
         * @return User UID if user is logged in, or CoreUtilities.Auth.GUEST_UID is user is not
         *         logged in.
         * */
        @Deprecated
        public static String getUserUid(){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser == null){
                return GUEST_UID;
            }
            return firebaseUser.getUid();
        }
    }

}
