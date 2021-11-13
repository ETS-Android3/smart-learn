package com.smart_learn.core.guest.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.smart_learn.core.guest.services.helpers.BasicRoomService;
import com.smart_learn.data.common.helpers.DataUtilities;
import com.smart_learn.data.guest.repository.GuestLessonRepository;
import com.smart_learn.data.guest.room.entitites.Lesson;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GuestLessonService extends BasicRoomService<Lesson, GuestLessonRepository> {

    private static GuestLessonService instance;

    private GuestLessonService() {
        super(GuestLessonRepository.getInstance());
    }

    public static synchronized GuestLessonService getInstance() {
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

    public LiveData<Integer> getLiveNumberOfLessons(){
        return repositoryInstance.getLiveNumberOfLessons();
    }

    protected boolean isItemValid(Lesson item) {
        if(item == null){
            Timber.w("item is null");
            return false;
        }

        if (item.getName() == null || item.getName().isEmpty()) {
            Timber.w("name is null or empty");
            return false;
        }

        if (item.getName().length() > DataUtilities.Limits.MAX_LESSON_NAME) {
            Timber.w("name is too big [" + item.getName().length() + "]");
            return false;
        }

        return true;
    }
}
