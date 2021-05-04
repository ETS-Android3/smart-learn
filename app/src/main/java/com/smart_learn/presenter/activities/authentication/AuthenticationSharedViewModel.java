package com.smart_learn.presenter.activities.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.presenter.activities.authentication.helpers.LoginForm;

import lombok.Getter;

/** This SharedViewModel will be used to share login credentials between
 * LoginFragment and RegisterFragment for a better user experience. */
public class AuthenticationSharedViewModel extends AndroidViewModel {

    @Getter
    private final MutableLiveData<LoginForm> liveLoginForm = new MutableLiveData<>(new LoginForm(null, null));
    private final MutableLiveData<String> liveToolbarTitle = new MutableLiveData<>("");

    public AuthenticationSharedViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getLiveToolbarTitle() {return liveToolbarTitle;}

    public void setLiveToolbarTitle(String name){
        liveToolbarTitle.setValue(name);
    }
}
