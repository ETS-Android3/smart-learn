package com.smart_learn.presenter.view_models;

import android.app.Application;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.core.services.LessonService;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.presenter.activities.dialogs.DialogDismissCallback;

public class OpenLessonViewModel extends AndroidViewModel {

    private final LessonService lessonService;

    // Leave this with no initial value. If you put an initial value it will trigger setValue().
    private final MutableLiveData<String> liveToastMessage = new MutableLiveData<>();

    public OpenLessonViewModel(@NonNull Application application) {
        super(application);
        lessonService = new LessonService(application);
    }

    private boolean lessonDetailsCheck(String lessonName){

        if (lessonName.isEmpty()) {
            liveToastMessage.setValue("Enter a name");
            return false;
        }

        /* TODO: check name length
        if (lessonName.length() > DatabaseSchemaK.LessonsTable.DIMENSION_COLUMN_NAME) {
            liveToastMessage.setValue("This name is too big. Choose a shorter name.");
            return false;
        }
         */

        // add lesson only if this does not exist
        if (lessonService.checkIfLessonExist(lessonName)) {
            liveToastMessage.setValue("Lesson " + lessonName + " already exists. Choose other name");
            return false;
        }

        return true;
    }

    public void processLessonDialog(EditText etLessonName, DialogDismissCallback dialogDismissCallback){

        if(etLessonName == null){
            Log.e(Logs.UNEXPECTED_ERROR, Logs.FUNCTION + "[processLessonDialog] etLessonName is null");
            return;
        }

        String lessonName = etLessonName.getText().toString().trim();

        // make some general checks
        if(!lessonDetailsCheck(lessonName)){
            return;
        }

        // lesson name is valid
        Lesson newLesson = new Lesson(lessonName,System.currentTimeMillis(),System.currentTimeMillis());

        // add in database
        lessonService.insert(newLesson);

        // close dialog
        dialogDismissCallback.onDismiss();

        // This update should be made automatically while using LiveData
        /*
        // update recycler view with lesson from database (need update for primary key)

        if(updateRecyclerView){
            val updatedLesson = applicationServiceK.lessonServiceK.getSampleLiveLesson(lessonName)
            if(updatedLesson != null && lessonsRVAdapter != null) {
                lessonsRVAdapter.insertItem(updatedLesson)
            }
        }
        */

    }

    public LiveData<String> getLiveToastMessage() {
        return liveToastMessage;
    }

}
