package com.smart_learn.core.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.smart_learn.presenter.helpers.ApplicationController;

import java.util.Locale;

public class SettingsService {

    public static final int NO_SHOW_OPTION = -1;
    private static final String SHOW_OPTIONS_KEY = "SHOW_OPTIONS_KEY";
    private static final String USER_LESSON_SHOW_OPTION = "USER_LESSON_SHOW_OPTION";
    private static final String GUEST_TEST_FILTER_OPTION = "GUEST_TEST_FILTER_OPTION";
    private static final String USER_TEST_FILTER_OPTION = "USER_TEST_FILTER_OPTION";

    private static final String LANGUAGE_KEY = "LANGUAGE_KEY";
    private static final String LANGUAGE_OPTION = "LANGUAGE_OPTION";

    public interface Languages {
        interface Options{
            // default is English
            int DEFAULT = 0;
            int ROMANIAN = 1;
        }
        interface Codes {
            String DEFAULT = "en";
            String ROMANIAN = "ro";
        }
    }

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

    public synchronized void saveGuestTestFilterOption(int option){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(SHOW_OPTIONS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(GUEST_TEST_FILTER_OPTION, option);
        editor.apply();
    }

    public synchronized int getGuestTestFilterOption(){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(SHOW_OPTIONS_KEY, Context.MODE_PRIVATE);
        return preferences.getInt(GUEST_TEST_FILTER_OPTION, NO_SHOW_OPTION);
    }

    public synchronized void saveUserTestFilterOption(int option){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(SHOW_OPTIONS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(USER_TEST_FILTER_OPTION, option);
        editor.apply();
    }

    public synchronized int getUserTestFilterOption(){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(SHOW_OPTIONS_KEY, Context.MODE_PRIVATE);
        return preferences.getInt(USER_TEST_FILTER_OPTION, NO_SHOW_OPTION);
    }

    public synchronized void saveLanguageOption(int language){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(LANGUAGE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(LANGUAGE_OPTION, language);
        editor.apply();
    }

    public synchronized int getLanguageOption(){
        SharedPreferences preferences = ApplicationController.getInstance().getSharedPreferences(LANGUAGE_KEY, Context.MODE_PRIVATE);
        return preferences.getInt(LANGUAGE_OPTION, Languages.Options.DEFAULT);
    }

    /**
     * Use this in order to load language in app system, in order to show values in the selected
     * language.
     *
     * @param baseContext Context used to update configuration.
     * */
    public void loadLanguageConfiguration(Context baseContext){
        // https://stackoverflow.com/questions/2900023/change-app-language-programmatically-in-android
        // https://www.youtube.com/watch?v=zILw5eV9QBQ&ab_channel=AtifPervaiz
        if(baseContext == null){
            return;
        }
        int languageOption = getLanguageOption();
        String languageCode;
        if (languageOption == SettingsService.Languages.Options.ROMANIAN) {
            languageCode = SettingsService.Languages.Codes.ROMANIAN;
        }
        else {
            languageCode = SettingsService.Languages.Codes.DEFAULT;
        }

        Locale newLocale = new Locale(languageCode);
        Locale currentLocale = baseContext.getResources().getConfiguration().getLocales().get(0);
        if(newLocale.equals(currentLocale)){
            return;
        }

        Locale.setDefault(newLocale);
        Configuration configuration = new Configuration();
        configuration.setLocale(newLocale);
        baseContext.getResources().updateConfiguration(configuration, baseContext.getResources().getDisplayMetrics());
    }
}
