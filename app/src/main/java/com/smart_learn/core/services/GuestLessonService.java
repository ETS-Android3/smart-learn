package com.smart_learn.core.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.exceptions.TODO;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.core.services.helpers.BasicRoomService;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.repository.GuestLessonRepository;
import com.smart_learn.data.room.entities.Lesson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuestLessonService extends BasicRoomService<Lesson, GuestLessonRepository> {

    private static GuestLessonService instance;

    private GuestLessonService() {
        super(GuestLessonRepository.getInstance());
    }

    public static GuestLessonService getInstance() {
        if(instance == null){
            instance = new GuestLessonService();
        }
        return instance;
    }

    public LiveData<Lesson> getSampleLiveLesson(int lessonId) {
        return repositoryInstance.getSampleLiveLesson(lessonId);
    }

    @NonNull
    public List<Lesson> getAllSampleLesson(){
        List<Lesson> tmp = repositoryInstance.getAllLiveSampleLessons().getValue();
        if(tmp == null){
            return new ArrayList<>();
        }
        return tmp;
    }

    public LiveData<List<Lesson>> getAllLiveSampleLessons() { return repositoryInstance.getAllLiveSampleLessons(); }

    public LiveData<Integer> getLiveSelectedItemsCount(){ return repositoryInstance.getLiveSelectedItemsCount(); }

    public LiveData<Integer> getLiveItemsNumber(){ return repositoryInstance.getLiveItemsNumber(); }

    public void deleteSelectedItems(){ repositoryInstance.deleteSelectedItems(); }

    public void updateSelectAll(boolean isSelected){ repositoryInstance.updateSelectAll(isSelected); }


    public boolean checkIfLessonExist(String lessonName) {
        return repositoryInstance.checkIfLessonExist(lessonName);
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
            update(lesson, null);
            return responseInfo;
        }


        insert(lesson, null);

        return responseInfo;
    }

    public LiveData<Integer> getLiveNumberOfLessons(){
        return repositoryInstance.getLiveNumberOfLessons();
    }

    @Override
    protected boolean isItemValid(Lesson item) {
        throw new TODO("not implemented");
    }
}
