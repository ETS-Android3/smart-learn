package com.smart_learn.presenter.helpers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.core.services.SettingsService;

/**
 * The main activity from which all the activities of the application must be extended.
 * */
public abstract class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Language must be loaded every time when app starts. Adding this in ApplicationController
        // did not work, so load language every time when an activity is created. Configurations will
        // be changed only if language was changed.
        // https://stackoverflow.com/questions/51053724/how-to-make-stay-in-the-last-language-when-android-app-is-closed
        // https://stackoverflow.com/questions/2900023/change-app-language-programmatically-in-android
        SettingsService.getInstance().loadLanguageConfiguration(getBaseContext());
    }
}