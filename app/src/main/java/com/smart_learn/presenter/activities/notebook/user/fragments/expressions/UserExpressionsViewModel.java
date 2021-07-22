package com.smart_learn.presenter.activities.notebook.user.fragments.expressions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.UserExpressionService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.expressions.ExpressionsViewModel;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.adapters.expressions.UserExpressionsAdapter;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class UserExpressionsViewModel extends ExpressionsViewModel<UserExpressionsAdapter> {

    @Getter
    @Setter
    private DocumentSnapshot currentLessonSnapshot;
    private final AtomicBoolean isDeletingActive;

    public UserExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
        currentLessonSnapshot = UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED;
        isDeletingActive = new AtomicBoolean(false);
    }

    @Override
    public void addExpression(String expressionValue, String notes, ArrayList<Translation> translations) {
        if(currentLessonSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_adding_expression));
            return;
        }

        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add(expressionValue);

        ExpressionDocument newExpression = new ExpressionDocument(
                new DocumentMetadata(
                        UserService.getInstance().getUserUid(),
                        System.currentTimeMillis(),
                        CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues)),
                notes,
                false,
                "",
                Translation.fromListToJson(translations),
                expressionValue
        );

        UserExpressionService.getInstance().addExpression(currentLessonSnapshot, newExpression, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_adding_expression));
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_adding_expression));
            }
        });
    }



    protected void deleteSelectedExpressions(){
        if(isDeletingActive.get()){
            return;
        }
        isDeletingActive.set(true);

        if(currentLessonSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_expressions));
            Timber.w("currentLessonSnapshot is not set");
            isDeletingActive.set(false);
            return;
        }

        if(adapter == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_expressions));
            Timber.w("adapter is null");
            isDeletingActive.set(false);
            return;
        }

        if(adapter.getSelectedValues().isEmpty()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.no_selected_expression));
            isDeletingActive.set(false);
            return;
        }

        UserExpressionService.getInstance().deleteExpressionList(currentLessonSnapshot, adapter.getSelectedValues(), new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_deleting_expressions));
                if (adapter != null) {
                    adapter.resetSelectedItems();
                }
                isDeletingActive.set(false);
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_deleting_expressions));
                isDeletingActive.set(false);
            }
        });
    }
}
