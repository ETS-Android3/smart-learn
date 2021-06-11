package com.smart_learn.presenter.activities.notebook.old;

import android.app.Application;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.LessonService;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.databinding.DialogNewLessonBinding;
import com.smart_learn.presenter.helpers.dialogs.DialogActionsCallback;
import com.smart_learn.presenter.helpers.dialogs.DialogDismissCallback;
import com.smart_learn.presenter.helpers.dialogs.LessonDialog;

/** https://developer.android.com/codelabs/android-room-with-a-view#9 */
public class BasicLessonViewModel extends AndroidViewModel {

    protected final LessonService lessonService;

    // Leave this with no initial value. If you put an initial value it will trigger setValue().
    private final MutableLiveData<String> liveToastMessage = new MutableLiveData<>();

    public BasicLessonViewModel(@NonNull Application application) {
        super(application);
        lessonService = new LessonService(application);
    }

    public LiveData<String> getLiveToastMessage() {
        return liveToastMessage;
    }

    private void processLessonDialog(Lesson lesson, boolean update, DialogDismissCallback dialogDismissCallback){
        ResponseInfo responseInfo = lessonService.tryToAddOrUpdateNewLesson(lesson, update);

        if(!responseInfo.isOk()){
            liveToastMessage.setValue(responseInfo.getInfo());
            return;
        }

        // here notebook was added so close the dialog
        dialogDismissCallback.onDismiss();
    }

    public DialogFragment prepareLessonDialog(boolean update, final Lesson lesson){

        int title = R.string.add_lesson;
        if(update){
            title = R.string.update_lesson;
        }

        return new LessonDialog(lesson,true, title, R.string.save, R.string.cancel, new DialogActionsCallback<DialogNewLessonBinding>() {

            @Override
            public void onShowDialog(DialogInterface dialogInterface, DialogNewLessonBinding dialogBinding) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onPositiveButtonPressed(dialogInterface,dialogBinding);
                    }
                });
            }

            @Override
            public void onPositiveButtonPressed(DialogInterface dialog, DialogNewLessonBinding dialogBinding) {
                // get notebook from data binding and view model
                Lesson dialogViewModelLesson = dialogBinding.getViewModel().getLiveLessonInfo().getValue();
                if(dialogViewModelLesson == null){
                    Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[dialogViewModelLesson] Lesson is null");
                    return;
                }

                Lesson tmpLesson;
                if(!update){
                    // If no update is made notebook from data binding and view model
                    // will be added if current configuration will be valid.
                    tmpLesson = dialogViewModelLesson;
                }
                else{
                    // Create a copy for dialogViewModelLesson such that after update , db will automatically
                    // notify recycler view adapter in order to change item.
                    tmpLesson = new Lesson(dialogViewModelLesson.getName(),dialogViewModelLesson.getCreatedAt(),
                            dialogViewModelLesson.getModifiedAt(),dialogViewModelLesson.isSelected());
                    tmpLesson.setLessonId(dialogViewModelLesson.getLessonId());
                }

                processLessonDialog(tmpLesson, update, new DialogDismissCallback() {
                    @Override
                    public void onDismiss() {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

}
