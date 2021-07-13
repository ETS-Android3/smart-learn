package com.smart_learn.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.WordDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Word;

import java.util.List;

public class GuestWordRepository extends BasicRoomRepository<Word> {

    private final WordDao wordDao;
    private LiveData<List<Word>> currentLessonLiveWordList;

    public GuestWordRepository(Application application) {
        // no need for db instance in class because communication will be made using dao interface
        AppRoomDatabase db = AppRoomDatabase.getDatabaseInstance(application);

        // this is used to communicate with db
        wordDao = db.wordDao();

        // set dao in super class
        super.basicDao = wordDao;
    }

    public Word getSampleWord(String word){
        return wordDao.getSampleWord(word);
    }

    public LiveData<List<Word>> getCurrentLessonLiveWords(int currentLessonId){
        if (currentLessonLiveWordList == null){
            // one query is enough because LiveData is made i.e. to be automatically notified by room
            // when changes are made in db
            currentLessonLiveWordList = wordDao.getLessonLiveWords(currentLessonId);
        }
        return currentLessonLiveWordList;
    }

    /** Get a sample LiveData wrapped word based on wordId. */
    public LiveData<Word> getSampleLiveWord(int wordId) {
        return wordDao.getSampleLiveWord(wordId);
    }

    /** Check if Word already exists in database. */
    public boolean checkIfWordExist(String word){
        return wordDao.getSampleWord(word) == null;
    }

    /** Check if Word already exists in a specific notebook. */
    public boolean checkIfWordExist(String word, int lessonId){
        return wordDao.getSampleWord(word,lessonId) == null;
    }

    public void deleteSelectedItems(int lessonId){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            wordDao.deleteSelectedItems(lessonId);
        });
    }

    public void updateSelectAll(boolean isSelected, int lessonId){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            wordDao.updateSelectAll(isSelected, lessonId);
        });
    }

    public LiveData<Integer> getLiveSelectedItemsCount(int lessonId){ return wordDao.getLiveSelectedItemsCount(lessonId); }

    public LiveData<Integer> getLiveItemsNumber(int lessonId){ return wordDao.getLiveItemsNumber(lessonId); }

    public LiveData<Integer> getLiveNumberOfWords(){
        return wordDao.getLiveNumberOfWords();
    }
}