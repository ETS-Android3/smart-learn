package com.smart_learn.presenter.activities.main.fragments.account_overview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.presenter.helpers.fragments.account_overview.AccountOverviewViewModel;

import org.jetbrains.annotations.NotNull;

public class UserAccountOverviewViewModel extends AccountOverviewViewModel {

    public UserAccountOverviewViewModel(@NonNull @NotNull Application application) {
        super(application);

        liveNumberOfLessons =  new MutableLiveData<>(0);
        liveNumberOfWords =  new MutableLiveData<>(0);
        liveNumberOfExpressions =  new MutableLiveData<>(0);
        liveNumberOfLocalActiveTests = new MutableLiveData<>(0);
        liveNumberOfLocalFinishedTests = new MutableLiveData<>(0);
        liveNumberOfOnlineActiveTests = new MutableLiveData<>(0);
        liveNumberOfOnlineFinishedTests = new MutableLiveData<>(0);
        liveSuccessRate = new MutableLiveData<>(0.0f);

        userHelloMessage = application.getResources().getString(R.string.hi) + ", " + UserService.getInstance().getUserDisplayName() + "!";
    }

    protected void setValues(UserDocument userDocument){
        if(userDocument == null){
            return;
        }

        ((MutableLiveData<Integer>)liveNumberOfLessons).setValue(Math.toIntExact(userDocument.getNrOfLessons()));
        ((MutableLiveData<Integer>)liveNumberOfWords).setValue(Math.toIntExact(userDocument.getNrOfWords()));
        ((MutableLiveData<Integer>)liveNumberOfExpressions).setValue(Math.toIntExact(userDocument.getNrOfExpressions()));
        ((MutableLiveData<Integer>)liveNumberOfLocalActiveTests).setValue(Math.toIntExact(userDocument.getNrOfLocalUnscheduledInProgressTests()));
        ((MutableLiveData<Integer>)liveNumberOfLocalFinishedTests).setValue(Math.toIntExact(userDocument.getNrOfLocalUnscheduledFinishedTests()));
        ((MutableLiveData<Integer>)liveNumberOfOnlineActiveTests).setValue(Math.toIntExact(userDocument.getNrOfOnlineInProgressTests()));
        ((MutableLiveData<Integer>)liveNumberOfOnlineFinishedTests).setValue(Math.toIntExact(userDocument.getNrOfOnlineFinishedTests()));

        int totalFinishedTests = Math.toIntExact(userDocument.getNrOfLocalUnscheduledFinishedTests() + userDocument.getNrOfOnlineFinishedTests());
        float successRate = 0.0f;
        if(totalFinishedTests != 0){
            successRate = (float) userDocument.getTotalSuccessRate() / (float) totalFinishedTests;
        }
        ((MutableLiveData<Float>)liveSuccessRate).setValue(successRate);
    }
}
