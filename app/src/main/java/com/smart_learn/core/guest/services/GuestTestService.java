package com.smart_learn.core.guest.services;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.smart_learn.core.guest.services.helpers.BasicRoomService;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.guest.repository.GuestTestRepository;
import com.smart_learn.data.guest.room.entitites.RoomTest;

import java.util.List;

import timber.log.Timber;

public class GuestTestService extends BasicRoomService<RoomTest, GuestTestRepository> {

    private static GuestTestService instance;

    private GuestTestService() {
        super(GuestTestRepository.getInstance());
    }

    public static GuestTestService getInstance() {
        if(instance == null){
            instance = new GuestTestService();
        }
        return instance;
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenScheduledTests(){
        return repositoryInstance.getAllLiveNotHiddenScheduledTests();
    }

    public List<RoomTest> getAllNotHiddenScheduledActiveTests(){
        return repositoryInstance.getAllNotHiddenScheduledActiveTests();
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenNonScheduledTests() {
        return repositoryInstance.getAllLiveNotHiddenNonScheduledTests();
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenInProgressTests(){
        return repositoryInstance.getAllLiveNotHiddenInProgressTests();
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenFinishedTests(){
        return repositoryInstance.getAllLiveNotHiddenFinishedTests();
    }

    public LiveData<RoomTest> getLiveTest(int testId){
        return repositoryInstance.getLiveTest(testId);
    }

    public RoomTest getTest(int testId){
        return repositoryInstance.getTest(testId);
    }

    public LiveData<Integer> getLiveNumberOfNotHiddenNonScheduledTests(){
        return repositoryInstance.getLiveNumberOfNotHiddenNonScheduledTests();
    }

    public LiveData<Integer> getLiveNumberOfInProgressTests(){
        return repositoryInstance.getLiveNumberOfInProgressTests();
    }

    public LiveData<Integer> getLiveNumberOfFinishedTests(){
        return repositoryInstance.getLiveNumberOfFinishedTests();
    }

    public Integer getNumberOfNonScheduledTests(){
        return repositoryInstance.getNumberOfNonScheduledTests();
    }

    public Integer getNumberOfScheduledTests(){
        return repositoryInstance.getNumberOfScheduledTests();
    }

    public LiveData<Float> getLiveSuccessRate(){
        return repositoryInstance.getLiveSuccessRate();
    }

    @Override
    public boolean isItemValid(RoomTest item) {
        if(item == null){
            Timber.w("item is null");
            return false;
        }

        if (item.getBasicInfo() == null){
            Timber.w("BasicInfo must not be null");
            return false;
        }

        if(item.getDaysStatus().size() != Test.NR_OF_WEEK_DAYS){
            Timber.w("days size [" + item.getDaysStatus().size() + "] is not equal with [" + Test.NR_OF_WEEK_DAYS + "]");
            return false;
        }

        if(TextUtils.isEmpty(item.getTestName())){
            Timber.w("test name must not be null or empty");
            return false;
        }

        return true;
    }
}
