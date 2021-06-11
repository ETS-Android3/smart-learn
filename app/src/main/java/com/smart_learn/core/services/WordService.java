package com.smart_learn.core.services;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.data.repository.WordRepository;

import java.util.ArrayList;
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

    public LiveData<List<Word>> getCurrentLessonLiveWords(long currentLessonId){
        return wordRepository.getCurrentLessonLiveWords(currentLessonId);
    }

    @NonNull
    public List<Word> getCurrentLessonSampleWords(long currentLessonId){
        List<Word> tmp = wordRepository.getCurrentLessonLiveWords(currentLessonId).getValue();
        if(tmp == null){
            return new ArrayList<>();
        }
        return tmp;
    }

    public LiveData<Word> getSampleLiveWord(long wordId) {
        return wordRepository.getSampleLiveWord(wordId);
    }

    boolean checkIfWordExist(String word){
        return wordRepository.checkIfWordExist(word);
    }

    boolean checkIfWordExist(String word, long lessonId){
        return wordRepository.checkIfWordExist(word,lessonId);
    }

    private ResponseInfo wordDetailsCheck(Word word){

        if(word.getWord().isEmpty()){
            return new ResponseInfo.Builder()
                            .setIsOk(false)
                            .setInfo("Enter a word")
                            .build();
        }

        /* TODO: check word length
         if(word.length > DatabaseSchema.EntriesTable.DIMENSION_COLUMN_WORD){
        Toast.makeText(activity, "This word is too big.",Toast.LENGTH_LONG).show()
        return false
        }

        if(phonetic.length > DatabaseSchema.EntriesTable.DIMENSION_COLUMN_PHONETIC){
        Toast.makeText(activity, "This phonetic translation is too big.",
            Toast.LENGTH_LONG).show()
        return false
        }

         */

        /*
        // TODO: check to see if word exist in all database not only in one notebook
        // add word only if this does not exists in current notebook
        if (checkIfWordExist(word.getWord())) {
            return new ResponseInfo(false,"Word " + word.getWord() + " already exists in this notebook. Choose other word");
        }
         */

        // TODO: check if translation exists in notebook

        return new ResponseInfo.Builder()
                        .setIsOk(true)
                        .build();
    }

    /** Try to add new Word using results from notebook entrance dialog */
    public ResponseInfo tryToAddOrUpdateNewWord(@NonNull Word word, boolean update){
        if(word == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[tryToAddOrUpdateNewWord] word is null");
            return new ResponseInfo.Builder()
                    .setIsOk(false)
                    .setInfo("[Internal error. The modification was not saved.]")
                    .build();
        }

        // make some general checks
        ResponseInfo responseInfo = wordDetailsCheck(word);
        if(!responseInfo.isOk()){
            return responseInfo;
        }

        // here word is valid
        if(update){
            word.setModifiedAt(System.currentTimeMillis());
            update(word);
            return responseInfo;
        }

        Word newWord = new Word(System.currentTimeMillis(), System.currentTimeMillis(), word.getFkLessonId(),
                false, word.getTranslation(), word.getWord());
        insert(newWord);

        return responseInfo;
    }

    public void deleteSelectedItems(long lessonId){ wordRepository.deleteSelectedItems(lessonId); }

    public void updateSelectAll(boolean isSelected, long lessonId){ wordRepository.updateSelectAll(isSelected,lessonId); }

    public LiveData<Integer> getLiveSelectedItemsCount(long lessonId){ return wordRepository.getLiveSelectedItemsCount(lessonId); }

    public LiveData<Integer> getLiveItemsNumber(long lessonId){ return wordRepository.getLiveItemsNumber(lessonId); }

}
