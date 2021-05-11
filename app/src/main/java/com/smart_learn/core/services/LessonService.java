package com.smart_learn.core.services;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.data.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;

public class LessonService extends BasicRoomService<Lesson> {

    private final LessonRepository lessonRepository;

    public LessonService(Application application){
        lessonRepository = new LessonRepository(application);

        // set super repository
        super.basicRoomRepository = lessonRepository;
    }

    public LiveData<Lesson> getSampleLiveLesson(long lessonId) {
        return lessonRepository.getSampleLiveLesson(lessonId);
    }

    @NonNull
    public List<Lesson> getAllSampleLesson(){
        List<Lesson> tmp = lessonRepository.getAllLiveSampleLessons().getValue();
        if(tmp == null){
            return new ArrayList<>();
        }
        return tmp;
    }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() { return lessonRepository.getAllLiveSampleLessons(); }

    public LiveData<Integer> getLiveSelectedItemsCount(){ return lessonRepository.getLiveSelectedItemsCount(); }

    public LiveData<Integer> getLiveItemsNumber(){ return lessonRepository.getLiveItemsNumber(); }

    public void deleteSelectedItems(){ lessonRepository.deleteSelectedItems(); }

    public void updateSelectAll(boolean isSelected){ lessonRepository.updateSelectAll(isSelected); }


    public boolean checkIfLessonExist(String lessonName) {
        return lessonRepository.checkIfLessonExist(lessonName);
    }

    private ResponseInfo lessonDetailsCheck(Lesson lesson){

        if (lesson.getName().isEmpty()) {
            return new ResponseInfo.Builder()
                    .setIsOk(false)
                    .setInfo("Enter a name")
                    .build();
        }

        /* TODO: check name length
        if (lessonName.length() > DatabaseSchema.LessonsTable.DIMENSION_COLUMN_NAME) {
            liveToastMessage.setValue("This name is too big. Choose a shorter name.");
            return false;
        }
         */

        // Add lesson only if this does not exist (should have a unique name).
        if (checkIfLessonExist(lesson.getName())) {
            return new ResponseInfo.Builder()
                    .setIsOk(false)
                    .setInfo("Lesson " + lesson.getName() + " already exists. Choose other name")
                    .build();
        }
        return new ResponseInfo.Builder()
                .setIsOk(true)
                .build();
    }

    /** Try to add new lesson using results from lesson dialog */
    public ResponseInfo tryToAddOrUpdateNewLesson(@NonNull Lesson lesson, boolean update){
        if(lesson == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[tryToAddOrUpdateNewLesson] lesson is null");
            return new ResponseInfo.Builder()
                    .setIsOk(false)
                    .setInfo("[Internal error. The modification was not saved.]")
                    .build();
        }

        // make some general checks
        ResponseInfo responseInfo = lessonDetailsCheck(lesson);
        if(!responseInfo.isOk()){
            return responseInfo;
        }

        // here lesson is valid
        if(update){
            lesson.setModifiedAt(System.currentTimeMillis());
            update(lesson);
            return responseInfo;
        }

        Lesson newLesson = new Lesson(lesson.getName(), System.currentTimeMillis(), System.currentTimeMillis(),false);
        insert(newLesson);

        return responseInfo;
    }

}
