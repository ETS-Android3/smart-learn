package com.smart_learn.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.models.room.dao.WordDao;
import com.smart_learn.models.room.db.AppRoomDatabase;
import com.smart_learn.models.room.entities.Word;

import java.util.List;

public class WordRepository extends BasicRoomRepository<Word> {

    private final WordDao wordDao;
    private final LiveData<List<Word>> sampleLiveWordList;

    public WordRepository(Application application) {
        // no need for db instance in class because communication will be made using dao interface
        AppRoomDatabase db = AppRoomDatabase.getDatabaseInstance(application);

        // this is used to communicate with db
        wordDao = db.wordDao();

        // set dao in super class
        super.basicDao = wordDao;

        // one query is enough because LiveData is made i.e. to be automatically notified by room
        // when changes are made in db
        sampleLiveWordList = wordDao.getAllLiveWords();
    }

    public Word getSampleWord(String word){
        return wordDao.getSampleWord(word);
    }

    public LiveData<List<Word>> getAllLiveWords(){
        return sampleLiveWordList;
    }

    /** Check if Word already exists in database. */
    public boolean checkIfWordExist(String word){
        return wordDao.getSampleWord(word) == null;
    }

    /** Check if Word already exists in a specific lesson. */
    public boolean checkIfWordExist(String word, long lessonId){
        return wordDao.getSampleWord(word,lessonId) == null;
    }
}