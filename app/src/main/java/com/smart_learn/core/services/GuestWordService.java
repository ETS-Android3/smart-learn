package com.smart_learn.core.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.exceptions.TODO;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.repository.GuestWordRepository;
import com.smart_learn.data.room.entities.Word;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuestWordService extends BasicRoomService<Word, GuestWordRepository> {

    private static GuestWordService instance;

    private GuestWordService() {
        super(GuestWordRepository.getInstance());
    }

    public static GuestWordService getInstance() {
        if(instance == null){
            instance = new GuestWordService();
        }
        return instance;
    }

    public Word getSampleWord(String word){
        return repositoryInstance.getSampleWord(word);
    }

    public LiveData<List<Word>> getCurrentLessonLiveWords(int currentLessonId){
        return repositoryInstance.getCurrentLessonLiveWords(currentLessonId);
    }

    @NonNull
    public List<Word> getCurrentLessonSampleWords(int currentLessonId){
        List<Word> tmp = repositoryInstance.getCurrentLessonLiveWords(currentLessonId).getValue();
        if(tmp == null){
            return new ArrayList<>();
        }
        return tmp;
    }

    public LiveData<Word> getSampleLiveWord(int wordId) {
        return repositoryInstance.getSampleLiveWord(wordId);
    }

    boolean checkIfWordExist(String word){
        return repositoryInstance.checkIfWordExist(word);
    }

    boolean checkIfWordExist(String word, int lessonId){
        return repositoryInstance.checkIfWordExist(word,lessonId);
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
            word.getBasicInfo().setModifiedAt(System.currentTimeMillis());
            update(word, null);
            return responseInfo;
        }

        // FIXME: fix adding a word
        //Word newWord = new Word(System.currentTimeMillis(), System.currentTimeMillis(), word.getFkLessonId(),
          //      false, word.getTranslation(), word.getWord());
        //insert(newWord);

        return responseInfo;
    }

    public void deleteSelectedItems(int lessonId){ repositoryInstance.deleteSelectedItems(lessonId); }

    public void updateSelectAll(boolean isSelected, int lessonId){ repositoryInstance.updateSelectAll(isSelected,lessonId); }

    public LiveData<Integer> getLiveSelectedItemsCount(int lessonId){ return repositoryInstance.getLiveSelectedItemsCount(lessonId); }

    public LiveData<Integer> getLiveItemsNumber(int lessonId){ return repositoryInstance.getLiveItemsNumber(lessonId); }

    public LiveData<Integer> getLiveNumberOfWords(){
        return repositoryInstance.getLiveNumberOfWords();
    }

    public LiveData<Integer> getLiveNumberOfWordsForSpecificLesson(int lessonId){
        return repositoryInstance.getLiveNumberOfWordsForSpecificLesson(lessonId);
    }

    public int getNumberOfWordsForSpecificLesson(int lessonId){
        return repositoryInstance.getNumberOfWordsForSpecificLesson(lessonId);
    }

    @Override
    protected boolean isItemValid(Word item) {
        throw new TODO("not implemented");
    }
}
