package com.smart_learn.core.services.test;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.repository.GuestTestRepository;
import com.smart_learn.data.room.entities.RoomTest;

import java.util.List;

import timber.log.Timber;

/**
 * Class and all methods will have package-private access.
 * */
class GuestTestService extends BasicRoomService<RoomTest, GuestTestRepository> {

    private static GuestTestService instance;

    private GuestTestService() {
        super(GuestTestRepository.getInstance());
    }

    protected static GuestTestService getInstance() {
        if(instance == null){
            instance = new GuestTestService();
        }
        return instance;
    }

    protected LiveData<List<RoomTest>> getAllLiveNotHiddenScheduledTests(){
        return repositoryInstance.getAllLiveNotHiddenScheduledTests();
    }

    protected List<RoomTest> getAllNotHiddenScheduledActiveTests(){
        return repositoryInstance.getAllNotHiddenScheduledActiveTests();
    }

    protected LiveData<List<RoomTest>> getAllLiveNotHiddenNonScheduledTests() {
        return repositoryInstance.getAllLiveNotHiddenNonScheduledTests();
    }

    protected LiveData<List<RoomTest>> getAllLiveNotHiddenInProgressTests(){
        return repositoryInstance.getAllLiveNotHiddenInProgressTests();
    }

    protected LiveData<List<RoomTest>> getAllLiveNotHiddenFinishedTests(){
        return repositoryInstance.getAllLiveNotHiddenFinishedTests();
    }

    protected LiveData<RoomTest> getLiveTest(int testId){
        return repositoryInstance.getLiveTest(testId);
    }

    protected RoomTest getTest(int testId){
        return repositoryInstance.getTest(testId);
    }

    protected LiveData<Integer> getLiveNumberOfNotHiddenNonScheduledTests(){
        return repositoryInstance.getLiveNumberOfNotHiddenNonScheduledTests();
    }

    protected Integer getNumberOfNonScheduledTests(){
        return repositoryInstance.getNumberOfNonScheduledTests();
    }

    protected Integer getNumberOfScheduledTests(){
        return repositoryInstance.getNumberOfScheduledTests();
    }

    @Override
    protected boolean isItemValid(RoomTest item) {
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
