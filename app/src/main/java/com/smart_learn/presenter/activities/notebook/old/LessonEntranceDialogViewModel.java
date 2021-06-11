package com.smart_learn.presenter.activities.notebook.old;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.data.models.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.models.room.entities.helpers.Translation;

import lombok.Getter;

public class LessonEntranceDialogViewModel extends BasicLessonEntranceViewModel {

    @Getter
    private final MutableLiveData<Word> liveLessonEntranceInfo;

    public LessonEntranceDialogViewModel(@NonNull Application application) {
        super(application);
        // this should be initialized in order to avoid null on getValue for live data
        liveLessonEntranceInfo = new MutableLiveData<>(new Word(0, 0, 0,
                false, new Translation("",""),""));
    }

    public void setLiveLessonInfo(LessonEntrance lessonEntrance){
        liveLessonEntranceInfo.setValue((Word)lessonEntrance);
    }
}
