package com.licenta.smart_learn.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.licenta.smart_learn.remote.api.HttpRequestService;

import java.util.concurrent.atomic.AtomicInteger;

public class CurrentConfig {

    @SuppressLint("StaticFieldLeak")
    private static CurrentConfig currentConfigInstance = null;

    // current context configuration
    public Context currentContext;
    public Activity currentActivity;

    // if some http request has an unexpected error
    public AtomicInteger requestErrorCode = new AtomicInteger(HttpRequestService.NO_REQUEST_MADE);

    //
    //private AtomicBoolean userLoggedIn = new AtomicBoolean(false);

    private CurrentConfig() {}

    public static CurrentConfig getCurrentConfigInstance(){
        if(currentConfigInstance == null){
            currentConfigInstance = new CurrentConfig();
        }
        return currentConfigInstance;
    }

    /** Very important. When application enters in a new activity configuration must be recreated. */
    public void makeNewConfig(Activity activity){
        currentActivity = activity;
        currentContext = activity.getApplicationContext();
        requestErrorCode.set(HttpRequestService.NO_REQUEST_MADE);
    }

}
