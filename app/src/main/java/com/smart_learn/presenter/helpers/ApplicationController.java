package com.smart_learn.presenter.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.smart_learn.BuildConfig;

import timber.log.Timber;

public class ApplicationController extends Application {

    // for shared preferences
    public static final String LOGIN_STATUS_KEY = "LOGIN_STATUS_KEY";
    public static final String LOGGED_IN = "LOGGED_IN";

    private static ApplicationController applicationController;

    public static ApplicationController getInstance() {return applicationController;}

    @Override
    public void onCreate() {
        super.onCreate();
        applicationController = this;
        if(BuildConfig.DEBUG){
            Timber.plant(new CustomTimberDebugTree());
        }

        addUserStatusListener();
    }

    private void addUserStatusListener(){

        // https://stackoverflow.com/questions/42571618/how-to-make-a-user-sign-out-in-firebase/51571501#51571501
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // This listener is used only to mark if user is no longer logged in.
                // To mark that a user is logged it will be made in login functions for every provider.
                if(firebaseAuth.getCurrentUser() == null){
                    SharedPreferences preferences = getSharedPreferences(LOGIN_STATUS_KEY, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(LOGGED_IN, false);
                    editor.apply();
                }
            }
        };

        // attach listener
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

}
