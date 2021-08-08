package com.smart_learn.presenter.helpers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.core.services.SettingsService;

/**
 * The main activity from which all the activities of the application must be extended.
 * */
public abstract class BasicActivity extends AppCompatActivity {

    public static int STANDARD_PROGRESS_DIALOG_TIMEOUT = 10000;

    private ProgressDialog progressDialog;
    // Used to close a progress dialog after some time if was not previously closed.
    private CountDownTimer progressDialogCountDownTimer;

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


    /**
     * Will create and show a ProgressDialog which will be canceled automatically
     * BasicActivity.STANDARD_PROGRESS_DIALOG_TIMEOUT, if was not already canceled.
     *
     * @param title Title to be set.
     * @param message Message to be set.
     * */
    public void showProgressDialog(String title, String message){
        showProgressDialog(title, message, STANDARD_PROGRESS_DIALOG_TIMEOUT);
    }


    /**
     * Will create and show a ProgressDialog which will be canceled automatically after specified
     * timeout, if was not already canceled.
     *
     * @param title Title to be set.
     * @param message Message to be set.
     * @param timeout Must be in milliseconds. After what time will be canceled automatically if
     *                was not already canceled.
     * */
    public void showProgressDialog(String title, String message, int timeout){
        // first close an existing dialog if any
        closeProgressDialog();

        // then get the new dialog
        progressDialog = ProgressDialog.show(this, title, message, true);

        // Because 'indeterminate' is set to true, use a CountDownTimer and close dialog after
        // specific 'timeout' in order to avoid an infinite dialog. This action is needed to avoid
        // an error which will prevent dialog from closing.
        progressDialogCountDownTimer = new CountDownTimer(timeout, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // no action needed here
            }
            @Override
            public void onFinish() {
                closeProgressDialog();
            }
        }.start();
    }


    /**
     * Will close fragment ProgressDialog and associated CountDownTimer, if was not already closed.
     * */
    public void closeProgressDialog(){
        // Leave this synchronized because this part can be reached from CountDownTimer and other
        // parts of program at the same time, and try to avoid a dismiss() call on a null progressDialog,
        // and cancel() call on null progressDialogCountDownTimer.
        synchronized (this){
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }
            if(progressDialogCountDownTimer != null){
                progressDialogCountDownTimer.cancel();
                progressDialogCountDownTimer = null;
            }
        }
    }
}