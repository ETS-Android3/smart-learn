package com.smart_learn.services;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.models.room.entities.Word;
import com.smart_learn.repository.WordRepository;

import java.util.List;

public class WordService extends BasicRoomService<Word> {

    private static WordService instance;
    private final WordRepository repository;

    private WordService(Application application){
        repository = new WordRepository(application);

        // set super repository
        super.basicRoomRepository = repository;
    }

    public static synchronized WordService getWordServiceInstance(Application application) {
        if (instance == null) {
            instance = new WordService(application);
        }
        return instance;
    }

    public Word getSampleWord(String word){
        return repository.getSampleWord(word);
    }

    public LiveData<List<Word>> getAllLiveWords(){
        return repository.getAllLiveWords();
    }

    boolean checkIfWordExist(String word){
        return repository.checkIfWordExist(word);
    }

    boolean checkIfWordExist(String word, long lessonId){
        return repository.checkIfWordExist(word,lessonId);
    }

}
