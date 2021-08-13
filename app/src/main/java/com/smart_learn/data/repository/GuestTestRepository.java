package com.smart_learn.data.repository;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.RoomTestDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.data.room.repository.BasicRoomRepository;
import com.smart_learn.presenter.helpers.ApplicationController;

import java.util.List;

public class GuestTestRepository extends BasicRoomRepository<RoomTest, RoomTestDao> {

    private static GuestTestRepository instance;

    private GuestTestRepository() {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(ApplicationController.getInstance()).roomTestDao());
    }

    public static GuestTestRepository getInstance() {
        if(instance == null){
            instance = new GuestTestRepository();
        }
        return instance;
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenScheduledTests(){
        return dao.getAllLiveNotHiddenScheduledTests();
    }

    public List<RoomTest> getAllNotHiddenScheduledActiveTests(){
        return dao.getAllNotHiddenScheduledActiveTests();
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenNonScheduledTests() {
        return dao.getAllLiveNotHiddenNonScheduledTests();
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenInProgressTests(){
        return dao.getAllLiveNotHiddenInProgressTests();
    }

    public LiveData<List<RoomTest>> getAllLiveNotHiddenFinishedTests(){
        return dao.getAllLiveNotHiddenFinishedTests();
    }

    public LiveData<RoomTest> getLiveTest(int testId){
        return dao.getLiveTest(testId);
    }

    public RoomTest getTest(int testId){
        return dao.getTest(testId);
    }

    public LiveData<Integer> getLiveNumberOfNotHiddenNonScheduledTests(){
        return dao.getLiveNumberOfNotHiddenNonScheduledTests();
    }

    public LiveData<Integer> getLiveNumberOfInProgressTests(){
        return dao.getLiveNumberOfNotHiddenInProgressTests();
    }

    public LiveData<Integer> getLiveNumberOfFinishedTests(){
        return dao.getLiveNumberOfNotHiddenFinishedTests();
    }

    public Integer getNumberOfNonScheduledTests(){
        return dao.getNumberOfNonScheduledTests();
    }

    public Integer getNumberOfScheduledTests(){
        return dao.getNumberOfScheduledTests();
    }

    public LiveData<Float> getLiveSuccessRate(){
        return dao.getLiveSuccessRate();
    }

}
