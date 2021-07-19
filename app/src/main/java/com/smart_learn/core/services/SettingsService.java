package com.smart_learn.core.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.smart_learn.presenter.helpers.ApplicationController;

public class SettingsService {

    public static final int NO_SHOW_OPTION = -1;
    private static final String SHOW_OPTIONS_KEY = "SHOW_OPTIONS_KEY";
    private static final String USER_LESSON_SHOW_OPTION = "USER_LESSON_SHOW_OPTION";

    private static SettingsService instance;

    private SettingsService() {

    }

    public static SettingsService getInstance() {
        if(instance == null){
            instance = new SettingsService();
        }
        return instance;
    }

    public synchronized void saveUserLessonShowOption(int option){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(SHOW_OPTIONS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USER_LESSON_SHOW_OPTION, option);
        editor.apply();
    }

    public synchronized int getUserLessonShowOption(){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(SHOW_OPTIONS_KEY, Context.MODE_PRIVATE);
        return preferences.getInt(USER_LESSON_SHOW_OPTION, NO_SHOW_OPTION);
    }
}
