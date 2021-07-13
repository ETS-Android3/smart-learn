package com.smart_learn.presenter.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TestLocalViewModel extends AndroidViewModel {

    //protected final TestService testService;

    // Leave this with no initial value. If you put an initial value it will trigger setValue().
    private final MutableLiveData<String> liveToastMessage = new MutableLiveData<>();

    public TestLocalViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getLiveToastMessage() {
        return liveToastMessage;
    }
}
