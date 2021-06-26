package com.smart_learn.presenter.activities.authentication.fragments.intro;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

public class IntroViewModel extends BasicAndroidViewModel {

    private final MutableLiveData<String> btnEmailOption = new MutableLiveData<>("");
    private final MutableLiveData<String> btnGoogleOption = new MutableLiveData<>("");
    private final MutableLiveData<String> btnChangeOption = new MutableLiveData<>("");
    private final MutableLiveData<String> tv1 = new MutableLiveData<>("");

    public IntroViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    /** Set text attributes for fields from BottomSheet included in IntroFragment. */
    public void setOptions(Fragment fragment, boolean login){
        if(login){
            btnEmailOption.setValue(fragment.getString(R.string.continue_with_email));
            btnGoogleOption.setValue(fragment.getString(R.string.sign_in_with_google));
            tv1.setValue(fragment.getString(R.string.not_have_an_account));
            btnChangeOption.setValue(fragment.getString(R.string.register));
            return;
        }

        btnEmailOption.setValue(fragment.getString(R.string.register_with_email));
        btnGoogleOption.setValue(fragment.getString(R.string.sign_in_with_google));
        tv1.setValue(fragment.getString(R.string.already_have_an_account));
        btnChangeOption.setValue(fragment.getString(R.string.login));
    }

    public LiveData<String> getBtnEmailOption(){ return btnEmailOption; }
    public LiveData<String> getBtnGoogleOption(){ return btnGoogleOption; }
    public LiveData<String> getBtnChangeOption(){ return btnChangeOption; }
    public LiveData<String> getTv1Option(){ return tv1; }
}
