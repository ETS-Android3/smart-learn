package com.smart_learn.data.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.smart_learn.data.helpers.DataCallbacks;
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

    public LiveData<List<Word>> getCurrentLessonLiveWords(int currentLessonId){
        if (currentLessonLiveWordList == null){
            // one query is enough because LiveData is made i.e. to be automatically notified by room
            // when changes are made in db
            currentLessonLiveWordList = dao.getLessonLiveWords(currentLessonId);
        }
        return currentLessonLiveWordList;
    }

    public List<Word> getLessonWords(int lessonId){
        return dao.getLessonWords(lessonId);
    }

    public LiveData<Word> getSampleLiveWord(int wordId) {
        return dao.getSampleLiveWord(wordId);
    }

    public Word getSampleWord(int wordId) {
        return dao.getSampleWord(wordId);
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

    public LiveData<Integer> getLiveNumberOfWords(){
        return dao.getLiveNumberOfWords();
    }

    public LiveData<Integer> getLiveNumberOfWordsForSpecificLesson(int lessonId){
        return dao.getLiveNumberOfWordsForSpecificLesson(lessonId);
    }

    public int getNumberOfWordsForSpecificLesson(int lessonId){
        return dao.getNumberOfWordsForSpecificLesson(lessonId);
    }

    public void deleteAll(int lessonId, @Nullable DataCallbacks.General callback) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            dao.deleteAll(lessonId);
            if(callback == null){
                return;
            }
            callback.onSuccess();
        });
    }
}