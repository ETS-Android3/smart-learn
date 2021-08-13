package com.smart_learn.core.services.word;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.word.GuestWordRepository;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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

    @NonNull
    public List<Word> getLessonWords(int lessonId){
        List<Word> tmp = repositoryInstance.getLessonWords(lessonId);
        return tmp == null ? new ArrayList<>() : tmp;
    }

    public LiveData<Word> getSampleLiveWord(int wordId) {
        return repositoryInstance.getSampleLiveWord(wordId);
    }

    public Word getSampleWord(int wordId) {
        return repositoryInstance.getSampleWord(wordId);
    }

    public void deleteSelectedItems(int lessonId){ repositoryInstance.deleteSelectedItems(lessonId); }

    public void updateSelectAll(boolean isSelected, int lessonId){ repositoryInstance.updateSelectAll(isSelected,lessonId); }

    public LiveData<Integer> getLiveSelectedItemsCount(int lessonId){ return repositoryInstance.getLiveSelectedItemsCount(lessonId); }

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
        // TODO: refactor this method

        if(item == null){
            Timber.w("item is null");
            return false;
        }

        // check word
        if (item.getWord() == null || item.getWord().isEmpty()) {
            Timber.w("word is null or empty");
            return false;
        }

        if (item.getWord().length() > DataUtilities.Limits.MAX_WORD) {
            Timber.w("word is too big [" + item.getWord().length() + "]");
            return false;
        }

        // check phonetic
        if(item.getPhonetic() == null){
            item.setPhonetic("");
        }

        if(item.getPhonetic().length() > DataUtilities.Limits.MAX_WORD_PHONETIC){
            Timber.w("phonetic is too big [" + item.getPhonetic().length() + "]");
            return false;
        }

        // check language
        if(item.getLanguage() == null){
            item.setLanguage("");
        }

        if(item.getLanguage().length() > DataUtilities.Limits.MAX_LANGUAGE){
            Timber.w("language is too big [" + item.getLanguage().length() + "]");
            return false;
        }

        // check notes
        if(item.getNotes() == null){
            item.setNotes("");
        }

        if(item.getNotes().length() > DataUtilities.Limits.MAX_NOTES){
            Timber.w("notes is too big [" + item.getNotes().length() + "]");
            return false;
        }


        // check translations
        if(item.getTranslations() == null){
            item.setTranslations(new ArrayList<>());
        }

        ArrayList<Translation> list = item.getTranslations();
        for(Translation translation : list){
            // check translation
            if(translation.getTranslation() == null){
                translation.setTranslation("");
            }

            if(translation.getTranslation().length() > DataUtilities.Limits.MAX_WORD_TRANSLATION){
                Timber.w("translation is too big [" + translation.getTranslation().length() + "]");
                return false;
            }

            // check language
            if(translation.getLanguage() == null){
                translation.setLanguage("");
            }

            if(translation.getLanguage().length() > DataUtilities.Limits.MAX_LANGUAGE){
                Timber.w("language is too big [" + translation.getLanguage().length() + "]");
                return false;
            }

            // check phonetic
            if(translation.getPhonetic() == null){
                translation.setPhonetic("");
            }

            if(translation.getPhonetic().length() > DataUtilities.Limits.MAX_WORD_TRANSLATION_PHONETIC){
                Timber.w("phonetic is too big [" + translation.getPhonetic().length() + "]");
                return false;
            }
        }

        return true;
    }

    public void deleteAll(int lessonId, @Nullable DataCallbacks.General callback) {
        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Values deleted",
                    "Deletion for values failed");
        }

        repositoryInstance.deleteAll(lessonId, callback);
    }
}
