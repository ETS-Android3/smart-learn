package com.smart_learn.presenter.view_models;

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
import com.smart_learn.core.services.WordService;
import com.smart_learn.core.utilities.Logs;
import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.data.models.room.entities.helpers.LessonEntrance;
import com.smart_learn.data.models.room.entities.helpers.ResponseInfo;
import com.smart_learn.databinding.DialogAddLessonWordBinding;
import com.smart_learn.presenter.activities.dialogs.DialogActionsCallback;
import com.smart_learn.presenter.activities.dialogs.DialogDismissCallback;
import com.smart_learn.presenter.activities.dialogs.LessonEntranceDialog;

public class BasicLessonEntranceViewModel extends AndroidViewModel {

    protected final WordService wordService;

    // Leave this with no initial value. If you put an initial value it will trigger setValue().
    private final MutableLiveData<String> liveToastMessage = new MutableLiveData<>();

    public BasicLessonEntranceViewModel(@NonNull Application application) {
        super(application);
        wordService = new WordService(application);
    }

    public LiveData<String> getLiveToastMessage() {
        return liveToastMessage;
    }

    private void processLessonDialog(LessonEntrance lessonEntrance, boolean update, DialogDismissCallback dialogDismissCallback){
        ResponseInfo responseInfo = wordService.tryToAddOrUpdateNewWord((Word)lessonEntrance, update);

        if(!responseInfo.isOk()){
            liveToastMessage.setValue(responseInfo.getInfo());
            return;
        }

        // here lesson was added so close the dialog
        dialogDismissCallback.onDismiss();
    }

    public DialogFragment prepareEntranceDialog(boolean update, final LessonEntrance lessonEntrance, long currentLessonId){
        int title = R.string.add_entrance;
        if(update){
            title = R.string.update_entrance;
        }

        return new LessonEntranceDialog(lessonEntrance,true, title, R.string.save, R.string.cancel,
                new DialogActionsCallback<DialogAddLessonWordBinding>() {

            @Override
            public void onShowDialog(DialogInterface dialogInterface, DialogAddLessonWordBinding dialogBinding) {
                Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onPositiveButtonPressed(dialogInterface,dialogBinding);
                    }
                });
            }

            @Override
            public void onPositiveButtonPressed(DialogInterface dialog, DialogAddLessonWordBinding dialogBinding) {
                // get lesson entrance from data binding and view model
                LessonEntrance dialogLessonEntrance = dialogBinding.getViewModel().getLiveLessonEntranceInfo().getValue();
                if(dialogLessonEntrance == null){
                    Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[dialogViewModelLessonEntrance] LessonEntrance is null");
                    return;
                }

                // link entrance with current lesson
                dialogLessonEntrance.setFkLessonId(currentLessonId);

                LessonEntrance tmpLesson;
                if(!update){
                    // If no update is made lesson entrance from data binding and view model
                    // will be added if current configuration will be valid.
                    tmpLesson = dialogLessonEntrance;
                }
                else{
                    // Create a copy for dialogViewModelLessonEntrance such that after update , db will automatically
                    // notify recycler view adapter in order to change item.
                    tmpLesson = new Word(dialogLessonEntrance.getCreatedAt(),dialogLessonEntrance.getModifiedAt(),
                            dialogLessonEntrance.getFkLessonId(),dialogLessonEntrance.isSelected(),
                            dialogLessonEntrance.getTranslation(),((Word)dialogLessonEntrance).getWord());

                    ((Word)tmpLesson).setWordId(((Word)dialogLessonEntrance).getWordId());
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
