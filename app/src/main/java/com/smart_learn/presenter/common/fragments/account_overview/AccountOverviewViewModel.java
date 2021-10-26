package com.smart_learn.presenter.common.fragments.account_overview;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import lombok.Getter;

/**
 * From this ViewModel, UserAccountOverviewViewModel and GuestAccountOverviewViewModel will be
 * extended in order to obtain a better control flow of the navigation.
 * */
@Getter
public abstract class AccountOverviewViewModel extends BasicAndroidViewModel {

    protected LiveData<Integer> liveNumberOfLessons;
    protected LiveData<Integer> liveNumberOfWords;
    protected LiveData<Integer> liveNumberOfExpressions;
    protected LiveData<Integer> liveNumberOfLocalActiveTests;
    protected LiveData<Integer> liveNumberOfLocalFinishedTests;
    protected LiveData<Integer> liveNumberOfOnlineActiveTests;
    protected LiveData<Integer> liveNumberOfOnlineFinishedTests;
    protected LiveData<Float> liveSuccessRate;
    protected MutableLiveData<String> liveSuccessRateDescription;

    protected String userHelloMessage;

    public AccountOverviewViewModel(@NonNull @NotNull Application application) {
        super(application);
        liveSuccessRateDescription = new MutableLiveData<>("");
    }

    public void setLiveSuccessRateDescription(float liveSuccessRate) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        liveSuccessRateDescription.setValue(decimalFormat.format(liveSuccessRate) + " %");
    }
}
