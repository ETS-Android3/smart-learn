package com.smart_learn.data.repository;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.room.dao.WordDao;
import com.smart_learn.data.room.db.AppRoomDatabase;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.repository.BasicRoomRepository;
import com.smart_learn.presenter.helpers.ApplicationController;

import java.util.List;

public class GuestWordRepository extends BasicRoomRepository<Word, WordDao> {

    private static GuestWordRepository instance;

    private LiveData<List<Word>> currentLessonLiveWordList;

    private GuestWordRepository() {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(ApplicationController.getInstance()).wordDao());
    }

    public static GuestWordRepository getInstance() {
        if(instance == null){
            instance = new GuestWordRepository();
        }
        return instance;
    }

    public Word getSampleWord(String word){
        return dao.getSampleWord(word);
    }

    public LiveData<List<Word>> getCurrentLessonLiveWords(int currentLessonId){
        if (currentLessonLiveWordList == null){
            // one query is enough because LiveData is made i.e. to be automatically notified by room
            // when changes are made in db
            currentLessonLiveWordList = dao.getLessonLiveWords(currentLessonId);
        }
        return currentLessonLiveWordList;
    }

    /** Get a sample LiveData wrapped word based on wordId. */
    public LiveData<Word> getSampleLiveWord(int wordId) {
        return dao.getSampleLiveWord(wordId);
    }

    /** Check if Word already exists in database. */
    public boolean checkIfWordExist(String word){
        return dao.getSampleWord(word) == null;
    }

    /** Check if Word already exists in a specific notebook. */
    public boolean checkIfWordExist(String word, int lessonId){
        return dao.getSampleWord(word,lessonId) == null;
    }

    public void deleteSelectedItems(int lessonId){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.deleteSelectedItems(lessonId);
        });
    }

    public void updateSelectAll(boolean isSelected, int lessonId){
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.updateSelectAll(isSelected, lessonId);
        });
    }

    public LiveData<Integer> getLiveSelectedItemsCount(int lessonId){ return dao.getLiveSelectedItemsCount(lessonId); }

    public LiveData<Integer> getLiveItemsNumber(int lessonId){ return dao.getLiveItemsNumber(lessonId); }

    public LiveData<Integer> getLiveNumberOfWords(){
        return dao.getLiveNumberOfWords();
    }

    public LiveData<Integer> getLiveNumberOfWordsForSpecificLesson(int lessonId){
        return dao.getLiveNumberOfWordsForSpecificLesson(lessonId);
    }

    public int getNumberOfWordsForSpecificLesson(int lessonId){
        return dao.getNumberOfWordsForSpecificLesson(lessonId);
    }
}