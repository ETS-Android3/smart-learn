package com.smart_learn.presenter.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.data.models.room.entities.helpers.LessonEntrance;

import java.util.List;

public class LessonEntriesRVViewModel extends BasicLessonEntranceViewModel {

    // By default, the empty mode will be set to false in order to avoid showing him before
    // the first loading of the adapter list.
    private final MutableLiveData<Boolean> liveEmptyMode = new MutableLiveData<>(false);

    public LessonEntriesRVViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getLiveEmptyMode() {
        return liveEmptyMode;
    }

    public List<Word> getCurrentLessonSampleWords(long currentLessonId) {
        return wordService.getCurrentLessonSampleWords(currentLessonId);
    }

    public LiveData<List<Word>> getCurrentLessonLiveWords(long currentLessonId) {
        return wordService.getCurrentLessonLiveWords(currentLessonId);
    }

    public LiveData<Word> getSampleLiveWord(long wordId) {
        return wordService.getSampleLiveWord(wordId);
    }

    public LiveData<Integer> getLiveSelectedItemsCount(long lessonId){
        return wordService.getLiveSelectedItemsCount(lessonId);
    }

    public LiveData<Integer> getLiveItemsNumber(long lessonId){
        return wordService.getLiveItemsNumber(lessonId);
    }

    public void update(@NonNull Word word) { wordService.update(word); }

    public void delete(@NonNull  Word word) { wordService.delete(word); }

    public void updateSelectAll(boolean isSelected, long lessonId){ wordService.updateSelectAll(isSelected,lessonId); }

    public void deleteSelectedItems(long lessonId){ wordService.deleteSelectedItems(lessonId); }

    /** This is a helper to show specific element when recycler view list is empty and other
     *  elements when recycler view list is NOT empty
     *
     * @param currentList current list of items from recycler view
     *  */
    public void checkEmptyMode(@NonNull List<LessonEntrance> currentList) {
        if(currentList.isEmpty()){
            liveEmptyMode.setValue(true);
            return;
        }
        liveEmptyMode.setValue(false);
    }

}
