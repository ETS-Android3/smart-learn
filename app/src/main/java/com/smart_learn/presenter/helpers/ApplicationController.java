package com.smart_learn.presenter.helpers;

import android.app.Application;

import com.smart_learn.BuildConfig;

import timber.log.Timber;

public class ApplicationController extends Application {

    private static ApplicationController applicationController;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationController = this;
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static ApplicationController getInstance() {return applicationController;}
}
