package com.smart_learn.core.services;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.data.repository.WordRepository;

import java.util.List;

public class WordService extends BasicRoomService<Word> {

    private final WordRepository wordRepository;

    public WordService(Application application){
        wordRepository = new WordRepository(application);

        // set super repository
        super.basicRoomRepository = wordRepository;
    }

    public Word getSampleWord(String word){
        return wordRepository.getSampleWord(word);
    }

    public LiveData<List<Word>> getAllLiveWords(){
        return wordRepository.getAllLiveWords();
    }

    boolean checkIfWordExist(String word){
        return wordRepository.checkIfWordExist(word);
    }

    boolean checkIfWordExist(String word, long lessonId){
        return wordRepository.checkIfWordExist(word,lessonId);
    }

}
