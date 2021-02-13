package com.smart_learn.core.services;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.data.repository.WordRepository;

import java.util.List;

public class WordService extends BasicRoomService<Word> {

    private final WordRepository repository;

    public WordService(Application application){
        repository = new WordRepository(application);

        // set super repository
        super.basicRoomRepository = repository;
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
