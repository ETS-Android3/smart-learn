package com.smart_learn.presenter.helpers.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * This is the basic class for all ViewModels from this App.
 * So, all ViewModels must inherit this class.
 * */
public abstract class BasicAndroidViewModel extends AndroidViewModel {

    // Leave this with no initial value. If you put an initial value it will trigger setValue().
    protected final MutableLiveData<String> liveToastMessage = new MutableLiveData<>();

    public BasicAndroidViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getLiveToastMessage() {
        return liveToastMessage;
    }
}
