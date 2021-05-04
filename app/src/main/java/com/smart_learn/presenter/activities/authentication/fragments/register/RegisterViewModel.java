package com.smart_learn.presenter.activities.authentication.fragments.register;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.presenter.activities.authentication.helpers.RegisterForm;
import com.smart_learn.presenter.helpers.BasicAndroidViewModel;

import lombok.Getter;

public class RegisterViewModel extends BasicAndroidViewModel {

    @Getter
    private final MutableLiveData<RegisterForm> liveRegisterForm;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        liveRegisterForm = new MutableLiveData<>(new RegisterForm(null, null, null, null));
    }

    public void register(RegisterForm registerForm){

    }

}
