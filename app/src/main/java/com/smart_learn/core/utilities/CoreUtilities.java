package com.smart_learn.core.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
