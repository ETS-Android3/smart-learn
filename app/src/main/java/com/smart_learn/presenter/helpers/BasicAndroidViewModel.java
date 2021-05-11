package com.smart_learn.presenter.helpers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BasicAndroidViewModel extends AndroidViewModel {

    // Leave this with no initial value. If you put an initial value it will trigger setValue().
    protected final MutableLiveData<String> liveToastMessage = new MutableLiveData<>();

    public BasicAndroidViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getLiveToastMessage() {
        return liveToastMessage;
    }
}
