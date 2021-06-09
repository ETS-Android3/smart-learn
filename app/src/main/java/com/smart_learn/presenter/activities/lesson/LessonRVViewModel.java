package com.smart_learn.presenter.activities.lesson;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.data.models.room.entities.Lesson;

import java.util.List;

public class LessonRVViewModel extends BasicLessonViewModel {

    // By default, the empty mode will be set to false in order to avoid showing him before
    // the first loading of the adapter list.
    private final MutableLiveData<Boolean> liveEmptyMode = new MutableLiveData<>(false);

    public LessonRVViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getLiveEmptyMode() {
        return liveEmptyMode;
    }

    public LiveData<Lesson> getSampleLiveLesson(long lessonId) {
        return lessonService.getSampleLiveLesson(lessonId);
    }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() { return lessonService.getAllLiveSampleLessons(); }

    public List<Lesson> getAllSampleLesson(){
        return lessonService.getAllSampleLesson();
    }

    public LiveData<Integer> getLiveSelectedItemsCount(){ return lessonService.getLiveSelectedItemsCount(); }

    public LiveData<Integer> getLiveItemsNumber(){ return lessonService.getLiveItemsNumber(); }

    public void update(@NonNull Lesson lesson) { lessonService.update(lesson); }

    public void delete(@NonNull Lesson lesson) { lessonService.delete(lesson); }

    public void updateSelectAll(boolean isSelected){ lessonService.updateSelectAll(isSelected); }

    public void deleteSelectedItems(){ lessonService.deleteSelectedItems(); }

    /** This is a helper to show specific element when recycler view list is empty and other
     *  elements when recycler view list is NOT empty
     *
     * @param currentList current list of items from recycler view
     *  */
    public void checkEmptyMode(@NonNull List<Lesson> currentList) {
        if(currentList.isEmpty()){
            liveEmptyMode.setValue(true);
            return;
        }
        liveEmptyMode.setValue(false);
    }

}
