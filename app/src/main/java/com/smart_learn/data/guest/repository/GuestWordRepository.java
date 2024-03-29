package com.smart_learn.data.guest.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.guest.room.dao.WordDao;
import com.smart_learn.data.guest.room.db.AppRoomDatabase;
import com.smart_learn.data.guest.room.entitites.Word;
import com.smart_learn.data.guest.repository.helpers.BasicRoomRepository;
import com.smart_learn.core.common.helpers.ApplicationController;

import java.util.List;

public class GuestWordRepository extends BasicRoomRepository<Word, WordDao> {

    private static GuestWordRepository instance;

    private GuestWordRepository() {
        // no need for db instance in class because communication will be made using dao interface
        super(AppRoomDatabase.getDatabaseInstance(ApplicationController.getInstance()).wordDao());
    }

    public static synchronized GuestWordRepository getInstance() {
        if(instance == null){
            instance = new GuestWordRepository();
        }
        return instance;
    }

    public LiveData<List<Word>> getCurrentLessonLiveWords(int currentLessonId){
        return dao.getLessonLiveWords(currentLessonId);
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