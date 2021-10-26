package com.smart_learn.presenter.common.fragments.helpers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * The main fragment from which all the fragments of the application must be extended.
 *
 * @param <VM> A ViewModel class that extends BasicAndroidViewModel that will be used by the fragment
 *             as main view model.
 * */
public abstract class BasicFragment <VM extends BasicAndroidViewModel> extends Fragment {

    public static int STANDARD_PROGRESS_DIALOG_TIMEOUT = 10000;

    private ProgressDialog progressDialog;
    // Used to close a progress dialog after some time if was not previously closed.
    private CountDownTimer progressDialogCountDownTimer;

    @Getter
    protected VM viewModel;

    // used to check is fragment is created
    protected boolean isCreated = false;

    /**
     *  Used to instantiate ViewModel. ViewModelProvider needs something like 'ViewModelClassName.class'.
     *
     * @return ViewModelClassName.class
     */
    @NonNull
    @NotNull
    protected abstract Class <VM> getModelClassForViewModel();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
        isCreated = true;
    }

    protected void setViewModel(){
        // Set view model (getModelClassForViewModel() is something like 'ViewModelClassName.class').
        viewModel = new ViewModelProvider(this).get(getModelClassForViewModel());

        // set observers
        viewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                PresenterUtilities.General.showShortToastMessage(requireContext(), s);
            }
        });
    }


    /**
     * Will create and show a ProgressDialog which will be canceled automatically
     * BasicFragment.STANDARD_PROGRESS_DIALOG_TIMEOUT, if was not already canceled.
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
        progressDialog = ProgressDialog.show(this.requireContext(), title, message, true);

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

    protected void showMessage(int id){
        PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(id));
    }

}