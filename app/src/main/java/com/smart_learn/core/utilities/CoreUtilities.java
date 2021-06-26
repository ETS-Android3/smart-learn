package com.smart_learn.core.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.smart_learn.presenter.helpers.ApplicationController;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

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
         * */
        public static String longToDate(long milliseconds){
            //https://www.geeksforgeeks.org/program-to-convert-milliseconds-to-a-date-format-in-java/
            return DateFormat.getDateInstance().format(new Date(milliseconds));
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
    }

}
