package com.smart_learn.core.services;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.repository.GuestLessonRepository;
import com.smart_learn.data.room.entities.Lesson;

import java.util.ArrayList;
import java.util.List;

public class GuestLessonService extends BasicRoomService<Lesson> {

    private final GuestLessonRepository guestLessonRepository;

    public GuestLessonService(Application application){
        guestLessonRepository = new GuestLessonRepository(application);

        // set super repository
        super.basicRoomRepository = guestLessonRepository;
    }

    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return guestLessonRepository.getSampleLiveLesson(lessonId);
    }

    @NonNull
    public List<Lesson> getAllSampleLesson(){
        List<Lesson> tmp = guestLessonRepository.getAllLiveSampleLessons().getValue();
        if(tmp == null){
            return new ArrayList<>();
        }
        return tmp;
    }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() { return guestLessonRepository.getAllLiveSampleLessons(); }

    public LiveData<Integer> getLiveSelectedItemsCount(){ return guestLessonRepository.getLiveSelectedItemsCount(); }

    public LiveData<Integer> getLiveItemsNumber(){ return guestLessonRepository.getLiveItemsNumber(); }

    public void deleteSelectedItems(){ guestLessonRepository.deleteSelectedItems(); }

    public void updateSelectAll(boolean isSelected){ guestLessonRepository.updateSelectAll(isSelected); }


    public boolean checkIfLessonExist(String lessonName) {
        return guestLessonRepository.checkIfLessonExist(lessonName);
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

        // Add notebook only if this does not exist (should have a unique name).
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

    /** Try to add new notebook using results from notebook dialog */
    public ResponseInfo tryToAddOrUpdateNewLesson(@NonNull Lesson lesson, boolean update){
        if(lesson == null){
            Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[tryToAddOrUpdateNewLesson] notebook is null");
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

        // here notebook is valid
        if(update){
            lesson.getBasicInfo().setModifiedAt(System.currentTimeMillis());
            update(lesson);
            return responseInfo;
        }


        insert(lesson);

        return responseInfo;
    }

    public LiveData<Integer> getLiveNumberOfLessons(){
        return guestLessonRepository.getLiveNumberOfLessons();
    }

}
